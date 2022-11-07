package org.example.database.query;

import org.example.index.IndexManager;
import org.example.server_client.ClientMessage;
import org.json.simple.JSONObject;

public abstract class DatabaseQuery {
    protected IndexManager indexManager;
    public DatabaseQuery(){
        indexManager=IndexManager.getInstance();
    }
    public abstract ClientMessage execute(JSONObject query);

}
