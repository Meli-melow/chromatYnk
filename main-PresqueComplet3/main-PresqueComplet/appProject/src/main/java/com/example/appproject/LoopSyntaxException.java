package com.example.appproject;

/**
 * This Exception is thrown when the interpreter identifies an incorrect syntax inside a loop
 */

public class LoopSyntaxException extends Exception{
    public LoopSyntaxException(String message){super(message);}
}