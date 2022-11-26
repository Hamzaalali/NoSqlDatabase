package org.example.protocol;
import org.example.authentication.AuthenticationManager;
import org.example.balance.RequestLoad;
import org.example.commands.CommandsMediator;
import org.example.commands.CommandTypes;
import org.example.exception.ConnectionTerminatedException;
import org.example.authentication.User;
import org.example.cluster.ClusterManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class TCPServerConnection implements Runnable{
        private final Socket socket;
        private User authenticatedUser;
        boolean isRunning;
        public TCPServerConnection(Socket socket) throws IOException {
            this.socket = socket;
            isRunning=true;
        }
        @Override
        public void run() {
            try{
                getAuthenticatedUser();
                getUserQueries();
            } catch (IOException e) {
                System.out.println("Error With Connection");
            }
        }
        private void getUserQueries() throws IOException {
            try {
                JSONObject clientMessage;
                while(isRunning){
                    JSONObject commandJson= TCPCommunicator.readJson(socket);
                    System.out.println("received :-"+ commandJson);
                    System.out.println("--------------------------------------------");
                    clientMessage = handleCommand(commandJson);
                    System.out.println("client message"+clientMessage);
                    TCPCommunicator.sendJson(socket,clientMessage);
                }
            } catch (ConnectionTerminatedException e){
                socket.close();
            }catch (Exception e) {
               System.out.println("socket closed at port "+socket.getPort());
            }
        }

    private JSONObject handleCommand(JSONObject commandJson)  {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        try{
            if(!RequestLoad.getInstance().addRequest()){
                broadcastUser();
                isRunning=false;
                clientMessage=redirectMessage();
            }else{
                commandJson.put("sync",false);
                JSONArray data= CommandsMediator.getInstance().execute(commandJson);
                clientMessage.put("data",data);
            }
        }catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
    private void getAuthenticatedUser() throws IOException {
            JSONObject clientMessage=new JSONObject();
            try{
                authenticate(clientMessage);
            }catch (ConnectionTerminatedException e){
                socket.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    private void authenticate(JSONObject clientMessage) throws IOException, ParseException, ConnectionTerminatedException {
        while(true){
            JSONObject userJson= TCPCommunicator.readJson(socket);
            String username=(String) userJson.get("username");
            String password=(String) userJson.get("password");
            Optional<User> user = AuthenticationManager.getInstance().authenticate(username, password);
            if(user.isPresent()){
                authenticatedUser=user.get();
                clientMessage.put("code_number",0);
                TCPCommunicator.sendJson(socket, clientMessage);
                break;
            }else{
                clientMessage.put("code_number",1);
                clientMessage.put("error_message","Invalid credentials");
                TCPCommunicator.sendJson(socket, clientMessage);
            }
        }
    }

    private void broadcastUser() throws IOException {
            JSONObject userJson=authenticatedUser.toJsonObject();
            userJson.put("commandType", CommandTypes.ADD_USER.toString());
            UDPCommunicator.broadcastCommand(userJson);
    }
    private JSONObject redirectMessage(){
        JSONObject redirectMessage=new JSONObject();
        redirectMessage.put("code_number",2);
        redirectMessage.put("nodes", ClusterManager.getInstance().getNodesJsonArray());
        redirectMessage.put("thisTcpPort",ClusterManager.getInstance().getTcpPort());
        return redirectMessage;
    }
}