package org.example.tcp.query;

import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.NoDocumentFoundException;
import org.example.file.system.DiskOperations;
import org.example.index.IndexManager;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

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
    protected UdpRoutineTypes udpRoutineTypes;
    public DatabaseQuery(){
        indexManager=IndexManager.getInstance();
        isBroadcastable=false;
        checkForAffinity=true;
        broadcastType=UdpRoutineTypes.SYNC;
        broadcastIp=ClusterManager.getInstance().getBroadcastIp();
        udpRoutineTypes=UdpRoutineTypes.NONE;
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
    protected Optional<Collection> getCollection() throws NoDatabaseFoundException {
        Optional<Database> database = getDatabase();
        Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
        return collection;
    }

    protected Optional<Database> getDatabase() {
        Optional<Database> database=indexManager.getDatabase(databaseName);
        return database;
    }
    protected JSONObject getDocument(Optional<Collection> collection) throws NoCollectionFoundException, IOException, ParseException, NoDocumentFoundException {
        Optional<JSONObject> indexObject= collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
        JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
        return document;
    }

    public void setUdpRoutineTypes(UdpRoutineTypes udpRoutineTypes) {
        this.udpRoutineTypes = udpRoutineTypes;
    }
}
