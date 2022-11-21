package org.example.commands.TCP;
import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.IndexServices;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class CreateIndexCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            Database database= CommandUtils.getDatabase(commandJson);
            JSONObject indexPropertyObject=CommandUtils.getIndexProperty(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            IndexServices indexServices=new IndexServices();
            indexServices.createIndex(indexPropertyObject,database,collection,databaseName,collectionName);
            if(!CommandUtils.isSync(commandJson))
                UDPCommunicator.broadcastSyncCommand(commandJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
