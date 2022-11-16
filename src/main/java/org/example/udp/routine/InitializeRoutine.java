package org.example.udp.routine;

import org.example.authentication.AuthenticationManager;
import org.example.cluster.ClusterManager;
import org.example.load.balance.RequestLoad;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class InitializeRoutine extends UdpRoutine{
    @Override
    public void execute(JSONObject routineJson) {
        try{
            JSONArray users= (JSONArray) routineJson.get("users");
            for(int i=0;i<users.size();i++){
                JSONObject user=(JSONObject)users.get(i);
                AuthenticationManager.getInstance().addUser((String) user.get("username"), (String) user.get("password"));
            }
            JSONArray nodes= (JSONArray) routineJson.get("nodes");
            for(int i=0;i<nodes.size();i++){
                JSONObject node=(JSONObject) nodes.get(i);
                ClusterManager.getInstance().addNode(node);
            }
            ClusterManager.getInstance().setTcpPort((long) routineJson.get("tcpPort"));
            RequestLoad.getInstance().setTimeWindow((long) routineJson.get("loadBalanceTimeWindow"));
            RequestLoad.getInstance().setMaxRequests((long) routineJson.get("loadBalanceMaxRequests"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
