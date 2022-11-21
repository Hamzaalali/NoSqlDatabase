package org.example.commands;

import org.example.protocol.UDPCommunicator;
import org.example.cluster.ClusterManager;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

public abstract class Command {
    protected abstract JSONArray execute(JSONObject commandJson);
    protected void redirectToNodeWithAffinity(Collection collection,JSONObject commandJson) throws IOException {
        CommandTypes commandType= CommandTypes.REDIRECT;
        String broadcastIp= ClusterManager.getInstance().getNodesList().get(collection.getNodeWithAffinity()-1).getIp();
        int port= ClusterManager.getInstance().getUdpPort();
        commandJson.put("isRedirected",true);
        JSONObject redirectCommand=new JSONObject();
        redirectCommand.put("commandType",commandType.toString());
        redirectCommand.put("command",commandJson);
        UDPCommunicator.sendUpdCommand(broadcastIp,port,commandJson);
    }
}
