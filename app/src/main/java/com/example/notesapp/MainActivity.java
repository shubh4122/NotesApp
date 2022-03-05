package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //This below req code has to be >=0 for SUCCESS
    //Using constant for it, coz later its easy to understand which REQUEST was handled by it!! -- GOOD PRACTICE
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2; // Imp these val must be different, Only then one can distinguish between requests
    private NoteViewModel noteViewModel;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.add_note_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(i, ADD_NOTE_REQUEST);
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);//Set to true if you know RecyclerView size will not CHANGE-- Makes it more efficient

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

//      GETTING INSTANCE OF OUR ViewModel IN ACTIVITY
//      This is the new way! ViewModelProviders.of has been DEPRECATED and REMOVED
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
//      As getAllNotes returns a LiveData obj, we can use observe(owner, observer)
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

        /**
         * This is used to help in 2 things
         * 1. drag and drop stuff  --  No use here
         * 2. swiping
         *
         * param 1 = 0 is for drag and drop
         * param 2 = LEFT RIGHT is for telling if swiped left or right
         *
         * in onSwiped, we get the viewHolder that has been swiped. We will get its position and then Delete it.
         *
         * Problem: Delete method takes in the Note itself and not the position
         * Solution: We make a method in Adapter for this
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //  Gives the items Position
                int positionOfSwipedNote = viewHolder.getAdapterPosition();
                Note noteToBeDeleted = adapter.getNoteAt(positionOfSwipedNote);
                noteViewModel.delete(noteToBeDeleted);

                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);  // We need to attach this ItemTouchHelper obj to our recyclerview to work

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());

                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
//            So, if requestCode is this, means if we Requested to add note or some other request! We used this Constt as its a good practice coz it tells which req it is
//            As its a req to add notes, Hence retrieve the Extras from the intent
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE); // Note this, EXTRA_TITLE can be used here, coz its static and static things can be directly called thru class. Obj not needed
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
//            As int val are NON NULLABLE, we need to pass a default val.
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1); //Hence 1 is the Default priority now

//            With this data we create a new note
            Note note = new Note(title, description, priority);
            noteViewModel.insert(note); // Note, whatever Ops we would do, will only be through ViewModel ONLY!!

            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            
            if (id == -1) {
                Toast.makeText(this, "Note can't be Updated!", Toast.LENGTH_SHORT).show();
                return;
            }


            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title, description, priority);
            note.setId(id); // VVVVVVV IMP step for Updation. See video 17:10
            noteViewModel.update(note);

            Toast.makeText(this, "Note Updated!", Toast.LENGTH_SHORT).show();
        }
//        Else is for , if we leave screen using CROSS, hence not saving
        else {
//            that is resultCode != RESULT_OK (or == RESULT_CANCELLED)
            Toast.makeText(this, "Note wasn't saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}