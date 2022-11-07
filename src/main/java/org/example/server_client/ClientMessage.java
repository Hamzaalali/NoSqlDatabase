package org.example.server_client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    int codeNumber;//0-> no errors , 1-> some error occurred
    String errorMessage;
    JSONObject data;

    JSONArray dataArray;
    public ClientMessage(){
        codeNumber=0;
    }
    public int getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(int codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONArray getDataArray() {
        return dataArray;
    }

    public void setDataArray(JSONArray dataArray) {
        this.dataArray = dataArray;
    }
}
