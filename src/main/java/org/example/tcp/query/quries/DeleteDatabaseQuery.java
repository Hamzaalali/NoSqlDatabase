package org.example.tcp.query.quries;

import org.example.database.Database;
import org.example.exception.NoDatabaseFoundException;
import org.example.file.system.DiskOperations;
import org.example.tcp.query.DatabaseQuery;
import org.json.simple.JSONObject;

import java.util.Optional;

public class DeleteDatabaseQuery extends DatabaseQuery {
    @Override
    public JSONObject execute() {
        JSONObject clientMessage=new JSONObject();
        clientMessage.put("code_number",0);
        isBroadcastable=true;
        indexManager.getDatabaseLock().lock();
        try{
            String databaseName=this.databaseName.orElseThrow(IllegalArgumentException::new);
            Optional<Database> database=getDatabase();
            database.orElseThrow(NoDatabaseFoundException::new);
            DiskOperations.deleteDatabase(databaseName);//hard delete
            indexManager.deleteDatabase(databaseName);//delete from the index structure
        } catch (Exception e) {
            clientMessage.put("code_number",1);
            clientMessage.put("error_message",e.getMessage());
        }
        indexManager.getDatabaseLock().lock();

        return clientMessage;
    }
}
