/*
    This is Entity file
    Like User defined Datatype in custom Adapters

    Contains the Table Info, like columns name and Table name
 */
package com.example.notesapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
//  Just by DECLARING these vars, Room itself creates Columns for these.
//  This is same as done in SQLite using PRIMARY KEY AUTOINCREMENT which ensures uniqueness!!
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int priority;

    public Note(String title, String description, int priority) {
//      ID not selected as it will be automatically be handled by ROOM, using @PrimaryKey
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    //  Setters
    //  Room will use this method to set Id
    public void setId(int id) {
        this.id = id;
    }



    //  Getters
    //  For Room to persist these values in the DB, we need to write Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
