package org.example.database.query;

import org.example.exception.DatabaseExistsException;
import org.example.file.system.DiskOperations;
import org.example.index.IndexManager;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

import java.io.IOException;

public class CreateDatabaseQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        indexManager.getDatabaseLock().lock();
        try{
            DiskOperations.createDatabase(databaseName);
            indexManager.addDatabase(databaseName);
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        indexManager.getDatabaseLock().unlock();
        return clientMessage;
    }
}
