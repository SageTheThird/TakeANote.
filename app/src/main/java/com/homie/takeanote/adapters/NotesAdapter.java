package com.homie.takeanote.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.homie.takeanote.Room.Note;
import com.homie.takeanote.MainActivity;
import com.homie.roompersistence.R;

import java.util.ArrayList;
import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>  {


    private Context context;
    private List<Note> notes_list=new ArrayList<>();
    private NoteItemClick noteItemClick;
    int lastPosition=0;

    public NotesAdapter() {
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView note_body_header;
        public TextView note_id;



        public MyViewHolder(View view) {
            super(view);
            note_body_header = view.findViewById(R.id.note_body_header);
            note_id = view.findViewById(R.id.note_id);

        }
    }

    public interface NoteItemClick{
        public void onClick();
    }


    public NotesAdapter(Context context) {
        this.context = context;



    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final Note note = notes_list.get(position);

        setAnimation(holder.itemView,position);


        holder.note_body_header.setText(note.getNote_body());

        if(note.getNote_id() > 1){
            holder.note_id.setText(String.valueOf(note.getDate_created()));
        }else {
            holder.note_id.setText("");
        }

//        if(position == notes_list.size() ){
//            holder.itemView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.item_animation_falldown));
//        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);

                intent.putExtra("fromHome","toMain");
                intent.putExtra("note",note);

                context.startActivity(intent);

                //mainActivity.addAndEditContacts(true, note, position);
            }
        });

    }




    public void setNotes_list(List<Note> notes_list){
        this.notes_list=notes_list;
        notifyDataSetChanged();
    }

    public void addNote(Note note){
        notes_list.add(note);
        notifyDataSetChanged();
    }


    public int getCurrentPosition(){
        return getCurrentPosition();
    }

    @Override
    public int getItemCount() {

        return notes_list.size();
    }

    public void deleteNoteFromList(int position){
        notes_list.remove(position);
        notifyDataSetChanged();
    }



    public List<Note> getNotes_list(){
        return notes_list;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated

        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_falldown);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}