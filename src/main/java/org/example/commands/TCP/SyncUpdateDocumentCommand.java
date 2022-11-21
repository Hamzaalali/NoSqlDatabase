package org.example.commands.TCP;

import org.example.commands.Command;
import org.example.commands.CommandUtils;
import org.example.database.collection.Collection;
import org.example.services.DocumentServices;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SyncUpdateDocumentCommand extends Command {

    @Override
    protected JSONArray execute(JSONObject commandJson) {
        try{
            String databaseName= CommandUtils.getDatabaseName(commandJson);
            String collectionName=CommandUtils.getCollectionName(commandJson);
            JSONObject data=CommandUtils.getData(commandJson);
            JSONObject document = CommandUtils.getDocument(commandJson);
            Collection collection=CommandUtils.getCollection(commandJson);
            DocumentServices documentServices=new DocumentServices();
            documentServices.updateDocument(document, data, collection, databaseName, collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JSONArray();
    }
}
