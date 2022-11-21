package org.example.commands.TCP;
import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.DatabaseServices;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateDatabaseCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            DatabaseServices databaseServices=new DatabaseServices();
            databaseServices.createDatabase(databaseName);
            if(!CommandUtils.isSync(commandJson))
                UDPCommunicator.broadcastSyncCommand(commandJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
