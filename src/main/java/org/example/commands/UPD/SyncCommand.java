package org.example.commands.UPD;

import org.example.commands.Command;
import org.example.commands.CommandsMediator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SyncCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        JSONObject syncCommandJson= (JSONObject) commandJson.get("command");
        syncCommandJson.put("sync",true);
        CommandsMediator.getInstance().execute(syncCommandJson);
        return new JSONArray();
    }
}
