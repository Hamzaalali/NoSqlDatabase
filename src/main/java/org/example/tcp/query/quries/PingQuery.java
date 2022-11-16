package org.example.tcp.query.quries;

import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONObject;

public class PingQuery extends DatabaseQuery {
    @Override
    public JSONObject execute(JSONObject query) {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        return clientMessage;
    }
}
