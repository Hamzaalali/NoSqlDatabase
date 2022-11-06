package org.example.index.types;

import org.example.database.collection.document.DocumentDataTypes;

import java.util.HashMap;
import java.util.Map;

public class IndexFactory {
    private Map<DocumentDataTypes,Index> indexMap;
    public IndexFactory(){
        indexMap=new HashMap<>();
    }

    public Map<DocumentDataTypes, Index> getIndexMap() {
        indexMap.put(DocumentDataTypes.INTEGER,new IntegerIndex());
        indexMap.put(DocumentDataTypes.STRING,new StringIndex());
        return indexMap;
    }
}
