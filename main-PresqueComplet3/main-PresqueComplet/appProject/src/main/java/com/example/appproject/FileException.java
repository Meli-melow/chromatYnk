package com.example.appproject;


/**
 * This Exception is thrown when the execution of a file goes wrong.
 */

public class FileException extends Exception {
    public FileException(String message) {
        super(message);
    }
}
