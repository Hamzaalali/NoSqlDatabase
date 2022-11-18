package org.example.exception;

public class ConnectionTerminatedException extends Exception{
    public ConnectionTerminatedException(){
        super("connection terminated");
    }
}
