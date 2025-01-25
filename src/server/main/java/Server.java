import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final private Integer PORT = 7776;

    public static void main(String[] args) {
        new Server().init();
    }

    private void init(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            boolean endApp = false;
            while(!endApp){
                Socket clientSocket = serverSocket.accept();
                endApp = processSocket(clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean processSocket(Socket socket) {
        return true;
    }
}
