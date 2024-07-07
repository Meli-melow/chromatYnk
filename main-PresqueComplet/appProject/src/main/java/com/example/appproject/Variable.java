package com.example.appproject;

public abstract class Variable {

    //TODO : documentation

    //TODO : gerer toutes les variables

    //TODO : gerer instructions invalides

    //TODO : tester avec 3 classes filles

    /*double value;

    String instruction;

    boolean condition;
    */
    Integer id;

    public Variable(int id) {
            this.id = id;}

    public int getVarId() {return this.id;}

    /*
    public Variable(double value) {
        //The value is omitted
        if(value == null)
            this.value = 0;

        else
            this.value = value;
    }

    public Variable(String instruction) {
        //The instruction is omitted
        if(instruction.equals(null))
            this.instruction = "";
        else
            this.instruction = instruction;
    }

    public Variable(boolean condition) {
        //The condition is omitted
        if(condition == null)
            this.condition = false;
        else
            this.condition = condition;
    }

    public double getVariable() {return this.value;}

    public void setVariable(double value) {this.value = value;}


    public String getVariable() {return this.instruction;}

    public void setVariable(String instruction) {this.instruction}

     */

}
