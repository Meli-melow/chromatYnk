package com.example.appproject;

import java.security.Key;
import java.util.*;

public class MapCursor {

    //TODO : documentation

    private Map<Integer, Cursor> cursorMap; // HashMap pour stocker les objets Cursor par id

    public MapCursor() {
        this.cursorMap = new HashMap<>(); // Initialisation de la HashMap
    }


    // Méthode pour ajouter un Cursor à la HashMap
    public void addCursor(Cursor cursor) {
        this.cursorMap.put(cursor.getId(), cursor);
    }


    // Méthode pour supprimer un Cursor de la HashMap
    public void removeCursor(int id) {
        this.cursorMap.remove(id);
    }
    public Map<Integer, Cursor> getCursors() {
        return this.cursorMap;
    }


    // Méthode pour récupérer un Cursor par son id
    public Cursor getCursorById(int id) {
        return this.cursorMap.get(id); // Retourne le Cursor correspondant à l'id, ou null s'il n'existe pas
    }

    // Méthode pour récupérer tous les cursors de la HashMap
    public List<Cursor> getAllCursors() {
        return new ArrayList<>(this.cursorMap.values());
    }

    //Méthode pour obtenir un l'id disponible le plus petit
    public int smallestAvailableId(){
        Set<Integer> keySet = cursorMap.keySet();
        int smallestId = 0;
        while(smallestId>=Collections.min(keySet)){
            keySet.remove(smallestId);
            smallestId++;
        }
        return smallestId;
    }

    // Méthode pour effacer tous les cursors de la HashMap
    public void clearCursors() {
        cursorMap.clear();
    }
}
