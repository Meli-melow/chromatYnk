package com.example.appproject;

import java.util.*;
import java.lang.*;

/**
 * MapVariable is used to stock in a HashMap and manage all the variables created.
 */
public class MapVariable {
    //TODO : documentation

    /**
     * The map stocks the Cursor objects by name as Key.
     */
    private Map<String, Variable> varMap;

    public MapVariable() {
        this.varMap = new HashMap<>();
    }

    /**
     * Method to add a new variable to the Map.
     * @param var
     */
    public void addVariable(Variable var) {
        this.varMap.put(var.getVarId(), var);
    }


    /**
     * Deletes the variable corresponding to name.
     * @param name Key of the variable in the Map
     */
    //TODO : check
    //TODO ; exception
    public void removeVariable(String name) throws IllegalArgumentException {
        if (!varMap.containsKey(name)) {
            throw new IllegalArgumentException("The key does not exist");
        }
        this.varMap.remove(name);
    }

    /**
     * Returns the List of all variables in the Map
     * @return
     */
    public Map<String, Variable> getVariableMap() {
        return this.varMap;
    }


    /**
     * Returns the Variable object designated by its name.
     * @param name
     * @return
     */
    public Variable getVariableById(String name) {
        return this.varMap.get(name);
    }

    /**
     * Returns a List of all values contained in the Map.
     * @return
     */
    public List<Variable> getAllValues() {
        return new ArrayList<>(this.varMap.values());
    }

    /**
     * Delete the Map.
     */
    public void clearVariables() {
        varMap.clear();
    }

    /**
     * Returns true if a variable named as "varName" already exists in the Map.
     * @param varName
     * @return
     */
    public boolean containsKey(String varName) {
         return (varMap.containsKey(varName));
    }

}
