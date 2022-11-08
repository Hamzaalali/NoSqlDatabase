package org.example.database;

import org.example.database.collection.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Collection> getCollection(String collectionName){
        return Optional.ofNullable(collections.get(collectionName));
    }
    public void deleteCollection(String collectionName) throws ClassNotFoundException {
        if(!collectionName.contains(collectionName))
            throw new ClassNotFoundException();
        collections.remove(collectionName);
    }
}
