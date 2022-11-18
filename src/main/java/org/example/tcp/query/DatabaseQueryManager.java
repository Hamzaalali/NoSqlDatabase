package org.example.tcp.query;

import org.example.tcp.query.factory.DatabaseQueryFactory;
import org.example.udp.UdpRoutineTypes;
import org.json.simple.JSONObject;

import java.util.Map;

public class DatabaseQueryManager {
    private Map<QueryType, DatabaseQuery >databaseQueryMap;
    private DatabaseQueryManager()
    {
        DatabaseQueryFactory databaseQueryFactory=new DatabaseQueryFactory();
        databaseQueryMap=databaseQueryFactory.databaseQueryMap();
    }
    public JSONObject syncExecute(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        databaseQueryMap.get(queryType).setQuery(query);
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        databaseQuery.setUdpRoutineTypes(UdpRoutineTypes.SYNC);
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
    public JSONObject redirectExecuteAndBroadcast(JSONObject query) {
        QueryType queryType= QueryType.valueOf((String) query.get("queryType"));
        databaseQueryMap.get(queryType).setQuery(query);
        DatabaseQuery databaseQuery=databaseQueryMap.get(queryType);
        databaseQuery.setUdpRoutineTypes(UdpRoutineTypes.QUERY_REDIRECT);
        JSONObject data= databaseQuery.execute();
        databaseQuery.broadcast();
        return data;
    }
    public static DatabaseQueryManager getInstance() {
        return new DatabaseQueryManager();
    }
}
