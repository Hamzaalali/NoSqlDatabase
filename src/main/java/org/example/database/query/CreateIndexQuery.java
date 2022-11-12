package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentDataTypes;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.InvalidIndexPropertyObject;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.index.types.Index;
import org.example.index.types.IndexFactory;
import org.example.json.JsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CreateIndexQuery extends DatabaseQuery {
    @Override
    public JSONObject execute(JSONObject query) {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);

        try {
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().lock();

            Optional<JSONObject> propertyJson=getIndexProperty(databaseName,collectionName,indexPropertyObject);
            String property= (String) propertyJson.orElseThrow(InvalidIndexPropertyObject::new).get("key");
            DocumentDataTypes propertyDataType= (DocumentDataTypes) propertyJson.get().get("documentDataTypes");
            Index index=getIndexFromDataType(propertyDataType);
            List<JSONObject> indexesList=collection.orElseThrow(NoCollectionFoundException::new).findAll();
            JSONArray collectionArray=DiskOperations.readCollection(databaseName,collectionName,indexesList);
            index.setIndexPropertyObject(indexPropertyObject);
            collection.get().addIndex(property,index);
            for(int i=0;i<collectionArray.size();i++){
                JSONObject jsonObject= (JSONObject) collectionArray.get(i);
                Object value= JsonUtils.searchForValue(jsonObject,indexPropertyObject);
                collection.get().addToIndex( property,value, (String) jsonObject.get("id"));
            }
            database.orElseThrow(NoDatabaseFoundException::new).getCollectionLock().unlock();

        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }
    private Optional<JSONObject> getIndexProperty(String databaseName, String collectionName, JSONObject indexPropertyObject) throws IOException, ParseException {
        DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
        Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(indexPropertyObject);
        return propertyJson;
    }
    private Index getIndexFromDataType(DocumentDataTypes propertyDataType){
        IndexFactory indexFactory=new IndexFactory();
        Index index=indexFactory.getIndexMap().get(propertyDataType);
        return index;
    }

}
