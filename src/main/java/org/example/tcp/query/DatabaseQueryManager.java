package org.example.tcp.query;

import org.example.tcp.query.factory.DatabaseQueryFactory;
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
        databaseQuery.setCheckForAffinity(false);
        return databaseQuery.execute();
    }

    public JSONObject executeAndBroadcast(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        databaseQueryMap.get(queryType).setQuery(query);
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        JSONObject data= databaseQuery.execute();
        databaseQuery.broadcast();
        return data;
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
