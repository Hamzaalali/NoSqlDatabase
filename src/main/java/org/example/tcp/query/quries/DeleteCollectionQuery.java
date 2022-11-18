package org.example.tcp.query.quries;

import org.example.cluster.ClusterManager;
import org.example.database.Database;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.example.udp.UdpManager;
import org.json.simple.JSONObject;

import java.util.Optional;

public class DeleteCollectionQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        try{
            Optional<Database> database=getDatabase();
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().lock();
            DiskOperations.deleteCollection(databaseName,collectionName);//hard delete
            database.orElseThrow(NoDatabaseFoundException::new).deleteCollection(collectionName);//delete from the index structure
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }

        return clientMessage;
    }
}
