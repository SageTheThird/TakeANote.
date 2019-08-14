package com.homie.takeanote.DataSource;

import com.homie.takeanote.Room.Note;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;


public interface DataSource {


    Observable<List<Note>> getAllNotes();

    Completable addNewNote(Note note);

    Completable updateNote(Note note);

    Completable deleteNote(Note note);

    Observable<Note> getNote(long id);


}
