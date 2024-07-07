package com.example.appproject;

import java.util.regex.*;


/**
 * This Class is used to create variables.
 * They are identified by their name.
 */

public class Variable {

    String id;

    /**
     * This constructor creates a variable.
     *
     * @param id the name given to the variable.
     */
    public Variable(String id) {
        this.id = id;}

    /**
     * The getter
     *
     * @return id name of the <i>Variable</i>.
     */
    public String getVarId() {return this.id;}

    /**
     * This method validates the identifier.
     *
     * @param identifier
     * @throws IllegalArgumentException
     */
    public static void isValidName(String identifier) throws IllegalArgumentException {

        // Regex to check valid identifier.
        String regex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the identifier is empty
        // return false
        if (identifier == null) {
            throw new IllegalArgumentException("Variable must have a name");
        }

        // Pattern class contains matcher() method
        // to find matching between given identifier
        // and regular expression.
        Matcher m = p.matcher(identifier);

        if (!m.matches()) {
            throw new IllegalArgumentException("Please enter a valid name");
        }

    }

}
