package org.example.commands.TCP;
import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.services.SearchServices;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class FindAllCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        JSONArray data=new JSONArray();
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            SearchServices searchServices=new SearchServices();
            data= searchServices.findAll(collection,databaseName,collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
