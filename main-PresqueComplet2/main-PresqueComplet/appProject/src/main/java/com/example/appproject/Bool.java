package com.example.appproject;

public class Bool extends Variable {

    boolean value;

    public Bool(String id, boolean value) {
        super(id);
        this.value = value;
    }

    public boolean getBoolValue() {return this.value;}
}
