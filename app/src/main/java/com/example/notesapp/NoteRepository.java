package com.example.notesapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    /**
     * To initialize above member vars
     * @param application -  Subclass of Context (?) See more on this
     *                    ViewModel will also use this
     */
    public NoteRepository(Application application) {
//      This getInstance is one we created back in NoteDatabase
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
//      Normally we cant call abstract Methods COZ they dont have a body
//      But the builder we used in NoteDatabase, will auto generate necessary code for noteDao method
//      ---> Room subclasses our abstract class
//      Hence we can call it!
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes(); // Method runs our QUERY!
    }

//  Now we create methods for all our DB operations and also provide METHOD BODY

/*  For LiveData Room automatically handles it in background Thread.
    But for below other ones, we need to do it, as ROOM doesnt allow DB operations on Main Thread!
    As doing it on Main thread could Freeze our app

    Here we achieve this using AsyncTask!! -- See alternative!!

    Below methods are used coz of ABSTRACTION layer, we try to provide in MVVM.
    The ViewModel will just call insert, update etc, without knowing anything about from where data is fetched
 */
    public void insert(Note note) {
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;//Returns all this data on Background THREAD!!!
    }


//    Class for background thread task - AsyncTask
//    should be STATIC, so no Reference to REPOSITORY itself, otherwise it could cause Memory Leaks
    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {

//        We need NoteDao for carrying out Operations!
        private NoteDao noteDao;

//        Passing it in constructor as this class is static!(?)

        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        //        We need NoteDao for carrying out Operations!
        private NoteDao noteDao;

//        Passing it in constructor as this class is static!(?)

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        //        We need NoteDao for carrying out Operations!
        private NoteDao noteDao;

//        Passing it in constructor as this class is static!(?)

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {

        //        We need NoteDao for carrying out Operations!
        private NoteDao noteDao;

//        Passing it in constructor as this class is static!(?)

        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}
