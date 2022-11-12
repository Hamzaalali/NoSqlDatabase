package org.example.server;

import org.example.authentication.AuthenticationManager;
import org.example.node.to.node.NodeToNodeReceiver;
import org.example.node.to.node.NodeToNodeSenderReceiver;
import org.example.database.DatabaseFacade;

import org.example.server_client.ServerClientCommunicator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;


public class Server implements Runnable{
   private DatabaseFacade databaseFacade;
   public Server(){
       databaseFacade=new DatabaseFacade();
   }

    @Override
    public void run() {
        initialize();
        listenForConnection();
    }
    private void initialize(){
        try{
//            NodeToNodeReceiver nodeToNodeReceiver=new NodeToNodeReceiver();
//            new Thread(nodeToNodeReceiver).start();
            NodeToNodeSenderReceiver nodeToNodeSenderReceiver =new NodeToNodeSenderReceiver();
            DatagramPacket packet=nodeToNodeSenderReceiver.receiveMessage();
            nodeToNodeSenderReceiver.sendMessage("i\'m up",packet.getAddress(), packet.getPort());
            JSONParser jsonParser=new JSONParser();
            packet =nodeToNodeSenderReceiver.receiveMessage();
            JSONArray jsonArray= (JSONArray) jsonParser.parse(new String(packet.getData(), 0, packet.getLength()));
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                AuthenticationManager.getInstance().addUser((String) jsonObject.get("username"), (String) jsonObject.get("password"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void listenForConnection(){
        System.out.println("wt");
        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            while (true) {
                System.out.println("wt");
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
           authenticate();
           getUserQueries();
        }
        private void getUserQueries(){
            JSONObject clientMessage;
            while(true){
                try {
                    JSONObject query= ServerClientCommunicator.readJson(socket);
                    System.out.println(query);
                    clientMessage=databaseFacade.execute(query);
                    ServerClientCommunicator.sendJson(socket,clientMessage);
                } catch (IOException e ) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void authenticate(){
            boolean isAuthenticated=false;
            JSONObject clientMessage;
            while(!isAuthenticated){
                try{
                    JSONObject user= ServerClientCommunicator.readJson(socket);
                    isAuthenticated=AuthenticationManager.getInstance().authenticate((String) user.get("username"), (String) user.get("password"));
                    clientMessage=new JSONObject();
                    if(isAuthenticated){
                        clientMessage.put("code_number",0);
                    }else{
                        clientMessage.put("code_number",1);
                        clientMessage.put("error_message","Invalid credentials");
                    }
                    ServerClientCommunicator.sendJson(socket,clientMessage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


class MainServer {
    public static void main(String[] args)  {
        Server server=new Server();
        new Thread(server).start();
    }
}