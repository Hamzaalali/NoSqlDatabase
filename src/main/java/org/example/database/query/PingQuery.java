package org.example.database.query;

import org.example.database.query.DatabaseQuery;
import org.json.simple.JSONObject;

public class PingQuery extends DatabaseQuery {
    @Override
    public JSONObject execute(JSONObject query) {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        return clientMessage;
    }
}
