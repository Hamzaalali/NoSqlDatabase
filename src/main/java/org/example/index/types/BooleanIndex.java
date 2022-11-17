package org.example.index.types;

import org.example.index.BPlusTree.BTree;

import java.util.List;

public class BooleanIndex extends Index{
    public BooleanIndex(){
        index=new BTree<Boolean, List<String>>();
    }
}
