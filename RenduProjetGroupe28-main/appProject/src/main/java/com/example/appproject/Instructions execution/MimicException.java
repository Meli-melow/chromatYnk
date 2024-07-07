package com.example.appproject;

/**
 * This Exception is thrown when the execution of a MIMIC command goes wrong
 */

public class MimicException extends Exception{
    public MimicException(String message){super(message);}
}
