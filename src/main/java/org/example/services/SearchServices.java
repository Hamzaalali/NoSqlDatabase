package org.example.services;
import org.example.database.JsonUtils;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.CollectionNotFoundException;
import org.example.exception.InvalidSearchObjectException;
import org.example.index.IndexManager;
import org.example.file.DiskOperations;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SearchServices {
    public static JSONArray findAll(Collection collection, String databaseName, String collectionName) throws IOException, ParseException {
        collection.getDocumentLock().lock();
        try{
            List<JSONObject> indexesList=collection.findAll();//get all indexes that exist in the id index ( to exclude the soft deleted documents)
            JSONArray data=DiskOperations.readCollection(databaseName,collectionName,indexesList);//read from the disk
            collection.getDocumentLock().unlock();
            return data;
        }catch (Exception e){
            collection.getDocumentLock().unlock();
            throw new RuntimeException(e);
        }
    }
    public static JSONArray find(Collection collection,JSONObject searchObject,String databaseName,String collectionName) throws IOException, ParseException, InvalidSearchObjectException, CollectionNotFoundException {
        collection.getDocumentLock().lock();
        try{
            DocumentSchema documentSchema= DiskOperations.getSchema(databaseName,collectionName);
            Optional<JSONObject> propertyJson=documentSchema.getLeafProperty(searchObject);
            String property= (String) propertyJson.orElseThrow(InvalidSearchObjectException::new).get("key");
            Object value=  propertyJson.get().get("value");
            JSONArray jsonArray=search(collection,databaseName,collectionName,searchObject, property, value);
            collection.getDocumentLock().unlock();
            return jsonArray;
        }catch (Exception e){
            collection.getDocumentLock().unlock();
            throw new RuntimeException(e);
        }
    }
    private static JSONArray search(Collection collection,String databaseName,String collectionName,JSONObject searchObject, String property, Object value) throws IOException, ParseException, CollectionNotFoundException {
        JSONArray jsonArray=new JSONArray();
        if(Objects.equals(property, "id")){
            idSearch(collection,databaseName,collectionName, (String) value, jsonArray);
        }else{
            jsonArray = noneIdSearch(collection, databaseName,collectionName,searchObject,property, value);
        }
        return jsonArray;
    }

    private static JSONArray noneIdSearch(Collection collection,String databaseName,String collectionName,JSONObject searchObject, String property, Object value) throws IOException, ParseException, CollectionNotFoundException {
        JSONArray jsonArray;
        if(collection.hasIndex(property)){
            jsonArray =indexSearch(databaseName,collectionName, property, value, collection);
        }else{
            jsonArray =fullSearch(databaseName,collectionName, collection,searchObject, value);
        }
        return jsonArray;
    }

    private static void idSearch(Collection collection,String databaseName,String collectionName, String value, JSONArray jsonArray) throws IOException, ParseException {
        Optional<JSONObject> indexObject= collection.getIndex(value);
        if(indexObject.isPresent()){
            JSONObject document = DiskOperations.readDocument(databaseName,collectionName,indexObject.get());
            jsonArray.add(document);
        }
    }

    private static JSONArray indexSearch(String databaseName,String collectionName,String property,Object value,Collection collection) throws IOException, ParseException, CollectionNotFoundException {
        Optional<List<String>> idsList=collection.searchForIndex(property,value);
        JSONArray jsonArray=new JSONArray();
        if(idsList.isEmpty()){
            return jsonArray;
        }
        for(int i=0;i<idsList.get().size();i++){
            String id=idsList.get().get(i);
            Optional<JSONObject> indexObject= IndexManager.getInstance().getDocumentIndex(databaseName,collectionName,id);
            if(indexObject.isPresent()){
                JSONObject document =DiskOperations.readDocument(databaseName,collectionName,indexObject.get());
                jsonArray.add(document);
            }
        }
        return jsonArray;
    }
    private static JSONArray fullSearch(String databaseName,String collectionName,Collection collection,JSONObject searchObject,Object value) throws IOException, ParseException {
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
