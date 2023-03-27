package com.example.laba1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.laba1.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextNotes;

    private FloatingActionButton fabSave;
    private ImageView imageViewDelete;
    private Notes notes;

    private boolean isOldNote = false;
    private final int DELETE_RESULT_CODE = -2;
    private final String LOAD_NOTES_NAME = "old_note", DATE_FORMAT="MMM d, HH:mm", NOTE_NAME="note";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTextTitle = findViewById(R.id.editText_title);
        editTextNotes = findViewById(R.id.editText_notes);
        fabSave = findViewById(R.id.fab_save);
        imageViewDelete = findViewById(R.id.imageView_delete);

        notes = new Notes();

        try {
            notes = (Notes) getIntent().getSerializableExtra(LOAD_NOTES_NAME);
            editTextTitle.setText(notes.getTitle());
            editTextNotes.setText(notes.getNotes());
            isOldNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String text = editTextNotes.getText().toString();

                if(!checkNoteInputs(title, text))
                    return;

                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                Date date = new Date();

                if (!isOldNote) {
                    notes = new Notes();
                }

                notes.setTitle(title);
                notes.setNotes(text);
                notes.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra(NOTE_NAME, notes);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOldNote) {
                    Intent intent = new Intent();
                    intent.putExtra(NOTE_NAME, notes);
                    setResult(DELETE_RESULT_CODE, intent);
                    finish();
                }
            }
        });
    }


    private boolean checkNoteInputs(String title, String text)
    {
        if (title.isEmpty()) {
            Toast.makeText(NoteActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (text.isEmpty()) {
            Toast.makeText(NoteActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}