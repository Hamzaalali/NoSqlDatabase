package org.example.database;

import org.example.database.query.DatabaseQuery;
import org.example.database.query.factory.DatabaseQueryFactory;
import org.example.index.IndexManager;
import org.example.server_client.QueryType;
import org.json.simple.JSONObject;

import java.util.Map;

public class DatabaseQueryManager {
    private static volatile DatabaseQueryManager instance;

    private Map<QueryType, DatabaseQuery >databaseQueryMap;
    private DatabaseQueryManager()
    {
        DatabaseQueryFactory databaseQueryFactory=new DatabaseQueryFactory();
        databaseQueryMap=databaseQueryFactory.databaseQueryMap();
    }
    public JSONObject execute(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        databaseQueryMap.get(queryType).setQuery(query);
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        return databaseQuery.execute(query);
    }
    public static DatabaseQueryManager getInstance() {
        DatabaseQueryManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(DatabaseQueryManager.class) {
            if (instance == null) {
                instance = new DatabaseQueryManager();
            }
            return instance;
        }
    }
}
