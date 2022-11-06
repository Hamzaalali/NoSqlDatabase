package org.example.index.types;

import org.example.index.BPlusTree.BTree;
import org.example.json.JsonUtils;
import org.json.simple.JSONObject;

public abstract class Index {
    protected BTree index;
    protected JSONObject indexPropertyObject;
    public BTree getIndex() {
        return index;
    }

    public void setIndex(BTree index) {
        this.index = index;
    }

    public JSONObject getIndexPropertyObject() {
        return indexPropertyObject;
    }

    public void setIndexPropertyObject(JSONObject indexObject) {
        this.indexPropertyObject = indexObject;
    }
}
