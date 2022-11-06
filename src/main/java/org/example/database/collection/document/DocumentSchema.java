package org.example.database.collection.document;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DocumentSchema {
    private JSONObject schema;
    public DocumentSchema(JSONObject schema){
        verifySchemaObject(schema);
        this.schema = schema;
    }
    public void verify(JSONObject jsonObject){
        verifyJson(jsonObject, schema);
    }
    private void verifyJson(JSONObject jsonObject,JSONObject documentSchema){
        for (Object key : documentSchema.keySet()) {
            if(!jsonObject.containsKey(key)){
                throw new IllegalArgumentException();
            }
            Object documentJsonValue=documentSchema.get(key);
            Object jsonObjectValue=jsonObject.get(key);
            if(documentJsonValue.getClass()==JSONObject.class){
                verifyJson((JSONObject) jsonObjectValue,(JSONObject)documentJsonValue);
                continue;
            }
            if(documentJsonValue.getClass()== JSONArray.class|| documentJsonValue.getClass()==ArrayList.class){
                verifyJsonList((List<Object>)jsonObjectValue,(List<Object>)documentJsonValue);
                continue;
            }
            verifyJsonType(jsonObjectValue,documentJsonValue);
        }
    }
    private void verifyJsonList(List<Object> jsonList,List<Object>documentList){
        for (Object jsonObject:jsonList) {
            verifyJson((JSONObject) jsonObject,(JSONObject) documentList.get(0));
        }
    }
    private void verifyJsonType(Object jsonObjectValue,Object documentJsonValue){
        String dataType=(String) documentJsonValue;
        if(!Objects.equals(dataType.toLowerCase(), jsonObjectValue.getClass().getSimpleName().toLowerCase())){
            throw new IllegalArgumentException();
        }
    }
    public static void verifySchemaObject(JSONObject documentSchema){
        for (Object value : documentSchema.values()) {
            if(value.getClass()== JSONObject.class ){
                verifySchemaObject((JSONObject) value);
                continue;
            }
            if(value.getClass()== ArrayList.class || value.getClass()== JSONArray.class){
                verifySchemaList((List<Object>) value);
                continue;
            }
            isValidDataType((String)value);
        }
    }
    private static void verifySchemaList(List<Object> jsonSchemaList){
        if(jsonSchemaList.size()!=1){
            throw  new IllegalArgumentException();
        }
        verifySchemaObject((JSONObject) jsonSchemaList.get(0));
    }
    private static void isValidDataType(String dataType){
        for (DocumentDataTypes dt : DocumentDataTypes.values()) {
            if (Objects.equals(dt.toString(), dataType)) {
                return;
            }
        }
        throw new IllegalArgumentException();
    }
    public JSONObject searchForLeafProperty(JSONObject searchObject){
        return searchForLeafProperty(searchObject,schema);
    }
    private JSONObject searchForLeafProperty(JSONObject searchObject,JSONObject schemaObject){
        for (Object key : schemaObject.keySet()) {
            if(searchObject.containsKey(key)){
                Object schemaObjectValue=schemaObject.get(key);
                Object searchObjectValue=searchObject.get(key);
                if(schemaObjectValue.getClass()==JSONObject.class){
                    return searchForLeafProperty((JSONObject) searchObjectValue,(JSONObject)schemaObjectValue);
                }
                if(schemaObjectValue.getClass()==ArrayList.class || schemaObjectValue.getClass()== JSONArray.class){
                    return searchForListLeafProperty((List<Object>)searchObjectValue,(List<Object>)schemaObjectValue);
                }
                JSONObject propertyJson=new JSONObject();
                propertyJson.put("key",key);
                propertyJson.put("value",searchObjectValue);
                propertyJson.put("documentDataTypes",DocumentDataTypes.valueOf(searchObjectValue.getClass().getSimpleName().toUpperCase()));
                return propertyJson;
            }
        }
        return null;
    }
    private JSONObject searchForListLeafProperty(List<Object> searchObjectList,List<Object>schemaObjectList){
        if(searchObjectList.size()!=1){
            throw  new IllegalArgumentException();
        }
        return searchForLeafProperty((JSONObject) searchObjectList.get(0), (JSONObject) schemaObjectList.get(0));
    }
    public JSONObject getSchema() {
        return schema;
    }
}
