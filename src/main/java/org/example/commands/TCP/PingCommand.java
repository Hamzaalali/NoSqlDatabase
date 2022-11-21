package org.example.commands.TCP;

import org.example.commands.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PingCommand extends Command {
    @Override
    protected JSONArray execute(JSONObject commandJson) {
        return new JSONArray();
    }
}
