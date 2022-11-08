package org.example.database;

import org.example.database.query.DatabaseQuery;
import org.example.database.query.factory.DatabaseQueryFactory;
import org.example.server_client.ClientMessage;
import org.example.server_client.QueryType;
import org.json.simple.JSONObject;

import java.util.Map;

public class DatabaseFacade {
    private Map<QueryType, DatabaseQuery >databaseQueryMap;
    public DatabaseFacade()
    {
        DatabaseQueryFactory databaseQueryFactory=new DatabaseQueryFactory();
        databaseQueryMap=databaseQueryFactory.databaseQueryMap();
    }
    public ClientMessage execute(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        databaseQueryMap.get(queryType).setQuery(query);
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        return databaseQuery.execute(query);
    }
}
