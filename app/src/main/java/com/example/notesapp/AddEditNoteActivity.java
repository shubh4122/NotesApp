package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.net.IDN;

public class AddEditNoteActivity extends AppCompatActivity {
//  Best Practice for Intent extra keys is to include Package name, to keep them unique!!!
    public static final String EXTRA_ID = "com.example.notesapp.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.notesapp.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.notesapp.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.notesapp.EXTRA_PRIORITY";

    private EditText editTextTitle, editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.titleEdittext);
        editTextDescription = findViewById(R.id.descriptionEdittext);
        numberPickerPriority = findViewById(R.id.priorityNumberPicker);

//        Giving numberpicker min and max val. Cant do it in XML
        numberPickerPriority.setMaxValue(10);
        numberPickerPriority.setMinValue(1);

//        to set X on top left for closing the activity
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int priority = numberPickerPriority.getValue();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please insert a Title/Description", Toast.LENGTH_SHORT).show();
            return;
        }
//      if not empty, insert in DB and leave this activity
//        2 Ways:
/*
          M1: Create a ViewModel Obj as in Main Activity and then use it to do our DB work.
          NOTE: we cant directly use NoteDao or NoteDatabase. According to MVVM Activity has access
          ONLY TO VIEWMODEL!!

          M2:
          Better practice to create 2 separate viewmodel classes(?)

          Below Method, we are sending all this data to other activity and will save there
 */
        Intent data = new Intent(this, MainActivity.class);
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1); // -1 when no id exist. hence invalid id
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    //  To Make save Action bar icon to appear
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
//        Or directly like:-
//        getMenuInflater.inflate(R.menu.add_note_menu, menu);

        return true; //True means we want to display the menu, If FALSE it wont be shown
    }

//    To handle clicks on our menu Items

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}