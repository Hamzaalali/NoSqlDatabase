package org.example.cluster;

import org.json.simple.JSONObject;

public class Node {
    private int nodeNumber;
    private int tcpPort;
    private JSONObject nodeJsonObject;
    private String ip;

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getTcpPort() {
        return tcpPort;
    }
    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }
    public static Node of(JSONObject nodeObject){
        Node node=new Node();
        node.nodeNumber= (int)((long) nodeObject.get("nodeNumber"));
        node.ip= (String) nodeObject.get("ip");
        node.tcpPort= (int)((long) nodeObject.get("tcpPort"));
        node.nodeJsonObject=nodeObject;
        return node;
    }
    public JSONObject getNodeJsonObject(){
        return nodeJsonObject;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
