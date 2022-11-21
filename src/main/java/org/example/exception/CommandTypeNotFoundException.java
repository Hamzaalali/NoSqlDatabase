package org.example.exception;

public class CommandTypeNotFoundException extends Exception{
    public CommandTypeNotFoundException(){
        super("Command Type Not Found!");
    }
}
