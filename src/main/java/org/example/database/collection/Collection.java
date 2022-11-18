package org.example.database.collection;

import org.example.cluster.ClusterManager;
import org.example.index.Index;
import org.example.database.JsonUtils;
import org.example.load.balance.AffinityDistributor;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Collection {
    private Map<String , JSONObject>idIndex;//this json object should contain information about where the document is in the collection file
    private Map<String,Index>indexes;
    private ReentrantLock documentLock;
    private boolean hasAffinity;
    private int nodeWithAffinity;
    public Collection(){
        nodeWithAffinity=AffinityDistributor.getInstance().hasAffinity();
        hasAffinity= ClusterManager.getInstance().getNodeNumber() == nodeWithAffinity;
        indexes=new HashMap<>();
        idIndex=new HashMap<>();
        documentLock=new ReentrantLock();
    }

    public void addIndex(String property,Index index){
        indexes.put(property,index);
    }
    public ReentrantLock getDocumentLock(){
        return documentLock;
    }
    public void addToIndex(String property,Object key,String id){
        List<String> indexValue= (List<String>) indexes.get(property).getIndex().search((Comparable) key);
        if(indexValue!=null){
            indexValue.add(id);
        }else{
            List<String>valueList=new ArrayList<>();
            valueList.add(id);
            indexes.get(property).getIndex().insert((Comparable) key,valueList);
        }
    }
    public Optional<JSONObject> getIndex( String id){
        return Optional.ofNullable(idIndex.get(id));
    }
    public void addDocumentToIndexes(JSONObject document,JSONObject indexObject){
        idIndex.put((String)indexObject.get("id"),indexObject);//add to id index
        for(String property:indexes.keySet()){//add document to current indexes
            Object value= JsonUtils.searchForValue(document,indexes.get(property).getIndexPropertyObject());
            addToIndex( property,value, (String) document.get("id"));
        }
    }
    public Optional<List<String>> searchForIndex(String property,Object value){
        Index index=indexes.get(property);
        return Optional.ofNullable((List<String>) index.getIndex().search((Comparable) value));
    }
    public boolean hasIndex(String property){
        return (indexes.containsKey(property));
    }
    public void deleteDocument(JSONObject document){
        idIndex.remove(document.get("id"));
        removeFromAllIndexes(document);
    }
    public List<JSONObject> findAll(){
        return new ArrayList<>(idIndex.values());
    }


    private void removeFromAllIndexes(JSONObject document){
        for(Index index:indexes.values()){
            JSONObject indexPropertyJson=index.getIndexPropertyObject();
            Object value=JsonUtils.searchForValue(document,indexPropertyJson);
            List<String>idsList= (List<String>) index.getIndex().search((Comparable) value);
            idsList.remove(document.get("id"));
            if(idsList.size()==0){
                index.getIndex().delete((Comparable) value);
            }
        }
    }

    public boolean hasAffinity() {
        return hasAffinity;
    }

    public void setHasAffinity(boolean hasAffinity) {
        this.hasAffinity = hasAffinity;
    }

    public int getNodeWithAffinity() {
        return nodeWithAffinity;
    }

    public void setNodeWithAffinity(int nodeWithAffinity) {
        this.nodeWithAffinity = nodeWithAffinity;
    }
}
