package org.example.index.types;

import org.example.index.BPlusTree.BTree;

import java.util.List;

public class LongIndex extends Index{
    public LongIndex(){
        index=new BTree<Long, List<String>>();
    }
}
