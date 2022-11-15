package org.example.udp.routine;

import org.example.authentication.AuthenticationManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.DatagramPacket;


public class AddUserRoutine extends UdpRoutine{

    @Override
    public DatagramPacket execute(DatagramPacket packet,JSONObject routineJson) {
        String password= (String) routineJson.get("password");
        String username= (String) routineJson.get("username");
        AuthenticationManager.getInstance().addUser(username,password);
        packet = new DatagramPacket("".toString().getBytes(), "".toString().getBytes().length, packet.getAddress(), packet.getPort());
        return packet;
    }
}
