package org.example.tcp.query.quries;

import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONObject;

import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

public class CreateCollectionQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        isBroadcastable=true;
        clientMessage.put("code_number",0);
        Optional<Database> database=getDatabase();
        database.get().getCollectionLock().lock();
        try {
            JSONObject schema=this.schema.orElseThrow(IllegalArgumentException::new);
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
            DocumentSchema.verifyJsonTypes(schema);
            DiskOperations.createCollection(databaseName,collectionName,schema);
            database.orElseThrow(NoDatabaseFoundException::new).createCollection(collectionName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();
        }catch (FileAlreadyExistsException e){
            database.get().getCollectionLock().unlock();
            isBroadcastable=false;
            //ignore
        }catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }

        return clientMessage;
    }
}
