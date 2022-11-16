package org.example.server;
import org.example.cluster.ClusterManager;
import org.example.tcp.ServerConnection;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.net.*;
import java.util.Objects;

public class Server implements Runnable{
    @Override
    public void run() {
        new Thread(new TcpListener()).start();
        try{
            System.out.println("udp listener start");
            new Thread(new UdpListener()).start();
            Thread.sleep(1000);
            sendInitializeMessage();
        }catch (Exception e){
            System.out.println(e.getMessage());
            new RuntimeException(e);
        }
    }
    private void sendInitializeMessage(){
        try{
            JSONObject routine=new JSONObject();
            routine.put("routineType", UdpRoutineTypes.INIT.toString());
            UdpManager.getInstance().sendToBootstrapper((routine.toJSONString()));
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
                    new Thread(new ServerConnection(socket)).start();
                }
            } catch (IOException e) {

            }
        }
    }
    private class UdpListener implements Runnable{
        private DatagramSocket udpSocket;
        private byte[] buf = new byte[1024];
        public UdpListener() throws SocketException {
            udpSocket = new DatagramSocket(ClusterManager.getInstance().getUdpPort());
        }
        @Override
        public void run() {
            try {
                while (true){
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    udpSocket.receive(packet);
                    if(Objects.equals(packet.getAddress().getHostAddress(),InetAddress.getLocalHost().getHostAddress())){
                        continue;
                    }
                    UdpManager.getInstance().execute(packet);
                }
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