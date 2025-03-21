import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.swing.*;


public class Main {

    // Parametry zadania
    private static final int NUM_CONSUMERS = 2;
    private static final int QUEUE_CAPACITY = NUM_CONSUMERS; // ograniczenie kolejki
    private static final int WORDS_LIMIT = 10; // liczba najczęściej występujących słów
    private static final Path ROOT_DIR = Paths.get("files"); // katalog z plikami
    private static boolean STOP = false;

    // Kolejka blokująca przechowująca obiekty Optional<Path>
    private static final BlockingQueue<Optional<Path>> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {

        // Button
        JFrame frame = new JFrame("Button");
        JButton stopButton = new JButton("STOP");

        stopButton.addActionListener(e -> {
            STOP = true;
            stopButton.setText("STOP");
            System.out.println("STOP pressed");
        });

        frame.add(stopButton);
        frame.setSize(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Tworzymy pulę wątków: 1 producent + NUM_CONSUMERS konsumentów
        ExecutorService executor = Executors.newFixedThreadPool(1 + NUM_CONSUMERS);

        // Uruchamiamy producenta
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " – PRODUCENT uruchomiony.");
            while (!STOP) {
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
                while (!STOP) {
                    try {
                        Optional<Path> optPath = queue.take();
                        // Jeżeli otrzymamy poison pill, kończymy pracę konsumenta
                        if (optPath.isEmpty()) {
                            System.out.println(threadName + " – otrzymano poison pill. Zakończenie pracy.");
                            break;
                        }
                        Path file = optPath.get();
                        // Obliczamy statystykę słów dla pliku
                        Map<String, Long> stats = getLinkedCountedWords(file);
                        System.out.println(threadName + " – przetworzono plik: " + file);
                        System.out.println("Top " + WORDS_LIMIT + " słów: " + stats);

                        // Opóźnienie jeśli wątek nie został przerwany
                        if (!Thread.currentThread().isInterrupted()) {
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
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
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    // Metoda zliczająca i zwracająca ilość wyrazów
    private static Map<String, Long> getLinkedCountedWords(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return reader.lines()
                    // Podział linii na słowa
                    .flatMap(line -> Arrays.stream(line.split("\\s+")))
                    // Usuwamy znaki specjalne regexem
                    .map(word -> word.replaceAll("[^a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]", ""))
                    .map(String::toLowerCase)
                    .filter(word -> word.length() >= 3)
                    // Grupowanie słów i zliczanie ich wystąpień
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                    .entrySet().stream()
                    // Sortowanie według liczby wystąpień malejąco
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .limit(WORDS_LIMIT)
                    // Zbieranie wyników do LinkedHashMap, aby zachować kolejność sortowania
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1, // merge function ignoruje drugi element o tym samm kluczu
                            LinkedHashMap::new // wrzuca to wszystko do listy uwzględniającej kolejność
                    ));
        }
    }
}
