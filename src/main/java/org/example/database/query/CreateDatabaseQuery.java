package org.example.database.query;

import org.example.file.system.DiskOperations;
import org.example.index.IndexManager;
import org.json.simple.JSONObject;

import java.io.IOException;

public class CreateDatabaseQuery extends DatabaseQuery {
    @Override
    public void execute(JSONObject query) {
        try{
            String databaseName= (String) query.get("databaseName");
            IndexManager indexManager=IndexManager.getInstance();
            DiskOperations.createDatabase(databaseName);
            indexManager.addDatabase(databaseName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
