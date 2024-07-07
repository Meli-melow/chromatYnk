package com.example.appproject;

import java.util.*;

public class MapVariable {

    //TODO : documentation

    private Map<Integer, Variable> varMap; // HashMap pour stocker les objets Variables par id

    public MapVariable() {
        this.varMap = new HashMap<>();
    }

    // Add a var to the HashMap
    public void addVariable(Variable var) {
        this.varMap.put(var.getVarId(), var);
    }


    // Remove a var from the HashMap
    public void removeVariable(int id) {
        this.varMap.remove(id);
    }
    public Map<Integer, Variable> getVariables() {
        return this.varMap;
    }


    // Get a var using its id
    public Variable getVariableById(int id) {
        return this.varMap.get(id);
    }

    // Get all vars that are in the HashMap
    public List<Variable> getAllVariables() {
        return new ArrayList<>(this.varMap.values());
    }

    //TODO : NB -> this is a MapVariable object
    public boolean constainsKey(String newId) {
        //TODO : NB -> is applied on varMap = HastTable object
        return (varMap.containsKey(newId));
    }

    // Erase HashMap
    public void clearVariables() {
        varMap.clear();
    }
}
