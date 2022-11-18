package org.example.tcp.query.quries;
import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.example.udp.UdpManager;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.UUID;

public class CreateDocumentQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        boolean doQuery=true;
        try {
            Optional<Collection> collection = getCollection();
            if(checkForAffinity){
                if(!collection.get().hasAffinity()){
                    broadcastType= UdpRoutineTypes.QUERY_REDIRECT;
                    broadcastIp=ClusterManager.getInstance().getNodesList().get(collection.get().getNodeWithAffinity()-1).getIp();
                    doQuery=false;
                }
            }
            if(doQuery){
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
                DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
                documentSchema.verify(document);
                UUID uuid = UUID.randomUUID();
                if(!document.containsKey("id")) {
                    document.put("id",uuid.toString());
                }
                document.put("_version",0);
                JSONObject indexObject= DiskOperations.createDocument(databaseName,collectionName,document);//write it to disk and retrieve the object that contains the location of this document on disk
                collection.orElseThrow(NoCollectionFoundException::new).addDocumentToIndexes(document,indexObject);//add this document to all indexes
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
            }
        } catch (Exception e) {

            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }

        return clientMessage;
    }


}
