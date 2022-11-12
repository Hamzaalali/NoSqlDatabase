package org.example.node.to.node;

import java.io.IOException;
import java.net.*;

public class NodeToNodeReceiver implements Runnable {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public NodeToNodeReceiver() throws SocketException {
        socket = new DatagramSocket(4000);
    }
    @Override
    public void run() {
        running = true;
        try{
            while (running) {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received
                        = new String(packet.getData(), 0, packet.getLength());
                packet = new DatagramPacket(buf, buf.length, address, port);
                System.out.println("from receiver");
                System.out.println(received);
                System.out.println(packet.getPort());
                System.out.println(packet.getAddress());
                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                socket.send(packet);
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
