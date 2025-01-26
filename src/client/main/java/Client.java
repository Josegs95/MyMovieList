import io.SocketCommunication;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final private Integer PORT = 7776;
    final private String HOST = "localhost";

    public static void main(String[] args) {
        new Client().init();
    }

    private void init(){
        try (Socket socket = new Socket(HOST, PORT);
             SocketCommunication socketCommunication = new SocketCommunication(socket);
             Scanner sc = new Scanner(System.in)) {

            String userInput = "";
            while(!userInput.equals("bye")){
                userInput = sc.nextLine();
                socketCommunication.writeStringToSocket(userInput);
                System.out.println("Servidor: " + socketCommunication.readStringFromSocket());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
