package org.example.exception;

public class DocumentNotFoundException extends Exception{
    public DocumentNotFoundException(){
        super("Document Not Found!");
    }
}
