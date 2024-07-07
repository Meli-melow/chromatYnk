package com.example.appproject;

import java.security.Key;
import java.util.*;

/**
 * MapCursor is used to stock in a HashMap and manage all the cursors available.
 */
public class MapCursor {

    /**
     * The map stocks the Cursor objects by id (CursorId) as Key.
     */
    private Map<Integer, Cursor> cursorMap;

    public MapCursor() {
        this.cursorMap = new HashMap<>(); // Initialisation
    }

    /**
     * Method to add a new cursor to the Map, with the selected id.
     * @param cursor
     */
    public void addCursor(Cursor cursor) {
        this.cursorMap.put(cursor.getId(), cursor);
    }

    /**
     * Deletes the cursor corresponding to id.
     * @param id Key of the cursor in the Map
     */
    public void removeCursor(int id) {
        this.cursorMap.remove(id);
    }
    public Map<Integer, Cursor> getCursors() {
        return this.cursorMap;
    }

    /**
     * Returns the Cursor object designated by its id.
     * @param id
     * @return
     */
    public Cursor getCursorById(int id) {
        return this.cursorMap.get(id); // Returns null if no cursor is matching the id
    }

    /**
     * Returns the List of all cursors in the Map
     * @return
     */
    public List<Cursor> getAllCursors() {
        return new ArrayList<>(this.cursorMap.values());
    }

    /**
     * The method is used to know the smallest unused Key of the Map.
     * @return the smallest available CursorId/Key.
     */
    public int smallestAvailableId() {
        Set<Integer> keySet = cursorMap.keySet();
        int smallestId = 1;
        while (keySet.contains(smallestId)) {
            smallestId++;
        }
        return smallestId;
    }


    /**
     * Clear all cursors from the Map
     */
    public void clearCursors() {
        cursorMap.clear();
    }
}
