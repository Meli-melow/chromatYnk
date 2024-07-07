package com.example.appproject;

/**
 * This Exception is thrown when an incorrect instruction is being executed
 */

public class UnknownCommand extends Exception {
    public UnknownCommand(String message) {super(message);}
}
