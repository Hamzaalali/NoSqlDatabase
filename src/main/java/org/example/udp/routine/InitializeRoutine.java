package org.example.udp.routine;

import org.example.authentication.AuthenticationManager;
import org.example.cluster.ClusterManager;
import org.example.load.balance.LoadBalancer;
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
            JSONArray nodes= (JSONArray) routineJson.get("ports");
            ClusterManager.getInstance().setNodes(nodes);
            ClusterManager.getInstance().setTcpPort((long) routineJson.get("tcpPort"));
            LoadBalancer.getInstance().setTimeWindow((long) routineJson.get("loadBalanceTimeWindow"));
            LoadBalancer.getInstance().setMaxRequests((long) routineJson.get("loadBalanceMaxRequests"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
