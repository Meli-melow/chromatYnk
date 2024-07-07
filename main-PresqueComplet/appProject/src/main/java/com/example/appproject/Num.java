package com.example.appproject;

public class Num extends Variable {

    Double value;


    public Num(int id, Double value) {
        super(id);
        this.value = value;
    }

    /*
    public Num(String val) {
        if(val != null)
    }
    */

    public double getNum() {return this.value;}

    public void setNum(double value) {this.value = value;}


}
