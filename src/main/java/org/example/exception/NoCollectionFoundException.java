package org.example.exception;

public class NoCollectionFoundException extends Exception{
    public NoCollectionFoundException(){
        super("No collection found with this name!");
    }
}
