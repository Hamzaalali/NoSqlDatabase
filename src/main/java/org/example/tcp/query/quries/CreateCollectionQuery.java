package org.example.tcp.query.quries;

import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.example.udp.UdpManager;
import org.json.simple.JSONObject;

import java.util.Optional;

public class CreateCollectionQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        isBroadcastable=true;
        clientMessage.put("code_number",0);
        try {
            Optional<Database> database=indexManager.getDatabase(databaseName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().lock();
            DocumentSchema.verifyJsonTypes(schema);
            DiskOperations.createCollection(databaseName,collectionName,schema);
            database.orElseThrow(NoDatabaseFoundException::new).createCollection(collectionName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }

        return clientMessage;
    }
}
