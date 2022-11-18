package org.example.tcp.query.quries;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentDataTypes;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.InvalidIndexPropertyObject;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.exception.system.DiskOperations;
import org.example.index.Index;
import org.example.database.JsonUtils;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CreateIndexQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        try {
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
            JSONObject indexPropertyObject=this.indexPropertyObject.orElseThrow(IllegalArgumentException::new);
            Optional<Database> database=getDatabase();
            Optional<Collection> collection=getCollection();
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().lock();
            Optional<JSONObject> propertyJson=getIndexProperty(databaseName,collectionName,indexPropertyObject);
            String property= (String) propertyJson.orElseThrow(InvalidIndexPropertyObject::new).get("key");
            DocumentDataTypes propertyDataType= (DocumentDataTypes) propertyJson.get().get("documentDataTypes");
            Index index=getIndexFromDataType(propertyDataType);
            addAllCurrentDocumentsToIndex(collection, property, index);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }

        return clientMessage;
    }

    private void addAllCurrentDocumentsToIndex(Optional<Collection> collection, String property, Index index) throws NoCollectionFoundException, IOException, ParseException {
        List<JSONObject> indexesList= collection.orElseThrow(NoCollectionFoundException::new).findAll();
        JSONArray collectionArray=DiskOperations.readCollection(databaseName.get(),collectionName.get(),indexesList);
        index.setIndexPropertyObject(indexPropertyObject.get());
        collection.get().addIndex(property, index);
        for(int i=0;i<collectionArray.size();i++){
            JSONObject jsonObject= (JSONObject) collectionArray.get(i);
            Object value= JsonUtils.searchForValue(jsonObject,indexPropertyObject.get());
            collection.get().addToIndex(property,value, (String) jsonObject.get("id"));
        }
    }

    private Optional<JSONObject> getIndexProperty(String databaseName, String collectionName, JSONObject indexPropertyObject) throws IOException, ParseException {
        DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
        Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(indexPropertyObject);
        return propertyJson;
    }
    private Index getIndexFromDataType(DocumentDataTypes propertyDataType){
        Index index=new Index(propertyDataType);
        return index;
    }

}
