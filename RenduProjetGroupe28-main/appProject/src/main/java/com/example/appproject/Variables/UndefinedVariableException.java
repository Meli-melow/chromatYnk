package com.example.appproject;

/**
 * This Exception is throws when an undefined variable is used inside an instruction.
 */

public class UndefinedVariableException extends Exception{
    public UndefinedVariableException(String message) {super(message);}

}

