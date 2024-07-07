package com.example.appproject;

/**
 * Bool Class inherits from Variable class.
 * This Class is used to create boolean variables.
 */


public class Bool extends Variable {

    /**
     * The value of the boolean variable.
     */
    boolean value;


    /**
     * This constructor creates a boolean variable.
     *
     * @param id
     * @param value
     */
    public Bool(String id, boolean value) {
        super(id);
        this.value = value;
    }

    /**
     * The getter
     *
     * @return value the boolean value of the <i>Bool</i> variable.
     */
    public boolean getBoolValue() {return this.value;}

    /**
     * @return boolean value of the Bool inside a String
     */
    @Override
    public String toString() {return "condition" + this.value;}

}
