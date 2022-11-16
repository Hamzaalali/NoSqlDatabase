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
            if(checkForAffinity){
                if(!collection.get().hasAffinity()){
                    broadcastType= UdpRoutineTypes.QUERY_REDIRECT;
                    broadcastIp= ClusterManager.getInstance().getNodesList().get(collection.get().getNodeWithAffinity()-1).getIp();
                    doQuery=false;
                }
            }
            if(doQuery){
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
                Optional<JSONObject> indexObject=collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
                JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
                collection.get().deleteDocument(document);
                JsonUtils.updateJsonObject(document,data);
                JSONObject newIndexObject= DiskOperations.createDocument(databaseName, collectionName, document);
                collection.get().addDocumentToIndexes(document,newIndexObject);
                clientMessage.put("data",document);
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
            }
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
}
