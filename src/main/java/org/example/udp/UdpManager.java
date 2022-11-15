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
    public DatagramPacket execute(DatagramPacket packet) throws ParseException {
        String received
                = new String(packet.getData(), 0, packet.getLength());
        JSONParser jsonParser=new JSONParser();
        JSONObject routine= (JSONObject) jsonParser.parse(received);
        System.out.println(received);
        System.out.println(packet.getPort());
        UdpRoutineTypes routineType= UdpRoutineTypes.valueOf((String) routine.get("routineType"));
        return routineMap.get(routineType).execute(packet,routine);
    }
    public static UdpManager getInstance() {
        if (instance == null) {
            instance = new UdpManager();
        }
        return instance;
    }
    public void broadcast(int port,String data) throws IOException {
        DatagramSocket socket=new DatagramSocket();
        System.out.println(ClusterManager.getInstance().getBroadcastIp());
        DatagramPacket packet=new DatagramPacket(data.getBytes(),data.getBytes().length, InetAddress.getByName(ClusterManager.getInstance().getBroadcastIp()),port);
        socket.send(packet);
        System.out.println("send broad cast at "+packet.getAddress().getHostAddress()+":"+port);
    }
    public void sendToBootstrapper(String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName("host.docker.internal"), ClusterManager.getInstance().getBootstrapperPort());
        socket.send(packet);
    }
}
