package org.example.tcp.query;

import org.example.cluster.ClusterManager;
import org.example.index.IndexManager;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;

public abstract class DatabaseQuery {
    protected IndexManager indexManager;
    protected String databaseName;
    protected String collectionName;
    protected JSONObject document;
    protected JSONObject schema;
    protected JSONObject indexPropertyObject;
    protected JSONObject searchObject;
    protected String documentId;
    protected JSONObject data;
    protected boolean isBroadcastable;
    protected JSONObject query;
    protected boolean checkForAffinity;
    protected UdpRoutineTypes broadcastType;
    protected String broadcastIp;
    public DatabaseQuery(){
        indexManager=IndexManager.getInstance();
        isBroadcastable=false;
        checkForAffinity=true;
        broadcastType=UdpRoutineTypes.SYNC;
        broadcastIp=ClusterManager.getInstance().getBroadcastIp();
    }
    public abstract JSONObject execute();
    public void broadcast(){
        try{
            if(isBroadcastable)
                UdpManager.getInstance().sendUpdQuery(broadcastIp,ClusterManager.getInstance().getUdpPort(), query,broadcastType);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public void setQuery(JSONObject query){
        this.query=query;
        databaseName= (String) query.get("databaseName");
        collectionName= (String) query.get("collectionName");
        document=(JSONObject) query.get("document");
        schema=(JSONObject) query.get("schema");
        indexPropertyObject=(JSONObject) query.get("indexProperty");
        searchObject=(JSONObject) query.get("searchObject");
        documentId=(String) query.get("documentId");
        data=(JSONObject) query.get("data");
    }

    public boolean isCheckForAffinity() {
        return checkForAffinity;
    }

    public void setCheckForAffinity(boolean checkForAffinity) {
        this.checkForAffinity = checkForAffinity;
    }
}
