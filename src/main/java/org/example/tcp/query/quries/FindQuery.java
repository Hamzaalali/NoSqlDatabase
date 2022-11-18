package org.example.tcp.query.quries;

import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.InvalidSearchObjectException;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.system.DiskOperations;
import org.example.database.JsonUtils;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FindQuery extends DatabaseQuery {

    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        try {
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            String collectionName=this.collectionName.orElseThrow(IllegalArgumentException::new);
            JSONObject searchObject=this.searchObject.orElseThrow(IllegalArgumentException::new);
            Optional<Collection> collection = getCollection();
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            collection.orElseThrow(NoCollectionFoundException::new);
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(searchObject);
            propertyJson.orElseThrow(InvalidSearchObjectException::new);
            String property= (String) propertyJson.get().get("key");
            Object value=  propertyJson.get().get("value");
            JSONArray jsonArray=search(collection, property, value);
            clientMessage.put("data",jsonArray);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
    }

    private JSONArray search(Optional<Collection> collection, String property, Object value) throws IOException, ParseException, NoCollectionFoundException {
        JSONArray jsonArray=new JSONArray();
        if(Objects.equals(property, "id")){
            idSearch(collection, (String) value, jsonArray);
        }else{
            jsonArray = noneIdSearch(collection, property, value);
        }
        return jsonArray;
    }

    private JSONArray noneIdSearch(Optional<Collection> collection, String property, Object value) throws IOException, ParseException, NoCollectionFoundException {
        JSONArray jsonArray;
        if(collection.get().hasIndex(property)){
            jsonArray =indexSearch(databaseName.get(),collectionName.get(), property, value, collection.get());
        }else{
            jsonArray =fullSearch(databaseName.get(),collectionName.get(), collection.get(),searchObject.get(), value);
        }
        return jsonArray;
    }

    private void idSearch(Optional<Collection> collection, String value, JSONArray jsonArray) throws IOException, ParseException {
        Optional<JSONObject> indexObject= collection.get().getIndex(value);
        if(indexObject.isPresent()){
            JSONObject document = DiskOperations.readDocument(databaseName.get(),collectionName.get(),indexObject.get());
            jsonArray.add(document);
        }
    }

    private JSONArray indexSearch(String databaseName,String collectionName,String property,Object value,Collection collection) throws IOException, ParseException, NoCollectionFoundException {
        Optional<List<String>> idsList=collection.searchForIndex(property,value);
        JSONArray jsonArray=new JSONArray();
        if(idsList.isEmpty()){
            return jsonArray;
        }
        for(int i=0;i<idsList.get().size();i++){
            String id=idsList.get().get(i);
            Optional<JSONObject> indexObject=indexManager.getDocumentIndex(databaseName,collectionName,id);
            if(indexObject.isPresent()){
                JSONObject document =DiskOperations.readDocument(databaseName,collectionName,indexObject.get());
                jsonArray.add(document);
            }
        }
        return jsonArray;
    }
    private JSONArray fullSearch(String databaseName,String collectionName,Collection collection,JSONObject searchObject,Object value) throws IOException, ParseException {
        List<JSONObject> indexesObjectList=collection.findAll();
        JSONArray jsonArray=new JSONArray();
        for(JSONObject indexObject:indexesObjectList){
            JSONObject document=DiskOperations.readDocument(databaseName,collectionName,indexObject);
            if(Objects.equals(JsonUtils.searchForValue(document,searchObject),value)){
                jsonArray.add(document);
            }
        }
        return jsonArray;
    }
}
