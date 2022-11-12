package org.example.node.to.node;

import java.io.IOException;
import java.net.*;

public class NodeToNodeSenderReceiver {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public NodeToNodeSenderReceiver() throws SocketException, UnknownHostException {
        socket = new DatagramSocket(4000);
        address = InetAddress.getByName("host.docker.internal");
        System.out.println( System.getenv("GATEWAY_IP"));
//        address=InetAddress.getByName(System.getenv("GATEWAY_IP"));
    }

    public void sendMessage(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, Integer.parseInt(System.getenv("BOOTSTRAPPER_PORT")));
        socket.send(packet);
    }
    public DatagramPacket receiveMessage() throws IOException {
        buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        System.out.println("from sender receiver");
        System.out.println(packet.getAddress());
        System.out.println(packet.getPort());
        System.out.println(received);
        return packet;
    }
    public void close() {
        socket.close();
    }
}