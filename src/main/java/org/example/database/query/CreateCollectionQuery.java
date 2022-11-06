package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.file.system.DiskOperations;
import org.example.server_client.Query;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.jar.JarEntry;

public class CreateCollectionQuery extends DatabaseQuery {
    @Override
    public void execute(JSONObject query) {
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject schema=(JSONObject) query.get("schema");
            DocumentSchema.verifySchemaObject(schema);
            DiskOperations.createCollection(databaseName,collectionName,schema);
            Database database=indexManager.getDatabases().get(databaseName);
            database.createCollection(collectionName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
