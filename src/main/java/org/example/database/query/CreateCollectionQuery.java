package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;

import java.util.Optional;

public class CreateCollectionQuery extends DatabaseQuery {
    @Override
    public JSONObject execute(JSONObject query) {
        JSONObject clientMessage=new JSONObject();
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
