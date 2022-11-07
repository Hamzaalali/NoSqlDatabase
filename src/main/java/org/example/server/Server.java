package org.example.server;

import org.example.authentication.AuthenticationManager;
import org.example.database.DatabaseFacade;
import org.example.server_client.ServerClientCommunicator;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
   private DatabaseFacade databaseFacade;
   private AuthenticationManager authenticationManager;
   public Server(){
       databaseFacade=new DatabaseFacade();
   }
   public void run(){
       listenForConnection();
   }
    private void listenForConnection(){
        try {
            ServerSocket serverSocket = new ServerSocket(8085);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New Connection At Port : "+socket.getPort());
                new Thread(new ServerConnection(socket)).start();
            }
        } catch (IOException e) {

        }
    }
    public class ServerConnection implements Runnable{
        private Socket socket;
        public ServerConnection(Socket socket) throws IOException {
            this.socket = socket;
        }

        @Override
        public void run() {
            while(true){
                try {
                    JSONObject query=(JSONObject) ServerClientCommunicator.readObj(socket);
                    System.out.println(query);
                    databaseFacade.execute(query);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


class MainServer {
    public static void main(String[] args) {
        Server server=new Server();
        server.run();
    }
}