package org.example.database;
import org.example.database.collection.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
public class Database {
    private Map<String,Collection> collections;
    private ReentrantLock collectionLock;
    public Database(){
        collections=new HashMap<>();
        collectionLock=new ReentrantLock();

    }
    public ReentrantLock getCollectionLock(){
        return collectionLock;
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
