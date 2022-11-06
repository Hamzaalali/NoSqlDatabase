package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentSchema;
import org.example.file.system.DiskOperations;
import org.example.server_client.Query;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FindQuery extends DatabaseQuery {
    @Override
    public void execute(JSONObject query) {
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject searchObject=(JSONObject) query.get("searchObject");
            Database database=indexManager.getDatabases().get(databaseName);
            Collection collection=database.getCollections().get(collectionName);
            DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
            JSONObject propertyJson=documentSchema.searchForLeafProperty(searchObject);
            String property= (String) propertyJson.get("key");
            Object value=  propertyJson.get("value");
            if(Objects.equals(property, "id")){
                JSONObject indexJson=collection.getIndex((String) value);
                JSONObject document = DiskOperations.readDocument(databaseName,collectionName,indexJson);
                System.out.println(document);
            }else{
                if(collection.hasIndex(property))
                    indexSearch(databaseName,collectionName,property,value,collection);
                else
                    fullSearch();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void indexSearch(String databaseName,String collectionName,String property,Object value,Collection collection) throws IOException, ParseException {
        List<String> idsList=collection.searchForIndex(property,value);
        if(idsList==null){
            return;
        }
        for(int i=0;i<idsList.size();i++){
            String id=idsList.get(i);
            JSONObject indexObject=indexManager.getDocumentIndex(databaseName,collectionName,id);
            if(indexObject==null){
                idsList.remove(id);
                if(idsList.size()==0){
                    collection.deleteFromIndex(property,value);
                }
                i--;
            }else{
                JSONObject document =DiskOperations.readDocument(databaseName,collectionName,indexObject);
                System.out.println(document);
            }
        }
    }
    private void fullSearch(){

    }
}
