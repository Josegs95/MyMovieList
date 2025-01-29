package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ApplicationProperty {

    public static HashMap<String, String> readPropertiesFromFile(Path filePath){
        if (!Files.isRegularFile(filePath)) {
            System.out.printf("El archivo %s no existe o no es válido", filePath.toAbsolutePath());
            return null;
        }

        HashMap<String, String> map = new HashMap<>();
        try {
            for(String line : Files.readAllLines(filePath)){
                if (line.startsWith("#"))
                    continue;
                String[] aux = line.split("=");
                map.put(aux[0], aux[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }
}
