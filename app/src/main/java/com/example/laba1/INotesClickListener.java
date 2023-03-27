package com.example.laba1;

import androidx.cardview.widget.CardView;

import com.example.laba1.Models.Notes;

public interface INotesClickListener {

    void onClick (Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
