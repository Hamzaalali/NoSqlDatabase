package org.example.commands.TCP;

import org.example.commands.Command;
import org.example.commands.CommandTypes;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.DocumentServices;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

public class UpdateDocumentCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            JSONObject data=CommandUtils.getData(commandJson);
            JSONObject document = CommandUtils.getDocument(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            if(!collection.hasAffinity()){
                long version= (long) document.get("_version");
                data.put("_version",version);
                commandJson.put("data",data);
                redirectToNodeWithAffinity(collection,commandJson);
            }else{
                updateIfHasAffinity(commandJson, databaseName, collectionName, data, document, collection);
                commandJson.put("commandType", CommandTypes.SYNC_UPDATE_DOCUMENT.toString());
                UDPCommunicator.broadcastCommand(commandJson);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
    private void updateIfHasAffinity(JSONObject commandJson, String databaseName, String collectionName, JSONObject data, JSONObject document, Collection collection) throws IOException {
        boolean update=true;
        boolean isRedirected= commandJson.containsKey("isRedirected");
        if(isRedirected){
            if(!isSameVersion(data, document)){
                update=true;
            }
        }
        if(update){
            DocumentServices documentServices=new DocumentServices();
            documentServices.updateDocument(document, data, collection, databaseName, collectionName);
        }
    }
    private boolean isSameVersion(JSONObject commandJson, JSONObject document) {
        long documentVersion= (long) document.get("_version");
        long queryVersion= (long) commandJson.get("_version");
        if( queryVersion== documentVersion){
            return true;
        }
        return false;
    }

}
