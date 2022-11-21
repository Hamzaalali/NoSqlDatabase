package org.example.commands.TCP;

import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.CollectionServices;
import org.example.database.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DeleteCollectionCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            Database database= CommandUtils.getDatabase(commandJson);
            CollectionServices collectionServices=new CollectionServices();
            collectionServices.deleteCollection(database,databaseName,collectionName);
            if(!CommandUtils.isSync(commandJson))
                UDPCommunicator.broadcastSyncCommand(commandJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
