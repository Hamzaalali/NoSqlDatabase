package org.example.tcp.query.quries;
import org.example.database.collection.Collection;
import org.example.exception.NoCollectionFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.Optional;

public class FindAllQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        try {
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
            Optional<Collection> collection = getCollection();
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            List<JSONObject>indexesList=collection.orElseThrow(NoCollectionFoundException::new).findAll();//get all indexes that exist in the id index ( to exclude the soft deleted documents)
            clientMessage.put("data",DiskOperations.readCollection(databaseName,collectionName,indexesList));//read from the disk
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
}
