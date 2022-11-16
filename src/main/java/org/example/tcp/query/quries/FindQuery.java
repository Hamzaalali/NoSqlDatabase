package org.example.tcp.query.quries;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.InvalidSearchObjectException;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
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
            Optional<Database> database=indexManager.getDatabase(databaseName);
            Optional<Collection> collection=database.orElseThrow(NoDatabaseFoundException::new).getCollection(collectionName);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().lock();
            collection.orElseThrow(NoCollectionFoundException::new);
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(searchObject);
            propertyJson.orElseThrow(InvalidSearchObjectException::new);
            String property= (String) propertyJson.get().get("key");
            Object value=  propertyJson.get().get("value");
            JSONArray jsonArray=new JSONArray();
            if(Objects.equals(property, "id")){
                Optional<JSONObject> indexObject=collection.get().getIndex((String) value);
                if(indexObject.isPresent()){
                    JSONObject document = DiskOperations.readDocument(databaseName,collectionName,indexObject.get());
                    jsonArray.add(document);
                }
            }else{
                if(collection.get().hasIndex(property)){
                    jsonArray =indexSearch(databaseName,collectionName,property,value,collection.get());
                }else{
                    jsonArray=fullSearch(databaseName,collectionName,collection.get(),searchObject,value);
                }
            }
            clientMessage.put("data",jsonArray);
            collection.orElseThrow(NoCollectionFoundException::new).getDocumentLock().unlock();
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        return clientMessage;
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
