package file;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ApplicationProperty {

    // Keys: API_KEY, API_READ_ACCESS_TOKEN, PORT, HOST
    private static HashMap<String, String> properties;

    private static final String PROPERTIES_FILENAME = "properties.txt";
    private static final String APPLICATION_NAME = "MyMovieList";

    private static String host;
    private static Integer port;

    private ApplicationProperty(){}

    public static HashMap<String, String> getProperties(){
        if (properties == null){
            try {
                Path filePath = Path.of(PROPERTIES_FILENAME);
                properties = new HashMap<>();

                if (Files.notExists(filePath)) {
                    createDefaultPropertiesFile();
                } else {
                    for(String line : Files.readAllLines(filePath)){
                        if (line.startsWith("#"))
                            continue;
                        String[] aux = line.split("=");
                        properties.put(aux[0], aux[1]);
                    }

                    host = properties.getOrDefault("HOST", "localhost");
                    port = Integer.valueOf(properties.getOrDefault("PORT", "7776"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }

    private static void createDefaultPropertiesFile() throws IOException {
        host = "localhost";
        port = 7776;

        properties.put("HOST", host);
        properties.put("PORT", String.valueOf(port));

        List<String> applicationProperties = Arrays.asList(
                "# MyMovieList Properties",
                "HOST=" + host,
                "PORT=" + port,
                "LANGUAGE=en-GB"
        );

        Path filePath = Path.of(PROPERTIES_FILENAME);
        Files.write(filePath, applicationProperties, StandardOpenOption.CREATE);
        JOptionPane.showMessageDialog(
                null,
                "Se ha detectado que no existía un archivo de propiedades de la " +
                        "aplicación, así que se ha creado uno con valores por defecto. " +
                        "Modifique dichos valores si lo ve necesario en: " +
                filePath.toAbsolutePath(),
                "Archivo creado por la aplicación",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static String getHost() {
        return host;
    }

    public static Integer getPort() {
        return port;
    }

    public static String getApplicationName(){
        return APPLICATION_NAME;
    }
}
