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
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

public class UpdateDocumentQuery extends DatabaseQuery {

    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        try{
            Optional<Collection> collection = getCollection();
            JSONObject document = getDocument(collection);
            if(collection.get().hasAffinity()){
                long documentVersion= (long) document.get("_version");
                if(udpRoutineTypes==UdpRoutineTypes.QUERY_REDIRECT){
                    long queryVersion= (long) query.get("_version");
                    if( queryVersion!=documentVersion){
                        return clientMessage;
                    }
                }
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
                collection.get().deleteDocument(document);
                JsonUtils.updateJsonObject(document,data);
                documentVersion++;
                document.put("_version",documentVersion);
                JSONObject newIndexObject= DiskOperations.createDocument(databaseName, collectionName, document);
                collection.get().addDocumentToIndexes(document,newIndexObject);
                query.put("data",document);
                clientMessage.put("data",document);
                collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
            }else{
                if(udpRoutineTypes==UdpRoutineTypes.SYNC){
                    collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
                    collection.get().deleteDocument(document);
                    JsonUtils.updateJsonObject(document,data);
                    JSONObject newIndexObject= DiskOperations.createDocument(databaseName, collectionName, document);
                    collection.get().addDocumentToIndexes(document,newIndexObject);
                    clientMessage.put("data",document);
                    collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
                }else{
                    long version= (long) document.get("_version");
                    query.put("_version",version);
                    System.out.println("query :-"+query);
                    broadcastType= UdpRoutineTypes.QUERY_REDIRECT;
                    broadcastIp= ClusterManager.getInstance().getNodesList().get(collection.get().getNodeWithAffinity()-1).getIp();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }


}
