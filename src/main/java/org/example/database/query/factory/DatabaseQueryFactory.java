package org.example.database.query.factory;
import org.example.database.query.*;
import org.example.server_client.QueryType;
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
        return databaseQueryMap;
    }
}
