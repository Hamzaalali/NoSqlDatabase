package org.example.tcp;
import org.example.authentication.AuthenticationManager;
import org.example.cluster.ClusterManager;
import org.example.file.system.DiskOperations;
import org.example.load.balance.RequestLoad;
import org.example.authentication.User;
import org.example.tcp.query.DatabaseQueryManager;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ServerConnection implements Runnable{
        private Socket socket;
        private User authenticatedUser;
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
                    if(!RequestLoad.getInstance().addRequest()){
                        broadcastUser();
                        isRunning=false;
                        clientMessage=redirectMessage();
                    }else{
                        DiskOperations.appendToFile("logs.json", query.toJSONString());
                        clientMessage= DatabaseQueryManager.getInstance().executeAndBroadcast(query);
                    }
                    ServerClientCommunicator.sendJson(socket,clientMessage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void authenticate() {
            JSONObject clientMessage=new JSONObject();
            try{
                while(true){
                    JSONObject userJson= ServerClientCommunicator.readJson(socket);
                    String username=(String) userJson.get("username");
                    String password=(String) userJson.get("password");
                    Optional<User> user = AuthenticationManager.getInstance().authenticate(username, password);
                    if(user.isPresent()){
                        authenticatedUser=user.get();
                        clientMessage.put("code_number",0);
                        break;
                    }else{
                        clientMessage.put("code_number",1);
                        clientMessage.put("error_message","Invalid credentials");
                    }
                }
                ServerClientCommunicator.sendJson(socket,clientMessage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        private void broadcastUser() throws IOException {
            JSONObject jsonObject=authenticatedUser.toJsonObject();
            jsonObject.put("routineType", UdpRoutineTypes.ADD_USER.toString());
            UdpManager.getInstance().broadcast(ClusterManager.getInstance().getUdpPort(), jsonObject.toJSONString());
        }
        private JSONObject redirectMessage(){
            JSONObject redirectMessage=new JSONObject();
            redirectMessage.put("code_number",2);
            redirectMessage.put("nodes", ClusterManager.getInstance().getNodesJsonArray());
            redirectMessage.put("thisTcpPort",ClusterManager.getInstance().getTcpPort());
            return redirectMessage;
        }
}