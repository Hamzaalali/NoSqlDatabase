package org.example.database;

import org.example.database.collection.document.DocumentDataTypes;
import org.example.database.query.DatabaseQuery;
import org.example.database.query.factory.DatabaseQueryFactory;
import org.example.server_client.Query;
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
    public void execute(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        databaseQuery.execute(query);
    }
}
