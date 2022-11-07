package org.example.exception;

public class NoDocumentFoundException extends Exception{
    public NoDocumentFoundException(){
        super("No document found with this ID!");
    }
}
