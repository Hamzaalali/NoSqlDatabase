package org.example.udp.routine;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.net.DatagramPacket;

public abstract class UdpRoutine {

    public abstract void execute( JSONObject routineJson);
}
