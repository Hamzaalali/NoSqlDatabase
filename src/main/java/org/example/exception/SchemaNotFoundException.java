package org.example.exception;

public class SchemaNotFoundException extends Exception{
    public SchemaNotFoundException(){
        super("Schema Not Found!");
    }
}
