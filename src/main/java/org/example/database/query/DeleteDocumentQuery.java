package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.server_client.Query;
import org.json.simple.JSONObject;


public class DeleteDocumentQuery extends DatabaseQuery{
    @Override
    public void execute(JSONObject query) {
        String databaseName= (String) query.get("databaseName");
        String collectionName= (String) query.get("collectionName");
        String documentId=(String) query.get("documentId");
        Database database=indexManager.getDatabases().get(databaseName);
        Collection collection=database.getCollections().get(collectionName);
        collection.deleteDocument(documentId);//soft delete from idIndex
    }
}
