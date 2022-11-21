package org.example.services;
import org.example.database.JsonUtils;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.CollectionNotFoundException;
import org.example.exception.InvalidIndexPropertyObject;
import org.example.index.Index;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentDataTypes;
import org.example.file.DiskOperations;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class IndexServices {
    public static void createIndex(JSONObject indexProperty,Database database,Collection collection,String databaseName,String collectionName) throws IOException, ParseException, InvalidIndexPropertyObject, CollectionNotFoundException {
        database.getCollectionLock().lock();
        Optional<JSONObject> propertyJson=getIndexProperty(databaseName,collectionName,indexProperty);
        String property= (String) propertyJson.orElseThrow(InvalidIndexPropertyObject::new).get("key");
        if (collection.containsIndex(property)) {
            database.getCollectionLock().unlock();
            return;
        }
        DocumentDataTypes propertyDataType= (DocumentDataTypes) propertyJson.get().get("documentDataTypes");
        Index index=getIndexFromDataType(propertyDataType);
        addAllCurrentDocumentsToIndex(indexProperty,collection,databaseName,collectionName, property, index);
        database.getCollectionLock().unlock();
    }
    private static void addAllCurrentDocumentsToIndex(JSONObject indexProperty,Collection collection,String databaseName,String collectionName, String property, Index index) throws IOException, ParseException {
        List<JSONObject> indexesList= collection.findAll();
        JSONArray collectionArray= DiskOperations.readCollection(databaseName,collectionName,indexesList);
        index.setIndexPropertyObject(indexProperty);
        collection.addIndex(property, index);
        for(int i=0;i<collectionArray.size();i++){
            JSONObject jsonObject= (JSONObject) collectionArray.get(i);
            Object value= JsonUtils.searchForValue(jsonObject,indexProperty);
            collection.addToIndex(property,value, (String) jsonObject.get("id"));
        }
    }

    private static Optional<JSONObject> getIndexProperty(String databaseName, String collectionName, JSONObject indexProperty) throws IOException, ParseException {
        DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
        Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(indexProperty);
        return propertyJson;
    }
    private static Index getIndexFromDataType(DocumentDataTypes propertyDataType){
        Index index=new Index(propertyDataType);
        return index;
    }
}
