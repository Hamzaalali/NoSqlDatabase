package org.example.database.collection;

import org.example.database.collection.document.DocumentDataTypes;
import org.example.index.BPlusTree.BTree;
import org.example.index.types.Index;
import org.example.json.JsonUtils;
import org.example.server_client.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class Collection {
    private Map<String , JSONObject>idIndex;//this json object should contain information about where the document is in the collection file
    private Map<String,Index>indexes;
    public Collection(){
        indexes=new HashMap<>();
        idIndex=new HashMap<>();
    }

    public void addIndex(String property,Index index){
        indexes.put(property,index);
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
    public JSONObject getIndex( String id){
        return idIndex.get(id);
    }
    public void addDocument(JSONObject document,JSONObject indexObject){
        idIndex.put((String)indexObject.get("id"),indexObject);
        for(String property:indexes.keySet()){//add document to current indexes
            Object value= JsonUtils.searchForValue(document,indexes.get(property).getIndexPropertyObject());
            addToIndex( property,value, (String) document.get("id"));
        }
    }
    public List<String> searchForIndex(String property,Object value){
        Index index=indexes.get(property);
        System.out.println();
        return (List<String>) index.getIndex().search((Comparable) value);
    }
    public boolean hasIndex(String property){
        return (indexes.containsKey(property));
    }
    public void deleteDocument(String id){
        idIndex.remove(id);

    }
    public List<JSONObject> findAll(){
        return new ArrayList<>(idIndex.values());
    }

    public void deleteFromIndex(String property,Object key ){
        indexes.get(property).getIndex().delete((Comparable) key);
    }
    public void updateDocument(JSONObject document,JSONObject newValue){

    }
}
