package org.example.exception;

public class InvalidSearchObjectException extends Exception{
    public InvalidSearchObjectException(){
        super("invalid search object syntax!");
    }
}
