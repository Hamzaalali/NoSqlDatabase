package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.database.collection.document.DocumentDataTypes;
import org.example.database.collection.document.DocumentSchema;
import org.example.exception.NoCollectionFoundException;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.index.types.Index;
import org.example.index.types.IndexFactory;
import org.example.json.JsonUtils;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.List;
public class CreateIndexQuery extends DatabaseQuery {
    @Override
    public ClientMessage execute(JSONObject query) {
        ClientMessage clientMessage=new ClientMessage();
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            JSONObject indexPropertyObject=(JSONObject) query.get("indexProperty");

            JSONObject propertyJson=getIndexProperty(databaseName,collectionName,indexPropertyObject);
            String property= (String) propertyJson.get("key");
            DocumentDataTypes propertyDataType= (DocumentDataTypes) propertyJson.get("documentDataTypes");
            Index index=getIndexFromDataType(propertyDataType);
            Database database=indexManager.getDatabases().get(databaseName);
            if(database==null){
                throw new NoDatabaseFoundException();
            }
            Collection collection=database.getCollections().get(collectionName);
            if(collection==null){
                throw new NoCollectionFoundException();
            }
            List<JSONObject> indexesList=collection.findAll();
            JSONArray collectionArray=DiskOperations.readCollection(databaseName,collectionName,indexesList);

            index.setIndexPropertyObject(indexPropertyObject);
            collection.addIndex(property,index);
            for(int i=0;i<collectionArray.size();i++){
                JSONObject jsonObject= (JSONObject) collectionArray.get(i);
                Object value= JsonUtils.searchForValue(jsonObject,indexPropertyObject);
                collection.addToIndex( property,value, (String) jsonObject.get("id"));
            }
        } catch (Exception e) {
            clientMessage.setCodeNumber(1);
            clientMessage.setErrorMessage(e.getMessage());
        }
        return clientMessage;
    }
    private JSONObject getIndexProperty(String databaseName,String collectionName,JSONObject indexPropertyObject) throws IOException, ParseException {
        DocumentSchema documentSchema=DiskOperations.getSchema(databaseName,collectionName);
        JSONObject propertyJson=documentSchema.getLeafProperty(indexPropertyObject);
        return propertyJson;
    }
    private Index getIndexFromDataType(DocumentDataTypes propertyDataType){
        IndexFactory indexFactory=new IndexFactory();
        Index index=indexFactory.getIndexMap().get(propertyDataType);
        return index;
    }

}
