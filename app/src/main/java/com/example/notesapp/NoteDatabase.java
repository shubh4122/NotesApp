/*
    This is a place that connects:
    --> Entities, --> DAO

    Here instance of Database is created
 */

package com.example.notesapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

//SEE theres something about MIGRATION strategies for ROOM DB, do later??
// When we increment the version num we need to tell room how to migrate to new Schema -- If we dont do this, our app will CRASH
//Usually when app not in production we keep V at 1, and whenever change is made to DB Schema
//we uninstall and reinstall the app

//If wanna add more entities, use = {...., ...., ....}
//Version same as in SQLite,
// Whenever we make changes to DB, we need to update the version no. ???????? See about this!!
@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

//  This instance is created Coz we want to turn this class into a singleton!!
//  Singleton --> Means we cant create Multiple instances of this Database. Instead we always use the same instance everywhere
    private static NoteDatabase instance;

    /**
     * Abstract coz we dont give it any method body.
     * This method will later be used to Access our DAO
     * @return  NoteDao
     */
    public abstract NoteDao noteDao();

    /**
     * Again we need to create a singleton --------------- LEARN ABOUT SINGLETON!!
     * synchronized means ONLY 1 thread at a time can access this Method, This we u dont accidentally create 2 instances
     * of the DB when 2 different threads try to access it at the same time
     *
     * WE create NoteDB instance HERE
     */
    public static synchronized NoteDatabase getInstance(Context context) {
//      We check this, coz we want to create instance only when it doesnt exist already!
        if(instance == null) {
            /** instance = new Database();
             *  cant do this, coz of this is abstract class!
             *  Learn such Java concepts!
             */
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                        .fallbackToDestructiveMigration()//THis is when ver num increases, And app crashes, this will delete DB and create them from scratch! (??)
                        .addCallback(roomCallback)  //Attaching Callback. Learn more about Callbacks(??)
                        .build(); // Returns instance of this DB
        }
        return instance;
    }

    /**
     * We want when app is opened for the first time, and so does the DB, Blank screen must not show up!
     *
     * could do 2 things -
     * 1. as in Udacity, display a screen that says No Entries now
     * 2. Populate it with some Data already!
     *
     * Here we follow point 2
     *
     * Its static coz, we will call this in getInstance method which itself is static
     */

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            new PopulateDbAsyncTask(instance).execute();    // Executing AsyncTask
            super.onCreate(db);
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase noteDatabase) {
//          This is possible as it is being called after DB has been created
            this.noteDao = noteDatabase.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
//          Creating notes
            noteDao.insert(new Note("Title1", "Description1", 1));
            noteDao.insert(new Note("Title2", "Description2", 4));
            noteDao.insert(new Note("Title3", "Description3", 2));
            noteDao.insert(new Note("Title4", "Description4", 3));

            return null;
        }
    }
}
