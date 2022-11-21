package org.example.server;
import org.example.commands.CommandTypes;
import org.example.protocol.TCPListener;
import org.example.protocol.UDPCommunicator;
import org.example.protocol.UDPListener;
import org.json.simple.JSONObject;

public class Server implements Runnable{
    @Override
    public void run() {
        try {
            new Thread(new TCPListener()).start();
            new Thread(new UDPListener()).start();
            Thread.sleep(1000);
            sendInitializeMessage();
        }catch (Exception e){
            new RuntimeException(e);
        }
    }
    private void sendInitializeMessage(){
        try{
            JSONObject commandJson=new JSONObject();
            commandJson.put("commandType", CommandTypes.INITIALIZE.toString());
            UDPCommunicator.sendToBootstrapper((commandJson.toJSONString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
class MainServer {
    public static void main(String[] args)  {
        Server server=new Server();
        new Thread(server).start();
    }
}