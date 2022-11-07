package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.NoDocumentFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Optional;


public class DeleteDocumentQuery extends DatabaseQuery{
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try{
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            Optional<JSONObject> indexObject=collection.orElseThrow(NoCollectionFoundException::new).getIndex(documentId);
            JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject.orElseThrow(NoDocumentFoundException::new));
            collection.get().deleteDocument(document);//delete from indexes only , soft delete
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
