package org.example.database.query;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.Optional;

public class FindAllQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try {
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            List<JSONObject>indexesList=collection.orElseThrow(NoCollectionFoundException::new).findAll();//get all indexes that exist in the id index ( to exclude the soft deleted documents)
            clientMessage.setDataArray(DiskOperations.readCollection(databaseName,collectionName,indexesList));//read from the disk
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
}
