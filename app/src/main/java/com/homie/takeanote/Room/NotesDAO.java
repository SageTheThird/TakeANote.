package com.homie.takeanote.Room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface NotesDAO {
    //Data Access Object
    //annotations do most of the job for inserting, just needs contact

    @Insert
    Completable addNewNote(Note note);

    @Update
    Completable updateNote(Note note);

    @Delete
    Completable deleteNote(Note note);

    @Query("select * from Notes")
    Observable<List<Note>> getAllNotes();


    @Query("select * from Notes where note_id ==:note_id")
    Observable<Note> getNote(long note_id);

}
