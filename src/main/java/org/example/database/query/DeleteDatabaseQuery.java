package org.example.database.query;

import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DeleteDatabaseQuery extends DatabaseQuery{
    @Override
    public void execute(JSONObject query) {
        try{
            String databaseName= (String) query.get("databaseName");
            DiskOperations.deleteDatabase(databaseName);//hard delete
            indexManager.deleteDatabase(databaseName);//delete from the index structure
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
