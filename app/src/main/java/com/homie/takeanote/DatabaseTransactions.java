package com.homie.takeanote;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.homie.takeanote.DataSource.LocalDataSource;
import com.homie.takeanote.Room.Note;
import com.homie.takeanote.ViewModel.NotesViewModel;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;

public class DatabaseTransactions {
    private static final String TAG = "DatabaseTransactions";

    private Context context;
    private LocalDataSource mLocalDataSource;
    private NotesViewModel mNotesViewModel;

    public DatabaseTransactions(Context context) {
        this.context = context;

        mLocalDataSource=new LocalDataSource(context,CreateAndOpenCallBack);
        mNotesViewModel =new NotesViewModel(mLocalDataSource);
    }

    public Completable addNewNote(final Note note){

        Log.d(TAG, "addNewNote: adding new note to database");

        return mNotesViewModel.insertOrUpdateNote(note)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Note>> getAllNotes(){
        return mNotesViewModel
                .getAllNotes()
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteNote(final Note note){

        return mNotesViewModel.deleteNote(note)
                .subscribeOn(Schedulers.io());

    }

    public Completable updateNote(Note note){
        return mNotesViewModel.
                updateNote(note)
                .subscribeOn(Schedulers.io());
    }

    RoomDatabase.Callback CreateAndOpenCallBack=new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.d("CallBack", "onCreate: database callBack onCreate");

            //calls only once when database is created
            Completable newNote=addNewNote(new
                    Note("Add a note",0, "",null,""));

            newNote.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {

                            Log.d(TAG, "onComplete: New Note Added");

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //calls everytime database is opened


        }

    };
}
