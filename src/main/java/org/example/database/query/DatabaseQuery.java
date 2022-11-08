package org.example.database.query;

import org.example.index.IndexManager;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

public abstract class DatabaseQuery {
    protected IndexManager indexManager;
    protected String databaseName;
    protected String collectionName;
    protected JSONObject document;
    protected JSONObject schema;
    protected JSONObject indexPropertyObject;
    protected JSONObject searchObject;
    protected String documentId;
    protected JSONObject data;
    public DatabaseQuery(){
        indexManager=IndexManager.getInstance();
    }
    public abstract ClientMessage execute(JSONObject query);
    public void setQuery(JSONObject query){
        databaseName= (String) query.get("databaseName");
        collectionName= (String) query.get("collectionName");
        document=(JSONObject) query.get("document");
        schema=(JSONObject) query.get("schema");
        indexPropertyObject=(JSONObject) query.get("indexProperty");
        searchObject=(JSONObject) query.get("searchObject");
        documentId=(String) query.get("documentId");
        data=(JSONObject) query.get("data");
    }

}
