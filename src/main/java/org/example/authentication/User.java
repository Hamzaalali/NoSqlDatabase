package org.example.authentication;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("username",username);
        jsonObject.put("password",password);
        return jsonObject;
    }
}
