package org.example.server;

import org.example.authentication.AuthenticationManager;
import org.example.database.DatabaseFacade;
import org.example.server_client.ServerClientCommunicator;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection implements Runnable{
        private Socket socket;
        private DatabaseFacade databaseFacade;
        public ServerConnection(Socket socket,DatabaseFacade databaseFacade) throws IOException {
            this.socket = socket;
            this.databaseFacade=databaseFacade;
        }
        @Override
        public void run() {
           authenticate();
           getUserQueries();
        }
        private void getUserQueries(){
            JSONObject clientMessage;
            while(true){
                try {
                    JSONObject query= ServerClientCommunicator.readJson(socket);
                    System.out.println(query);
                    clientMessage=databaseFacade.execute(query);
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
                    isAuthenticated= AuthenticationManager.getInstance().authenticate((String) user.get("username"), (String) user.get("password"));
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
    }