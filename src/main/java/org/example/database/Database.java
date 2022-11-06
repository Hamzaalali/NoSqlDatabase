package org.example.database;

import org.example.database.collection.Collection;
import org.example.index.BPlusTree.BTree;
import org.example.server_client.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private Map<String,Collection> collections;
    public Database(){
        collections=new HashMap<>();
    }
    public Collection createCollection(String name){
        Collection collection=new Collection();
        collections.put(name,collection);
        return collection;
    }
    public Map<String, Collection> getCollections() {
        return collections;
    }
    public void deleteCollection(String collectionName){
        collections.remove(collectionName);
    }
}
