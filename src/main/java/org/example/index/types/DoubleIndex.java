package org.example.index.types;

import org.example.index.BPlusTree.BTree;

import java.util.List;

public class DoubleIndex extends Index{
    public DoubleIndex(){
        index=new BTree<Double, List<String>>();
    }
}
