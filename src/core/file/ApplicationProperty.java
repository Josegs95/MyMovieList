package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ApplicationProperty {

    // Keys: API_KEY, API_READ_ACCESS_TOKEN, PORT, HOST
    private static HashMap<String, String> properties;

    final static private String PROPERTIES_FILENAME = "properties.txt";

    public static HashMap<String, String> getProperties(){
        if (properties == null){
            Path filePath = Path.of(PROPERTIES_FILENAME);
            if (!Files.isRegularFile(filePath)) {
                System.out.printf("El archivo %s no existe o no es válido", filePath.toAbsolutePath());
                return null;
            }

            properties = new HashMap<>();
            try {
                for(String line : Files.readAllLines(filePath)){
                    if (line.startsWith("#"))
                        continue;
                    String[] aux = line.split("=");
                    properties.put(aux[0], aux[1]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }
}
