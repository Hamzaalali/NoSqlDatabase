package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.NoDocumentFoundException;
import org.example.file.system.DiskOperations;
import org.example.json.JsonUtils;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;

public class UpdateDocumentQuery extends DatabaseQuery{

    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try{
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            Optional<JSONObject> indexObject=collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
            JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
            collection.get().deleteDocument(document);
            JsonUtils.updateJsonObject(document,data);
            JSONObject newIndexObject= DiskOperations.createDocument(databaseName, collectionName, document);
            collection.get().addDocumentToIndexes(document,newIndexObject);
            clientMessage.setData(document);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
