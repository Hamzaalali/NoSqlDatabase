package org.example.index;

import org.example.database.Database;
import org.json.simple.JSONObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
public class IndexManager implements Serializable {
    private static volatile IndexManager instance;
    private Map<String, Database> databases;
    private IndexManager() {
        databases=new HashMap<>();
    }
    public static IndexManager getInstance() {

        IndexManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(IndexManager.class) {
            if (instance == null) {
                instance = new IndexManager();
            }
            return instance;
        }
    }
    public void addDatabase(String databaseName){
        Database database=new Database();
        databases.put(databaseName,database);
    }
    public Map<String, Database> getDatabases() {
        return databases;
    }
    public JSONObject getDocumentIndex(String databaseName,String collectionName,String id){
        return  databases.get(databaseName).getCollections().get(collectionName).getIndex(id);
    }
    public void deleteDatabase(String databaseName){
        databases.remove(databaseName);
    }
}
