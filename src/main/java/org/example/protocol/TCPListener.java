package org.example.protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListener extends Listener {
    private ServerSocket serverSocket;
    public TCPListener() throws IOException {
        serverSocket = new ServerSocket(3000);
    }
    @Override
    void listen() {
        try {
            while (isRunning) {
                Socket socket = serverSocket.accept();//accepts new connections
                new Thread(new TCPServerConnection(socket)).start();
            }
        } catch (IOException e) {

        }
    }
}
