import file.ApplicationProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
        int port = Integer.parseInt(PROPERTIES.get("PORT"));
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newCachedThreadPool()){

            System.out.println("Escuchando en el puerto: " + port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ha llegado un cliente");
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
