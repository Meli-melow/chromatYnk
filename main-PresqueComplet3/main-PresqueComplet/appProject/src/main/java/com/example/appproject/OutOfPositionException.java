package com.example.appproject;

/**
 * This Exception is thrown when a cursor is sent out of the drawing Pane.
 */

public class OutOfPositionException extends Exception {
    public OutOfPositionException(String message) {
        super(message);
    }
}
