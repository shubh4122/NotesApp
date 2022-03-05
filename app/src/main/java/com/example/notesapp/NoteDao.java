/*
    DAO file
    contains all the operations that we would perform on a given Table/DB
    like insert, Update, delete etc
 */

package com.example.notesapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

//  Here we declare all the methods for the operations we want to perform
//  And Annotate it accordingly, Room itself does work for us

//  To insert
    @Insert // This insert will Make room do insertion task
    void insert(Note note); // We do insert operation on a 'Note'

    @Update
    void update(Note note);

//  Looking at source code, u may see we may pass multiple types of args
//  like, Var args, lists, and one shown below, so on.
    @Delete
    void delete(Note note);

//  Now we may want to perform some actions that may not be provided using @Annotation
//  For this we use @Query -- To make our own Query/Task we want to perform

    /**
     * Why room is better than SQLite?
     *
     * Suppose we made some error(Typo) in writing below query,
     * Our class wont compile and would mark it with red Underline to show there's and error
     *
     * If it were SQLite, this would compile and start normally, but when this query
     * would have been run, would have crashed App, Or memory Leakage etc.!
     */
    @Query("DELETE FROM note_table") // The query we wrote Means delete all the notes from note_table
    void deleteAllNotes();

//  This method returns all the notes, so we can receive them and show in RecyclerView
//  Also at compile time, Room will check note_table columns are in Note class, if not it'll throw error
    @Query("SELECT * FROM note_table ORDER BY priority ASC")
    /**
     * ROOM can return LiveData
     * Difference is, now we can Observe this list here
     * As soon as any changes happen in note_table, they will be updated and Activity will be notified!!
     * ROOM takes all necessary stuff to handle this Livedata itself
     * so we don't have to do anything
     */
    LiveData<List<Note>> getAllNotes();
}
