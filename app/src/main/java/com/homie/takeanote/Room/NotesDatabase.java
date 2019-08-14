package com.homie.takeanote.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Note.class},version = 1)
public abstract class NotesDatabase extends RoomDatabase {

    public static final String DATABASE_NAME="NotesDB";

    public abstract NotesDAO getContactDAO();

    private static volatile NotesDatabase INSTANCE;


    public static NotesDatabase getInstance(Context context, RoomDatabase.Callback callback) {
        if (INSTANCE == null) {
            synchronized (NotesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotesDatabase.class, DATABASE_NAME).addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static NotesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotesDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
