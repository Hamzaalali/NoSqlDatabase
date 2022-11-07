package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;

public class CreateDocumentQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try {

            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject document=(JSONObject) query.get("document");

            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            documentSchema.verify(document);

            UUID uuid = UUID.randomUUID();
            document.put("id",uuid.toString());

            JSONObject indexObject= DiskOperations.createDocument(databaseName,collectionName,document);//write it to disk and retrieve the object that contains the location of this document on disk

            Database database=indexManager.getDatabases().get(databaseName);
            if(database==null){
                throw new NoDatabaseFoundException();
            }
            Collection collection=database.getCollections().get(collectionName);
            if(collection==null){
                throw new NoCollectionFoundException();
            }
            collection.addDocumentToIndexes(document,indexObject);//add this document to all indexes
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
