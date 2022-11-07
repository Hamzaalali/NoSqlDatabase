package org.example.database.query;

import org.example.database.Database;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DeleteDatabaseQuery extends DatabaseQuery{
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();

        try{
            String databaseName= (String) query.get("databaseName");
            Database database=indexManager.getDatabases().get(databaseName);
            if(database==null){
                throw new NoDatabaseFoundException();
            }
            DiskOperations.deleteDatabase(databaseName);//hard delete
            indexManager.deleteDatabase(databaseName);//delete from the index structure
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
