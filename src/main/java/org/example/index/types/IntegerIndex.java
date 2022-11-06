package org.example.index.types;

import org.example.index.BPlusTree.BTree;

import java.util.List;

public class IntegerIndex extends Index{
    public IntegerIndex(){
        index=new BTree<Integer, List<String>>();
    }
}
