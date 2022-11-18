package org.example.tcp.query;

import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.NoDocumentFoundException;
import org.example.exception.system.DiskOperations;
import org.example.index.IndexManager;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

public abstract class DatabaseQuery {
    protected IndexManager indexManager;
    protected Optional<String> databaseName;
    protected Optional<String> collectionName;
    protected Optional<JSONObject> document;
    protected Optional<JSONObject> schema;
    protected Optional<JSONObject> indexPropertyObject;
    protected Optional<JSONObject> searchObject;
    protected Optional<String> documentId;
    protected Optional<JSONObject> data;
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
        databaseName= Optional.ofNullable((String) query.get("databaseName"));
        collectionName= Optional.ofNullable((String) query.get("collectionName"));
        document=Optional.ofNullable((JSONObject) query.get("document"));
        schema=Optional.ofNullable((JSONObject) query.get("schema"));
        indexPropertyObject=Optional.ofNullable((JSONObject) query.get("indexProperty"));
        searchObject=Optional.ofNullable((JSONObject) query.get("searchObject"));
        documentId=Optional.ofNullable((String) query.get("documentId"));
        data=Optional.ofNullable((JSONObject) query.get("data"));
    }

    public boolean isCheckForAffinity() {
        return checkForAffinity;
    }

    public void setCheckForAffinity(boolean checkForAffinity) {
        this.checkForAffinity = checkForAffinity;
    }
    protected Optional<Collection> getCollection() throws NoDatabaseFoundException {
        Optional<Database> database = getDatabase();
        String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
        Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
        return collection;
    }

    protected Optional<Database> getDatabase() {
        String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
        Optional<Database> database=indexManager.getDatabase(databaseName);
        return database;
    }
    protected JSONObject getDocument(Optional<Collection> collection) throws NoCollectionFoundException, IOException, ParseException, NoDocumentFoundException {
        String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
        String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
        String documentId=this.documentId.orElseThrow(IllegalArgumentException::new);
        Optional<JSONObject> indexObject= collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
        JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
        return document;
    }

    public void setUdpRoutineTypes(UdpRoutineTypes udpRoutineTypes) {
        this.udpRoutineTypes = udpRoutineTypes;
    }
}
