import controller.APIController;
import file.ApplicationProperty;
import io.SocketCommunication;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public class Client {
    final private Map<String, String> PROPERTIES;

    public Client(){
        PROPERTIES = ApplicationProperty.readPropertiesFromFile(Path.of("properties.txt"));
    }

    public static void main(String[] args) {
        new Client().init();
    }

    private void init(){
        if (PROPERTIES == null)
            return;

//        try (Socket socket = new Socket(PROPERTIES.get("HOST"), Integer.valueOf(PROPERTIES.get("PORT")));
//             SocketCommunication socketCommunication = new SocketCommunication(socket);
//             Scanner sc = new Scanner(System.in)) {
//
//            String userInput = "";
//            while(!userInput.equals("bye")){
//                userInput = sc.nextLine();
//                socketCommunication.writeStringToSocket(userInput);
//                System.out.println("Servidor: " + socketCommunication.readStringFromSocket());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try{
            Map data = APIController.searchMovie("Batman", PROPERTIES.get("API_READ_ACCESS_TOKEN"));
            System.out.println(data);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
