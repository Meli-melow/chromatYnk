package com.example.appproject;


/**
 * Str Class inherits from Variable class.
 * This Class is used to create string variables.
 */

public class Str extends Variable {

    String value;

    public Str(String id, String value) {
        super(id);
        this.value = value;
    }

    public String getStrValue() {return this.value;}
}
