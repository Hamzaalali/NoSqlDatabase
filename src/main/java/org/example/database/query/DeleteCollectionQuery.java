package org.example.database.query;

import org.example.database.Database;
import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DeleteCollectionQuery extends DatabaseQuery{
    @Override
    public void execute(JSONObject query) {
        try{
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            Database database=indexManager.getDatabases().get(databaseName);
            DiskOperations.deleteCollection(databaseName,collectionName);//hard delete
            database.deleteCollection(collectionName);//delete from the index structure
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
