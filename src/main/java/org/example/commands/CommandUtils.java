package org.example.commands;
import org.example.exception.*;
import org.example.index.IndexManager;
import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.file.DiskOperations;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Optional;

public class CommandUtils {

    public static Database getDatabase(JSONObject commandJson) throws DatabaseNotFoundException {
        Optional<Database> database= IndexManager.getInstance().getDatabase(getDatabaseName(commandJson));;
        return database.orElseThrow(DatabaseNotFoundException::new);
    }
    public static Collection getCollection(JSONObject commandJson) throws DatabaseNotFoundException, CollectionNotFoundException {
        Database database = getDatabase(commandJson);
        String collectionName=getCollectionName(commandJson);
        Optional<Collection> collection=database.getCollection(collectionName);
        return collection.orElseThrow(CollectionNotFoundException::new);
    }
    public static JSONObject getDocument(JSONObject commandJson) throws CollectionNotFoundException, IOException, ParseException, DocumentNotFoundException, DatabaseNotFoundException, DocumentIdNotFoundException {
        Optional<JSONObject> indexObject= getCollection(commandJson).getIndex(getDocumentId(commandJson));
        JSONObject document= DiskOperations.readDocument(getDatabaseName(commandJson),getCollectionName(commandJson),indexObject.orElseThrow(DocumentNotFoundException::new));
        return document;
    }
    public static String getDatabaseName(JSONObject commandJson) throws DatabaseNotFoundException {
        Optional<String> databaseName= Optional.ofNullable((String) commandJson.get("databaseName"));
        return databaseName.orElseThrow(DatabaseNotFoundException::new);
    }
    public static String getCollectionName(JSONObject commandJson) throws CollectionNotFoundException {
        Optional<String>collectionName= Optional.ofNullable((String) commandJson.get("collectionName"));
        return collectionName.orElseThrow(CollectionNotFoundException::new);
    }
    public static JSONObject getDocumentJson(JSONObject commandJson) throws DocumentNotFoundException {
        Optional<JSONObject> documentJson=Optional.ofNullable((JSONObject) commandJson.get("document"));
        return documentJson.orElseThrow(DocumentNotFoundException::new);
    }
    public static JSONObject getSchemaJson(JSONObject commandJson) throws SchemaNotFoundException {
        Optional<JSONObject>schema=Optional.ofNullable((JSONObject) commandJson.get("schema"));
        return schema.orElseThrow(SchemaNotFoundException::new);
    }
    public static JSONObject getIndexProperty(JSONObject commandJson) throws IndexPropertyNotFoundException {
        Optional<JSONObject>indexPropertyJson=Optional.ofNullable((JSONObject) commandJson.get("indexProperty"));
        return indexPropertyJson.orElseThrow(IndexPropertyNotFoundException::new);
    }
    public static JSONObject getSearchObject(JSONObject commandJson) throws SearchObjectNotFoundException {
        Optional<JSONObject>searchObject=Optional.ofNullable((JSONObject) commandJson.get("searchObject"));
        return searchObject.orElseThrow(SearchObjectNotFoundException::new);
    }
    public static String getDocumentId(JSONObject commandJson) throws DocumentIdNotFoundException {
        Optional<String>documentId=Optional.ofNullable((String) commandJson.get("documentId"));
        return documentId.orElseThrow(DocumentIdNotFoundException::new);
    }
    public static JSONObject getData(JSONObject commandJson) throws DataNotFoundException {
        Optional<JSONObject> data=Optional.ofNullable((JSONObject) commandJson.get("data"));
        return data.orElseThrow(DataNotFoundException::new);
    }
    public static CommandTypes getCommandType(JSONObject commandJson) {
        return CommandTypes.valueOf((String) commandJson.get("commandType"));
    }
    public static boolean isSync(JSONObject commandJson){
        Optional<Boolean>isSync=Optional.of((boolean) commandJson.get("sync"));
        if(isSync.isEmpty()){
            return false;
        }
        return isSync.get();
    }
}
