package org.example.tcp.query.quries;

import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.NoDocumentFoundException;
import org.example.file.system.DiskOperations;
import org.example.database.JsonUtils;
import org.example.tcp.query.DatabaseQuery;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;

import java.util.Optional;

public class UpdateDocumentQuery extends DatabaseQuery {

    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        boolean doQuery=true;
        try{
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            Optional<JSONObject> indexObject=collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
            JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
            long version= (long) document.get("_version");
            query.put("_version",version);
            if(checkForAffinity){
                if(!collection.get().hasAffinity()){
                    broadcastType= UdpRoutineTypes.QUERY_REDIRECT;
                    broadcastIp= ClusterManager.getInstance().getNodesList().get(collection.get().getNodeWithAffinity()-1).getIp();
                    doQuery=false;
                }
            }
            if(doQuery){
                long queryVersion= (long) query.get("_version");
                long documentVersion= (long) document.get("_version");
                System.out.println(queryVersion);
                System.out.println(documentVersion);
                if(queryVersion==documentVersion){
                    System.out.println("test");
                    collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
                    collection.get().deleteDocument(document);
                    JsonUtils.updateJsonObject(document,data);
                    documentVersion++;
                    document.put("_version",documentVersion);
                    System.out.println(document.get("_version"));
                    JSONObject newIndexObject= DiskOperations.createDocument(databaseName, collectionName, document);
                    collection.get().addDocumentToIndexes(document,newIndexObject);
                    clientMessage.put("data",document);
                    collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
                }
            }
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
}
