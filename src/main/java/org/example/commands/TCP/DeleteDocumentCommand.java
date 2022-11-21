package org.example.commands.TCP;
import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.protocol.UDPCommunicator;
import org.example.services.DocumentServices;
import org.example.database.collection.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class DeleteDocumentCommand extends Command {
    @Override
    public JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            String documentId=CommandUtils.getDocumentId(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            if(collection.hasAffinity()==false&&!CommandUtils.isSync(commandJson)){
                redirectToNodeWithAffinity(collection,commandJson);
                return new JSONArray();
            }
            DocumentServices documentServices=new DocumentServices();
            documentServices.deleteDocument(collection,databaseName,collectionName,documentId);
            if(!CommandUtils.isSync(commandJson))
                UDPCommunicator.broadcastSyncCommand(commandJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
