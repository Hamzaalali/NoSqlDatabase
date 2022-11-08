package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class DeleteCollectionQuery extends DatabaseQuery{
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try{
            Optional<Database> database=indexManager.getDatabase(databaseName);
            DiskOperations.deleteCollection(databaseName,collectionName);//hard delete
            database.orElseThrow(NoDatabaseFoundException::new).deleteCollection(collectionName);//delete from the index structure
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
