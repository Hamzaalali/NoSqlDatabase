package org.example.cluster;
import org.example.index.IndexManager;
import org.json.simple.JSONArray;

public class ClusterManager {
    private static volatile ClusterManager instance;
    private JSONArray nodes;
    private String broadcastIp;
    private int bootstrapperPort;//the port that the bootstrapper will send info to
    private long tcpPort;//the port that is used for client to communicate with this node
    private ClusterManager() {
        bootstrapperPort= Integer.parseInt(System.getenv("BOOTSTRAPPER_PORT"));
        broadcastIp=System.getenv("BROADCAST_IP");
    }
    public static ClusterManager getInstance() {
        ClusterManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(IndexManager.class) {
            if (instance == null) {
                instance = new ClusterManager();
            }
            return instance;
        }
    }

    public JSONArray getNodesPorts() {
        return nodes;
    }

    public void setNodes(JSONArray nodes) {
        this.nodes = nodes;
    }

    public static void setInstance(ClusterManager instance) {
        ClusterManager.instance = instance;
    }

    public JSONArray getNodes() {
        return nodes;
    }

    public String getBroadcastIp() {
        return broadcastIp;
    }

    public void setBroadcastIp(String broadcastIp) {
        this.broadcastIp = broadcastIp;
    }

    public int getBootstrapperPort() {
        return bootstrapperPort;
    }

    public long getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(long tcpPort) {
        this.tcpPort = tcpPort;
    }
}
