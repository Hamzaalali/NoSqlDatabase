package org.example.database.query;

import org.example.index.IndexManager;
import org.json.simple.JSONObject;

public abstract class DatabaseQuery {
    protected IndexManager indexManager;
    public DatabaseQuery(){
        indexManager=IndexManager.getInstance();
    }
    public abstract void execute(JSONObject query);

}
