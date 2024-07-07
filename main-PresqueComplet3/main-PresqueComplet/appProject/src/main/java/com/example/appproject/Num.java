package com.example.appproject;

import java.util.regex.*;


/**
 * Num Class inherits from Variable class.
 * This Class is used to create numerical variables.
 */

public class Num extends Variable {

    Double value;

    /**
     * This constructor creates a boolean variable.
     *
     * @param id
     * @param value
     */
    public Num(String id, Double value) {
        super(id);
        this.value = value;
    }

    /**
     * The getter
     *
     * @return value the float value of the <i>Num</i> variable.
     */
    public double getNumValue() {return this.value;}

    /**
     * @return float value of the Num inside a String
     */
    @Override
    public String toString() {return "float : " + this.value;}

    public static boolean isName(String identifier) {

        // Regex to check valid identifier.
        String regex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";

        // Compile the ReGex ~ implements
        Pattern p = Pattern.compile(regex);

        // If the identifier is empty
        // return false
        if (identifier == null) {
            System.out.println("Name is empty \n");
            return false;
        }

        // Pattern class contains matcher() method
        // to indicate if given identifier
        // mathches regular expression.
        Matcher m = p.matcher(identifier);

        // Return if the identifier
        // matched the ReGex
        return m.matches();

    }

}
