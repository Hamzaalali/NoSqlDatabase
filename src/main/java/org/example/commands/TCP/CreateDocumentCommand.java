package org.example.commands.TCP;
import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.DocumentServices;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

public class CreateDocumentCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            JSONObject document=CommandUtils.getDocumentJson(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            if(collection.hasAffinity()==false&&!CommandUtils.isSync(commandJson)){
                redirectToNodeWithAffinity(collection,commandJson);
                return new JSONArray();
            }
            UUID uuid = UUID.randomUUID();
            if(!document.containsKey("id")) {
                document.put("id",uuid.toString());
            }
            document.put("_version",0);
            commandJson.put("document",document);
            DocumentServices documentServices=new DocumentServices();
            documentServices.createDocument(document,collection,databaseName,collectionName);
            if(!CommandUtils.isSync(commandJson))
                UDPCommunicator.broadcastSyncCommand(commandJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
