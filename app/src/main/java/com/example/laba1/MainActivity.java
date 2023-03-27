package com.example.laba1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.laba1.Adapter.NotesListAdapter;
import com.example.laba1.DataBase.RoomDB;
import com.example.laba1.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    RecyclerView recyclerView;
    FloatingActionButton floatAdd;
    NotesListAdapter notesListAdapter;
    SearchView searchView;
    RoomDB database;

    List<Notes> notes = new ArrayList<>();
    Notes selectedNote;

    private final int DELETE_RESULT_CODE = -2, SPAN_COUNT=2, ADD_NOTE_CODE=101, MODIFY_NOTE_CODE=102;
    private final String LOAD_NOTES_NAME = "old_note", NOTE_NAME="note";

    private final INotesClickListener notesClickListener = new INotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra(LOAD_NOTES_NAME
                    , notes);

            startActivityForResult(intent, MODIFY_NOTE_CODE);
        }


        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView)
    {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }




    private void findElems()
    {
        recyclerView = findViewById(R.id.recycler_home);
        floatAdd = findViewById(R.id.fab_add);
        searchView = findViewById(R.id.searchView_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findElems();

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecycle(notes);

        //Add sub
        floatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this, NoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_CODE);
            }
        });
        //Search sub
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });
    }

    private void filter (String s) {
        List<Notes> filteredList = new ArrayList<>();

        for (Notes singleNote : notes) {
            //title and text search
            if (singleNote.getTitle().toLowerCase().contains(s.toLowerCase()) ||
                singleNote.getNotes().toLowerCase().contains(s.toLowerCase())) {

                filteredList.add(singleNote);
            }
        }

        notesListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Notes newNotes = (Notes) data.getSerializableExtra(NOTE_NAME);
                database.mainDAO().insert(newNotes);

                database.mainDAO().update(newNotes.getID(), newNotes.getTitle(), newNotes.getNotes());

                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == MODIFY_NOTE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Notes newNotes = (Notes) data.getSerializableExtra(NOTE_NAME);

                database.mainDAO().update(newNotes.getID(), newNotes.getTitle(), newNotes.getNotes());

            }
            if (resultCode == DELETE_RESULT_CODE) {
                Notes newNotes = (Notes) data.getSerializableExtra(NOTE_NAME);

                database.mainDAO().delete(newNotes);

            }

            notes.clear();
            notes.addAll(database.mainDAO().getAll());
            notesListAdapter.notifyDataSetChanged();
        }
    }

    private void updateRecycle(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.pin:
                if(selectedNote.isPinned())
                {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Note unpinned", Toast.LENGTH_SHORT).show();
                }else{
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Note pinned", Toast.LENGTH_SHORT).show();
                }

                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();

                return  true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Note removed", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return false;
        }
    }
}