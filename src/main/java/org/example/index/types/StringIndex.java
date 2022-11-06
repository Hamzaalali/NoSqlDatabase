package org.example.index.types;

import org.example.index.BPlusTree.BTree;
import org.example.index.IndexManager;

import java.util.List;

public class StringIndex extends Index {
    public StringIndex(){
        index=new BTree<String, List<String>>();
    }
}
