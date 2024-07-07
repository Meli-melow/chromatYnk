package com.example.appproject;

public class Num extends Variable{

    Double value;


    public Num(String id, Double value) {
        super(id);
        this.value = value;
    }


    public double getNumValue() {return this.value;}

}
