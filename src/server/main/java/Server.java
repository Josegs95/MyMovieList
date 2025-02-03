import file.ApplicationProperty;
import io.SocketCommunication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final private Map<String, String> PROPERTIES;

    public Server(){
        PROPERTIES = ApplicationProperty.getProperties();
    }

    public static void main(String[] args) {
        new Server().init();
    }

    private void init(){
        if (PROPERTIES == null)
            return;
        int port = Integer.valueOf(PROPERTIES.get("PORT"));
        try (ServerSocket serverSocket = new ServerSocket(port)){
            ExecutorService executor = Executors.newCachedThreadPool();

            System.out.println("Listening on port: " + port);
            boolean endApp = false;
            while(!endApp){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ha llegado un cliente");
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean processSocket(Socket socket) {
        try(SocketCommunication socketCommunication = new SocketCommunication(socket)){
            String clientMessage = "";
            while(!clientMessage.equals("bye")){
                clientMessage = socketCommunication.readStringFromSocket();
                System.out.println("Cliente: " + clientMessage);
                socketCommunication.writeStringToSocket("Recibido");
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
