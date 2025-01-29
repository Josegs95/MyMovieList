import io.SocketCommunication;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable{
    final private Socket SOCKET;

    public ClientHandler(Socket socket){
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.SOCKET = socket;
    }

    @Override
    public void run() {
        try(SocketCommunication socketCommunication = new SocketCommunication(SOCKET)){
            String clientMessage = "";
            Long id = Thread.currentThread().threadId();
            while(!clientMessage.equals("bye")){
                clientMessage = socketCommunication.readStringFromSocket();
                System.out.println("Cliente " + id + ": " + clientMessage);
                socketCommunication.writeStringToSocket("Recibido");
            }
        } catch (SocketException e){}
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
