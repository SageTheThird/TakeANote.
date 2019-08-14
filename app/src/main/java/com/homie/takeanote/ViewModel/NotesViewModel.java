package com.homie.takeanote.ViewModel;

import androidx.lifecycle.ViewModel;

import com.homie.takeanote.DataSource.LocalDataSource;
import com.homie.takeanote.Room.Note;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class NotesViewModel extends ViewModel {


    private LocalDataSource mLocalDataSource;

    public NotesViewModel(LocalDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public Observable<List<Note>> getAllNotes(){
        return mLocalDataSource.getAllNotes();
    }

    public Completable updateNote(Note note){
        return mLocalDataSource.updateNote(note);
    }

    public Observable<Note> getSingleNote(long id){
        return mLocalDataSource.getNote(id);
    }

    public Completable insertOrUpdateNote(Note note){
        return mLocalDataSource.addNewNote(note);
    }

    public Completable deleteNote(Note note){
        return mLocalDataSource.deleteNote(note);
    }
}
