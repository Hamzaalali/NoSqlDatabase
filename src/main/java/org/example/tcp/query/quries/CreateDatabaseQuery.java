package org.example.tcp.query.quries;

import org.example.exception.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONObject;

public class CreateDatabaseQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        isBroadcastable=true;
        clientMessage.put("code_number",0);
        indexManager.getDatabaseLock().lock();
        try{
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            DiskOperations.createDatabase(databaseName);
            indexManager.addDatabase(databaseName);
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        indexManager.getDatabaseLock().unlock();

        return clientMessage;
    }
}
