package org.example.server;
import org.example.authentication.AuthenticationManager;
import org.example.cluster.ClusterManager;
import org.example.database.DatabaseQueryManager;
import org.example.file.system.DiskOperations;
import org.example.load.balance.LoadBalancer;
import org.example.server_client.ServerClientCommunicator;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.Socket;
public class ServerConnection implements Runnable{
        private Socket socket;
        private String authUsername;
        private String authPassword;
        boolean isRunning;
        public ServerConnection(Socket socket) throws IOException {
            this.socket = socket;
            isRunning=true;
        }
        @Override
        public void run() {
           authenticate();
           getUserQueries();
        }
        private void getUserQueries(){
            JSONObject clientMessage;
            while(isRunning){
                try {
                    JSONObject query= ServerClientCommunicator.readJson(socket);
                    System.out.println(query);
                    if(!LoadBalancer.getInstance().addRequest()){
                        broadcastUser();
                        isRunning=false;
                        clientMessage=redirectMessage();
                    }else{
                        DiskOperations.appendToFile("logs.json", query.toJSONString());
                        clientMessage= DatabaseQueryManager.getInstance().execute(query);
                        UdpManager.getInstance().broadcastQuery(4000,query);
                    }
                    System.out.println(clientMessage);
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
                    authUsername=(String) user.get("username");
                    authPassword=(String) user.get("password");
                    isAuthenticated= AuthenticationManager.getInstance().authenticate(authUsername, authPassword);
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
        private void broadcastUser() throws IOException {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("password",authPassword);
            jsonObject.put("username",authUsername);
            jsonObject.put("routineType", UdpRoutineTypes.ADD_USER.toString());
            UdpManager.getInstance().broadcast(4000,jsonObject.toJSONString());
        }
        private JSONObject redirectMessage(){
            JSONObject redirectMessage=new JSONObject();
            redirectMessage.put("code_number",2);
            redirectMessage.put("ports", ClusterManager.getInstance().getNodesPorts());
            redirectMessage.put("thisTcpPort",ClusterManager.getInstance().getTcpPort());
            return redirectMessage;
        }
}