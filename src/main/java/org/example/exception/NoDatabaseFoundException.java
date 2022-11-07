package org.example.exception;

public class NoDatabaseFoundException extends Exception{
    public NoDatabaseFoundException(){
        super("No database found with this name!");
    }
}
