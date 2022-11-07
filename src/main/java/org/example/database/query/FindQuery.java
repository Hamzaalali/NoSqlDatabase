package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.InvalidSearchObjectException;
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
import java.util.List;
import java.util.Objects;

public class FindQuery extends DatabaseQuery {
    ClientMessage clientMessage=new ClientMessage();
    @Override
    public ClientMessage execute(JSONObject query) {
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject searchObject=(JSONObject) query.get("searchObject");

            Database database=indexManager.getDatabases().get(databaseName);
            if(database==null){
                throw new NoDatabaseFoundException();
            }
            Collection collection=database.getCollections().get(collectionName);
            if(collection==null){
                throw new NoCollectionFoundException();
            }

            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            JSONObject propertyJson=documentSchema.getLeafProperty(searchObject);
            if(propertyJson==null){
                throw new InvalidSearchObjectException();
            }
            String property= (String) propertyJson.get("key");
            Object value=  propertyJson.get("value");
            JSONArray jsonArray=new JSONArray();
            if(Objects.equals(property, "id")){
                JSONObject indexObject=collection.getIndex((String) value);
                if(indexObject!=null){
                    JSONObject document = DiskOperations.readDocument(databaseName,collectionName,indexObject);
                    jsonArray.add(document);
                }
            }else{
                if(collection.hasIndex(property)){
                    jsonArray.add(indexSearch(databaseName,collectionName,property,value,collection));
                }else{
                    jsonArray.add(fullSearch(databaseName,collectionName,collection,searchObject,value));
                }
            }
            clientMessage.setDataArray(jsonArray);
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }

    private JSONArray indexSearch(String databaseName,String collectionName,String property,Object value,Collection collection) throws IOException, ParseException {
        List<String> idsList=collection.searchForIndex(property,value);
        JSONArray jsonArray=new JSONArray();
        if(idsList==null){
            return jsonArray;
        }
        for(int i=0;i<idsList.size();i++){
            String id=idsList.get(i);
            JSONObject indexObject=indexManager.getDocumentIndex(databaseName,collectionName,id);
            JSONObject document =DiskOperations.readDocument(databaseName,collectionName,indexObject);
            jsonArray.add(document);
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
