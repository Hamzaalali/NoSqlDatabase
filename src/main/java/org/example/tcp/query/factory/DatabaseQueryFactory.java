package org.example.tcp.query.factory;
import org.example.tcp.query.QueryType;
import org.example.tcp.query.DatabaseQuery;
import org.example.tcp.query.quries.*;
import java.util.HashMap;
import java.util.Map;
public class DatabaseQueryFactory {
    public Map<QueryType, DatabaseQuery> databaseQueryMap(){
        Map<QueryType,DatabaseQuery> databaseQueryMap=new HashMap<>();
        databaseQueryMap.put(QueryType.CREATE_DATABASE,new CreateDatabaseQuery());
        databaseQueryMap.put(QueryType.CREATE_COLLECTION,new CreateCollectionQuery());
        databaseQueryMap.put(QueryType.CREATE_DOCUMENT,new CreateDocumentQuery());
        databaseQueryMap.put(QueryType.CREATE_INDEX,new CreateIndexQuery());
        databaseQueryMap.put(QueryType.FIND,new FindQuery());
        databaseQueryMap.put(QueryType.FIND_ALL,new FindAllQuery());
        databaseQueryMap.put(QueryType.DELETE_DATABASE,new DeleteDatabaseQuery());
        databaseQueryMap.put(QueryType.DELETE_COLLECTION,new DeleteCollectionQuery());
        databaseQueryMap.put(QueryType.DELETE_DOCUMENT,new DeleteDocumentQuery());
        databaseQueryMap.put(QueryType.UPDATE_DOCUMENT,new UpdateDocumentQuery());
        databaseQueryMap.put(QueryType.PING,new PingQuery());
        return databaseQueryMap;
    }
}
