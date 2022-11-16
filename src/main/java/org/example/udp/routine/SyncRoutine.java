package org.example.udp.routine;
import org.example.database.DatabaseQueryManager;
import org.json.simple.JSONObject;

public class SyncRoutine extends UdpRoutine {
    @Override
    public void execute(JSONObject routineJson) {
        JSONObject query= (JSONObject) routineJson.get("query");
        DatabaseQueryManager.getInstance().execute(query);
    }
}
