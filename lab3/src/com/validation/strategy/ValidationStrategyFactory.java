package com.validation.strategy;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ValidationStrategyFactory {

    // mapa adnotacji - strategia
    private static final Map<Class<? extends Annotation>,
            ValidationStrategy> strategies = new HashMap<>();

    static {
        try {
            // nazwa pakietu -> ścieżka
            String packageName = "com.validation.strategy";
            String path = packageName.replace('.', '/');

            // pobranie classloadera aplikacji
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Szukanie katalogu z klasami strategii
            URL packageURL = classLoader.getResource(path);

            if (packageURL != null) {
                File folder = new File(packageURL.toURI());

                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName();

                        // ignorowanie klas z $ w nazwie
                        if (fileName.endsWith(".class") && !fileName.contains("$")) {
                            String className = packageName + "." +
                                    fileName.replace(".class", "");

                            try {
                                // ładowanie klasy
                                Class<?> clazz = Class.forName(className);

                            } catch (Exception e) {
                                System.err.println("Błąd przy przetwarzaniu klasy: " + className);
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    System.err.println("Nie udało się odczytać plików z folderu: "
                            + folder.getAbsolutePath());
                }
            } else {
                System.err.println("Nie znaleziono ścieżki pakietu: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ValidationStrategyFactory() {
        // konstruktor prywatny nie pozwala na instancjonowanie klasy
    }

    public static ValidationStrategy getStrategy(Annotation annotation) {
        return strategies.get(annotation.annotationType());
    }

    public static Map<Class<? extends Annotation>, ValidationStrategy> getStrategiesSnapshot() {
        return Map.copyOf(strategies);
    }
}
