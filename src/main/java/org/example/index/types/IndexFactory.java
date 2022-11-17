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
        indexMap.put(DocumentDataTypes.LONG,new LongIndex());
        indexMap.put(DocumentDataTypes.STRING,new StringIndex());
        indexMap.put(DocumentDataTypes.DOUBLE,new DoubleIndex());
        indexMap.put(DocumentDataTypes.BOOLEAN,new BooleanIndex());
        return indexMap;
    }
}
