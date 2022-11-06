package org.example.server_client;

import org.json.simple.JSONObject;

import java.io.Serializable;

public class Query implements Serializable {
    private String databaseName;
    private String collectionName;
    private QueryType queryType;
    private JSONObject jsonData;
    private SearchObject searchObject;
    private String documentId;
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public SearchObject getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(SearchObject searchObject) {
        this.searchObject = searchObject;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return "Query{" +
                "databaseName='" + databaseName + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", queryType=" + queryType +
                ", jsonData=" + jsonData +
                ", searchObject=" + searchObject +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}
