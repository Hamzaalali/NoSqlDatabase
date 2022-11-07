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


public class DeleteDocumentQuery extends DatabaseQuery{
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();

        try{
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            String documentId=(String) query.get("documentId");
            Database database=indexManager.getDatabases().get(databaseName);
            if(database==null){
                throw new NoDatabaseFoundException();
            }
            Collection collection=database.getCollections().get(collectionName);
            if(collection==null){
                throw new NoCollectionFoundException();
            }
            JSONObject indexObject=collection.getIndex(documentId);
            if(indexObject==null){
                throw new NoDocumentFoundException();
            }
            JSONObject document= DiskOperations.readDocument(databaseName,collectionName,indexObject);
            collection.deleteDocument(document);//delete from indexes only , soft delete
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
