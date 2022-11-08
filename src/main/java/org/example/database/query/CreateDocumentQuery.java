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
import java.util.Optional;
import java.util.UUID;

public class CreateDocumentQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try {
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            documentSchema.verify(document);
            UUID uuid = UUID.randomUUID();
            document.put("id",uuid.toString());
            JSONObject indexObject= DiskOperations.createDocument(databaseName,collectionName,document);//write it to disk and retrieve the object that contains the location of this document on disk
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).addDocumentToIndexes(document,indexObject);//add this document to all indexes
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
