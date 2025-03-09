import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GenerateSampleFiles {
    public static void main(String[] args) {
        Path dir = Paths.get("files");
        try {
            // Jeśli katalog nie istnieje, tworzymy go
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
                System.out.println("Utworzono katalog: " + dir.toAbsolutePath());
            }

            // Plik 1
            Path file1 = dir.resolve("file1.txt");
            List<String> content1 = List.of(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            );
            Files.write(file1, content1);

            // Plik 2
            Path file2 = dir.resolve("file2.txt");
            List<String> content2 = List.of(
                    "Hello world! This is a sample text.",
                    "Hello again, hello!"
            );
            Files.write(file2, content2);

            // Plik 3
            Path file3 = dir.resolve("file3.txt");
            List<String> content3 = List.of(
                    "Java programming.",
                    "Lambdas and Streams make code concise and readable."
            );
            Files.write(file3, content3);

            // Plik 4
            Path file4 = dir.resolve("file4.txt");
            List<String> content4 = List.of(
                    "Wzorzec producent-konsument jest często stosowany w programowaniu równoległym.",
                    "Konsument przetwarza dane, a producent dostarcza pliki tekstowe do analizy."
            );
            Files.write(file4, content4);

            System.out.println("Przykładowe pliki zostały wygenerowane w katalogu: " + dir.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
