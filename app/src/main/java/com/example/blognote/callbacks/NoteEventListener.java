package com.example.blognote.callbacks;

import com.example.blognote.model.Note;

import com.example.blognote.model.Note;

/**
 * Created by ixi.Dv on 22/07/2018.
 */
public interface NoteEventListener {
    /**
     * call wen note clicked.
     *
     * @param note: note item
     */
    void onNoteClick(Note note);

    /**
     * call wen long Click to note.
     *
     * @param note : item
     */
    void onNoteLongClick(Note note);
}