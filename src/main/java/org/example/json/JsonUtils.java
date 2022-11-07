package org.example.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonUtils {
    public static Object searchForValue(JSONObject searchObject,JSONObject referenceObject){
        for (Object key : referenceObject.keySet()) {
            Object referenceObjectValue=searchObject.get(key);
            Object searchObjectValue=searchObject.get(key);
            if(referenceObjectValue.getClass()==JSONObject.class){
                return searchForValue((JSONObject) searchObjectValue,(JSONObject)referenceObjectValue);
            }
            if(referenceObjectValue.getClass()== ArrayList.class||referenceObjectValue.getClass()== JSONArray.class){
                return searchForListValue((List<Object>)searchObjectValue,(List<Object>)referenceObjectValue);
            }
            return searchObjectValue;
        }
        return null;
    }
    private static Object searchForListValue(List<Object> searchObjectList,List<Object>schemaObjectList){
        if(searchObjectList.size()!=1){
            throw  new IllegalArgumentException();
        }
        return searchForValue((JSONObject) searchObjectList.get(0), (JSONObject) schemaObjectList.get(0));
    }
    public static void updateJsonObject(JSONObject oldJsonObject,JSONObject newJsonObject){
        for (Object key : newJsonObject.keySet()) {
            Object newJsonObjectValue=newJsonObject.get(key);
            Object oldJsonObjectValue=oldJsonObject.get(key);
            if(oldJsonObjectValue.getClass()==JSONObject.class){
                updateJsonObject((JSONObject) oldJsonObjectValue,(JSONObject)newJsonObjectValue);
                continue;
            }
            if(oldJsonObjectValue.getClass()== ArrayList.class||oldJsonObjectValue.getClass()== JSONArray.class){
                 updateJsonList((List<JSONObject>)oldJsonObjectValue,(List<JSONObject>)newJsonObjectValue);
                 continue;
            }
            oldJsonObject.remove(key);
            oldJsonObject.put(key,newJsonObjectValue);
        }
    }
    private static void updateJsonList(List<JSONObject> oldJsonObjectList,List<JSONObject>newJsonObjectList){
        for(int i=0;i< newJsonObjectList.size();i++){
            updateJsonObject( oldJsonObjectList.get(i),  newJsonObjectList.get(i));
        }
    }
}
