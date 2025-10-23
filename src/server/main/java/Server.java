import init.EnvironmentVariables;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static int PORT;
    public static String HOST;

    public Server() {
        EnvironmentVariables.loadEnvironmentVariables();

        PORT = Integer.parseInt(System.getProperty("SERVER_PORT"));
        HOST = System.getProperty("SERVER_HOST");
    }

    public static void main(String[] args) {
        new Server().init();
    }

    private void init(){
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor = Executors.newCachedThreadPool()){

            System.out.println("Escuchando en el puerto: " + PORT);
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
