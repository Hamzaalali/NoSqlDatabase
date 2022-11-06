package org.example.database.query;

import org.example.file.system.DiskOperations;
import org.example.index.IndexManager;
import org.example.server_client.Query;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DeleteDatabaseQuery extends DatabaseQuery{
    @Override
    public void execute(JSONObject query) {
        try{
            String databaseName= (String) query.get("databaseName");
            DiskOperations.deleteDatabase(databaseName);
            indexManager.deleteDatabase(databaseName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
