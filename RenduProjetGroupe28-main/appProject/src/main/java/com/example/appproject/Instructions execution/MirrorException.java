package com.example.appproject;

/**
 * This Exception is thrown when the execution of a MIRROR command goes wrong
 */

public class MirrorException extends Exception{
    public MirrorException(String message){super(message);}
}
