package org.example.udp;
import org.example.cluster.ClusterManager;
import org.example.udp.routine.RoutinesFactory;
import org.example.udp.routine.UdpRoutine;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.Map;

public class UdpManager {
    Map<UdpRoutineTypes, UdpRoutine> routineMap;
    private static UdpManager instance;

    private UdpManager(){
        RoutinesFactory routinesFactory=new RoutinesFactory();
        routineMap=routinesFactory.getRoutines();
    }
    public void execute(DatagramPacket packet) throws ParseException {
        String received
                = new String(packet.getData(), 0, packet.getLength());
        System.out.println("received :-"+ received);
        JSONParser jsonParser=new JSONParser();
        JSONObject routine= (JSONObject) jsonParser.parse(received);
        UdpRoutineTypes routineType= UdpRoutineTypes.valueOf((String) routine.get("routineType"));
        routineMap.get(routineType).execute(routine);
    }
    public static UdpManager getInstance() {
        if (instance == null) {
            instance = new UdpManager();
        }
        return instance;
    }
    public void sendUpdQuery(String ip,int port,JSONObject query,UdpRoutineTypes udpRoutineType) throws IOException {
        JSONObject routineJson=new JSONObject();
        routineJson.put("routineType",udpRoutineType.toString());
        routineJson.put("query",query);
        DatagramSocket socket=new DatagramSocket();
        System.out.println(ClusterManager.getInstance().getBroadcastIp());
        DatagramPacket packet=new DatagramPacket(routineJson.toJSONString().getBytes(),routineJson.toJSONString().getBytes().length, InetAddress.getByName(ip),port);
        socket.send(packet);
        System.out.println("sent upd at "+packet.getAddress().getHostAddress()+":"+port);
    }
    public void broadcast(int port,String data) throws IOException {
        DatagramSocket socket=new DatagramSocket();
        System.out.println(ClusterManager.getInstance().getBroadcastIp());
        DatagramPacket packet=new DatagramPacket(data.getBytes(),data.getBytes().length, InetAddress.getByName(ClusterManager.getInstance().getBroadcastIp()),port);
        socket.send(packet);
        System.out.println("sent broad cast at "+packet.getAddress().getHostAddress()+":"+port);
    }
    public void broadcastQuery(int port,JSONObject query) throws IOException {
        JSONObject routineJson=new JSONObject();
        routineJson.put("routineType",UdpRoutineTypes.SYNC.toString());
        routineJson.put("query",query);
        DatagramSocket socket=new DatagramSocket();
        System.out.println(ClusterManager.getInstance().getBroadcastIp());
        DatagramPacket packet=new DatagramPacket(routineJson.toJSONString().getBytes(),routineJson.toJSONString().getBytes().length, InetAddress.getByName(ClusterManager.getInstance().getBroadcastIp()),port);
        socket.send(packet);
        System.out.println("sent broad cast at "+packet.getAddress().getHostAddress()+":"+port);
    }
    public void sendToBootstrapper(String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName("host.docker.internal"), ClusterManager.getInstance().getBootstrapperPort());
        socket.send(packet);
    }
}
