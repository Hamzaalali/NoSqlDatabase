package org.example.database.query;

import org.example.database.Database;
import org.example.database.collection.Collection;
import org.example.file.system.DiskOperations;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class FindAllQuery extends DatabaseQuery {
    @Override
    public void execute(JSONObject query) {
        try {
            String databaseName= (String) query.get("databaseName");
            String collectionName= (String) query.get("collectionName");
            Database database=indexManager.getDatabases().get(databaseName);
            Collection collection=database.getCollections().get(collectionName);
            List<JSONObject>indexesList=collection.findAll();//get all indexes that exist in the id index ( to exclude the soft deleted documents)
            System.out.println(DiskOperations.readCollection(databaseName,collectionName,indexesList));//read from the disk
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
