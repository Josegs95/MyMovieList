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

    final static private String PROPERTIES_FILENAME = "properties.txt";
    final static private String APPLICATION_NAME = "MyMovieList";

    private static String HOST;
    private static Integer PORT;

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

                    HOST = properties.getOrDefault("HOST", "localhost");
                    PORT = Integer.valueOf(properties.getOrDefault("PORT", "7776"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }

    private static void createDefaultPropertiesFile() throws IOException {
        HOST = "localhost";
        PORT = 7776;

        properties.put("HOST", HOST);
        properties.put("PORT", String.valueOf(PORT));

        List<String> applicationProperties = Arrays.asList(
                "# MyMovieList Properties",
                "HOST=" + HOST,
                "PORT=" + PORT,
                "LANGUAGE=en-GB"
        );

        Path filePath = Path.of(PROPERTIES_FILENAME);
        Files.write(filePath, applicationProperties, StandardOpenOption.CREATE);
        JOptionPane.showMessageDialog(
                null,
                "Se ha detectado que no existía un archivo de propiedades de la aplicación, así que se ha" +
                        "creado uno con valores por defecto.\nModifique dichos valores si lo ve necesario en: " +
                filePath.toAbsolutePath(),
                "Archivo creado por la aplicación",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static String getHOST() {
        return HOST;
    }

    public static Integer getPORT() {
        return PORT;
    }

    public static String getApplicationName(){
        return APPLICATION_NAME;
    }
}
