package com.example.appproject;

/**
 * This Exception is thrown when a cursor is sent out of the drawing Pane.
 */

public class NewCursorException extends Exception{
    public NewCursorException(String message){super(message);}
}
