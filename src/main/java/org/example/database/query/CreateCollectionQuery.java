package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

import java.util.Optional;

public class CreateCollectionQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try {
            Optional<Database> database=indexManager.getDatabase(databaseName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().lock();
            DocumentSchema.verifyJsonTypes(schema);
            DiskOperations.createCollection(databaseName,collectionName,schema);
            database.orElseThrow(NoDatabaseFoundException::new).createCollection(collectionName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }

        return clientMessage;
    }
}
