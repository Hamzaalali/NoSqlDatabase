package org.example.exception;

public class DocumentIdNotFoundException extends Exception{
    public DocumentIdNotFoundException(){
        super("Document Id Not Found");
    }
}
