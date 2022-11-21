package org.example.commands.UPD;
import org.example.commands.Command;
import org.example.commands.CommandsMediator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RedirectCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        JSONObject redirectCommandJson= (JSONObject) commandJson.get("command");
        commandJson.put("isRedirected",true);
        CommandsMediator.getInstance().execute(redirectCommandJson);//TODO
        return new JSONArray();
    }
}
