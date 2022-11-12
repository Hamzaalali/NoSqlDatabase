package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.UUID;

public class CreateDocumentQuery extends DatabaseQuery {
    @Override
    public JSONObject execute(JSONObject query) {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);

        try {
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            documentSchema.verify(document);
            UUID uuid = UUID.randomUUID();
            document.put("id",uuid.toString());
            JSONObject indexObject= DiskOperations.createDocument(databaseName,collectionName,document);//write it to disk and retrieve the object that contains the location of this document on disk
            collection.orElseThrow(NoCollectionFoundException::new).addDocumentToIndexes(document,indexObject);//add this document to all indexes
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {

            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
}
