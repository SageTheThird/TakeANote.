package com.homie.takeanote.DataSource;

import android.content.Context;

import androidx.room.RoomDatabase;

import com.homie.takeanote.Room.Note;
import com.homie.takeanote.Room.NotesDAO;
import com.homie.takeanote.Room.NotesDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class LocalDataSource implements DataSource {

    private NotesDAO mNotesDao;


    public LocalDataSource(Context context){
        NotesDatabase notesDatabase=NotesDatabase.getInstance(context);
        this.mNotesDao=notesDatabase.getContactDAO();
    }

    public LocalDataSource(Context context, RoomDatabase.Callback callback) {


        NotesDatabase notesDatabase = NotesDatabase.getInstance(context, callback);
        this.mNotesDao = notesDatabase.getContactDAO();
    }

    @Override
    public Observable<List<Note>> getAllNotes() {
        return mNotesDao.getAllNotes();
    }

    @Override
    public Completable addNewNote(Note note) {
        return mNotesDao.addNewNote(note);
    }

    @Override
    public Completable updateNote(Note note) {
        return mNotesDao.updateNote(note);
    }

    @Override
    public Completable deleteNote(Note note) {
        return mNotesDao.deleteNote(note);
    }

    @Override
    public Observable<Note> getNote(long id) {
        return mNotesDao.getNote(id);
    }
}
