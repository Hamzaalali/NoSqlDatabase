package org.example.server;
import org.example.authentication.AuthenticationManager;
import org.example.cluster.ClusterManager;
import org.example.node.to.node.NodeToNodeSenderReceiver;
import org.example.database.DatabaseFacade;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
        new Thread(new TcpListener()).start();
        try{
            System.out.println("udp listener start");
            new Thread(new UdpListener()).start();
        }catch (Exception e){
            System.out.println(e.getMessage());
            new RuntimeException(e);
        }
    }
    private void initialize(){
        try{
            NodeToNodeSenderReceiver nodeToNodeSenderReceiver =new NodeToNodeSenderReceiver();
            DatagramPacket packet;
            JSONObject routine=new JSONObject();
            routine.put("routineType", UdpRoutineTypes.INIT.toString());
            nodeToNodeSenderReceiver.sendMessage(routine.toJSONString());
            JSONParser jsonParser=new JSONParser();
            packet =nodeToNodeSenderReceiver.receiveMessage();
            JSONObject initlializeObject=(JSONObject) jsonParser.parse(new String(packet.getData(), 0, packet.getLength()));
            System.out.println(initlializeObject);
            JSONArray users= (JSONArray) initlializeObject.get("users");
            for(int i=0;i<users.size();i++){
                JSONObject user=(JSONObject)users.get(i);
                AuthenticationManager.getInstance().addUser((String) user.get("username"), (String) user.get("password"));
            }
            JSONArray nodes= (JSONArray) initlializeObject.get("ports");
            ClusterManager.getInstance().setNodes(nodes);
            ClusterManager.getInstance().setTcpPort((long) initlializeObject.get("tcpPort"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private class TcpListener implements Runnable{
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(3000);
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("New Connection At Port : "+socket.getPort());
                    new Thread(new ServerConnection(socket,databaseFacade)).start();
                }
            } catch (IOException e) {

            }
        }
    }
    private class UdpListener implements Runnable{
        private DatagramSocket udpSocket;
        private byte[] buf = new byte[1024];
        public UdpListener() throws SocketException {
            udpSocket = new DatagramSocket(4000);
        }
        @Override
        public void run() {
            try {
                System.out.println("waiting for udp");
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                udpSocket.receive(packet);
                String received
                        = new String(packet.getData(), 0, packet.getLength());
                System.out.println("from udp listener"+received);
                UdpManager.getInstance().execute(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);
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