import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Main {

    // Parametry zadania
    private static final int NUM_CONSUMERS = 2;
    private static final int QUEUE_CAPACITY = NUM_CONSUMERS; // ograniczenie kolejki
    private static final int WORDS_LIMIT = 10; // liczba najczęściej występujących słów
    private static final Path ROOT_DIR = Paths.get("files"); // katalog z plikami

    // Kolejka blokująca przechowująca obiekty Optional<Path>
    private static final BlockingQueue<Optional<Path>> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    // Flaga sterująca pracą producenta (można użyć przy zatrzymywaniu)
    private static final AtomicBoolean stopFlag = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Tworzymy pulę wątków: 1 producent + NUM_CONSUMERS konsumentów
        ExecutorService executor = Executors.newFixedThreadPool(1 + NUM_CONSUMERS);

        // Uruchamiamy producenta
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " – PRODUCENT uruchomiony.");
            try {
                // Przechodzimy przez drzewo katalogów
                Files.walkFileTree(ROOT_DIR, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Sprawdzamy, czy plik ma rozszerzenie .txt
                        if (file.toString().toLowerCase().endsWith(".txt")) {
                            try {
                                // Umieszczamy ścieżkę opakowaną w Optional w kolejce
                                queue.put(Optional.of(file));
                                System.out.println(threadName + " – dodano: " + file);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return FileVisitResult.TERMINATE;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Po zakończeniu wyszukiwania wysyłamy poison pills do konsumentów
            for (int i = 0; i < NUM_CONSUMERS; i++) {
                try {
                    queue.put(Optional.empty());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(threadName + " – PRODUCENT zakończył pracę.");
        });

        // Uruchamiamy konsumentów
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " – KONSUMENT uruchomiony.");
                while (true) {
                    try {
                        Optional<Path> optPath = queue.take();
                        // Jeżeli otrzymamy poison pill, kończymy pracę konsumenta
                        if (!optPath.isPresent()) {
                            System.out.println(threadName + " – otrzymano poison pill. Zakończenie pracy.");
                            break;
                        }
                        Path file = optPath.get();
                        // Obliczamy statystykę słów dla pliku
                        Map<String, Long> stats = getLinkedCountedWords(file, WORDS_LIMIT);
                        System.out.println(threadName + " – przetworzono plik: " + file);
                        System.out.println("Top " + WORDS_LIMIT + " słów: " + stats);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (IOException e) {
                        System.err.println(threadName + " – błąd przy przetwarzaniu pliku: " + e.getMessage());
                    }
                }
                System.out.println(threadName + " – KONSUMENT zakończył pracę.");
            });
        }

        // Zamykamy pulę wątków
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    /**
     * Metoda zwraca najczęściej występujące słowa w pliku.
     * - Dzieli linie na słowa za pomocą wyrażenia regularnego.
     * - Usuwa znaki nie będące literami i cyframi.
     * - Konwertuje słowa na małe litery.
     * - Filtrowanie – tylko słowa o długości co najmniej 3 znaków.
     * - Grupuje słowa i liczy ich wystąpienia.
     * - Sortuje według liczby wystąpień malejąco.
     * - Zwraca wynik w LinkedHashMap (zachowuje kolejność).
     */
    private static Map<String, Long> getLinkedCountedWords(Path path, int wordsLimit) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return reader.lines()
                    // Podział linii na słowa (rozdzielamy według białych znaków)
                    .flatMap(line -> Arrays.stream(line.split("\\s+")))
                    // Usuwamy znaki specjalne (zostają tylko litery, cyfry oraz polskie znaki)
                    .map(word -> word.replaceAll("[^a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]", ""))
                    // Konwersja do małych liter
                    .map(String::toLowerCase)
                    // Filtrowanie – tylko słowa o długości co najmniej 3 znaków
                    .filter(word -> word.length() >= 3)
                    // Grupowanie słów i zliczanie ich wystąpień
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                    .entrySet().stream()
                    // Sortowanie według liczby wystąpień (malejąco)
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .limit(wordsLimit)
                    // Zbieranie wyników do LinkedHashMap, aby zachować kolejność sortowania
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        }
    }
}

