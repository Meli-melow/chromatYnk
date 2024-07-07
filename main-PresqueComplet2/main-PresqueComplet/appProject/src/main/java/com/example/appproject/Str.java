package com.example.appproject;

public class Str extends Variable {

    String value;

    public Str(String id, String value) {
        super(id);
        this.value = value;
    }

    public String getStrValue() {return this.value;}
}
