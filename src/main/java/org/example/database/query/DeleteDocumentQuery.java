package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;


public class DeleteDocumentQuery extends DatabaseQuery{
    @Override
    public void execute(JSONObject query) {
        try{
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            String documentId=(String) query.get("documentId");
            Database database=indexManager.getDatabases().get(databaseName);
            Collection collection=database.getCollections().get(collectionName);
            JSONObject indexObject=collection.getIndex(documentId);
            JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject);
            collection.deleteDocument(document);//delete from indexes only , soft delete
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
