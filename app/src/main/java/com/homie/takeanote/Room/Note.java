package com.homie.takeanote.Room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.homie.takeanote.Utils.DataConverter;

import java.util.List;

@Entity(tableName = "Notes")
public class Note implements Parcelable {

    @ColumnInfo(name = "note_body")
    private String note_body;


    @NonNull
    @ColumnInfo(name = "note_id")
    @PrimaryKey(autoGenerate = true)
    private long note_id;

    @ColumnInfo
    private String date_created;

    @TypeConverters(DataConverter.class)
    private List<String> image;

    @ColumnInfo
    private String time_created;

    protected Note(Parcel in) {
        note_body = in.readString();
        note_id = in.readLong();
        date_created = in.readString();
        image = in.createStringArrayList();
        time_created = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public String toString() {
        return "Note{" +
                "note_body='" + note_body + '\'' +
                ", note_id=" + note_id +
                ", date_created='" + date_created + '\'' +
                ", image=" + image +
                ", time_created='" + time_created + '\'' +
                '}';
    }

    public Note() {
    }

    public String getNote_body() {
        return note_body;
    }

    public void setNote_body(String note_body) {
        this.note_body = note_body;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public Note(String note_body, long note_id, String date_created, List<String> image, String time_created) {
        this.note_body = note_body;
        this.note_id = note_id;
        this.date_created = date_created;
        this.image = image;
        this.time_created = time_created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(note_body);
        dest.writeLong(note_id);
        dest.writeString(date_created);
        dest.writeStringList(image);
        dest.writeString(time_created);
    }
}