import config.ApiConfiguration;
import config.HibernateUtil;
import init.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static int PORT;
    public static String HOST;

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public Server() {
        EnvironmentVariables.loadEnvironmentVariables();

        PORT = Integer.parseInt(System.getProperty("SERVER_PORT"));
        HOST = System.getProperty("SERVER_HOST");
    }

    public static void main(String[] args) {
        new Server().init();
    }

    private void init(){
        createDatabaseConnection();

        try (ServerSocket serverSocket = new ServerSocket(PORT); ExecutorService executor = Executors.newCachedThreadPool()){
            executor.execute(ApiConfiguration::load);

            LOGGER.info("Escuchando en el puerto: {}", PORT);
            while(true){
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Ha llegado un cliente");
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private void createDatabaseConnection() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown hook activado. Cerrando Hibernate...");
            HibernateUtil.shutdown();
        }));

        HibernateUtil.load();
    }
}
