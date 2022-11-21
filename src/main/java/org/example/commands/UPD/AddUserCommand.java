package org.example.commands.UPD;

import org.example.authentication.AuthenticationManager;
import org.example.commands.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AddUserCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        String password= (String) commandJson.get("password");
        String username= (String) commandJson.get("username");
        AuthenticationManager.getInstance().addUser(username,password);
        return new JSONArray();
    }
}
