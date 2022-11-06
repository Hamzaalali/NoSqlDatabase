package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.file.system.DiskOperations;
import org.example.server_client.Query;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;

public class CreateDocumentQuery extends DatabaseQuery {
    @Override
    public void execute(JSONObject query) {
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject document=(JSONObject) query.get("document");
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            documentSchema.verify(document);
            UUID uuid = UUID.randomUUID();
            document.put("id",uuid.toString());
            JSONObject indexObject= DiskOperations.createDocument(databaseName,collectionName,document);
            Database database=indexManager.getDatabases().get(databaseName);
            Collection collection=database.getCollections().get(collectionName);
            collection.addDocument(document,indexObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
