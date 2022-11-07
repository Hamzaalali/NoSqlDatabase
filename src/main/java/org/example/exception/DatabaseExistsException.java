package org.example.exception;

public class DatabaseExistsException extends Exception{
    public DatabaseExistsException(){
        super("Database Already Exists!");
    }
}
