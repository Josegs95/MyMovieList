package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketCommunication implements AutoCloseable{
    final private Socket SOCKET;
    private DataInputStream dis;
    private DataOutputStream dos;

    public SocketCommunication(Socket socket){
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.SOCKET = socket;
        try{
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStringToSocket(String message) throws IOException {
        dos.writeUTF(message);
    }

    public String readStringFromSocket() throws IOException {
        return dis.readUTF();
    }

    @Override
    public void close() throws IOException {
        dis.close();
        dos.close();
        SOCKET.close();
    }
}
