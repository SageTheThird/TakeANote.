package com.homie.takeanote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.homie.takeanote.Home.HomeFragment;
import com.homie.takeanote.NewNote.NewNoteFragment;
import com.homie.roompersistence.R;
import com.homie.takeanote.Room.Note;
import com.homie.takeanote.Utils.DateCreated;
import com.homie.takeanote.adapters.NotesAdapter;

public class MainActivity extends AppCompatActivity implements NotesAdapter.NoteItemClick {
    private static final String TAG = "MainActivity";

    private FrameLayout container;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container=findViewById(R.id.main_frame_layout);
        Intent intent=getIntent();


        if(intent.hasExtra("fromFloatingButton")){

        }

        if(intent.hasExtra("fromHome")){

            /*
            * User is Coming From HomeFragment (Not Floating Button)
            * */

            Note note=intent.getParcelableExtra("note");
            NewNoteFragment fragment=new NewNoteFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("note",note);
            fragment.setArguments(bundle);
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.main_frame_layout,fragment).commit();

        }else if(intent.hasExtra("fromFloatingButton")) {

            /*
            * User is Coming From Home Fragment (Floating Button)
            * */

            Note note=new Note("Add a note",1, DateCreated.getDate_Created(),null,DateCreated.getCurrentTimeUsingDate());
            NewNoteFragment fragment=new NewNoteFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable("note",note);
            fragment.setArguments(bundle);
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.main_frame_layout,fragment).commit();

        }else{
            /*
            *Default Home Fragment
            * */
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.main_frame_layout,new HomeFragment()).commit();
        }





    }

    @Override
    public void onClick() {


        getSupportFragmentManager().
                beginTransaction().replace(R.id.main_frame_layout,new NewNoteFragment()).commit();


    }




//
//    public void addAndEditContacts(final boolean isUpdate, final Note note, final int position) {
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
//        View view = layoutInflaterAndroid.inflate(R.layout.fragment_new_note, null);
//
//        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilderUserInput.setView(view);
//
//        TextView contactTitle = view.findViewById(R.id.new_contact_title);
//        final EditText newContact = view.findViewById(R.id.name);
//        final EditText contactEmail = view.findViewById(R.id.email);
//
//        contactTitle.setText(!isUpdate ? "Add New Note" : "Edit Note");
//
//        if (isUpdate && note != null) {
//            newContact.setText(note.getName());
//            contactEmail.setText(note.getEmail());
//        }
//
//        alertDialogBuilderUserInput
//                .setCancelable(false)
//                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogBox, int id) {
//
//                    }
//                })
//                .setNegativeButton("Delete",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//
//                                if (isUpdate) {
//
//                                    deleteNote(note, position);
//                                } else {
//
//                                    dialogBox.cancel();
//
//                                }
//
//                            }
//                        });
//
//
//        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
//        alertDialog.show();
//
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (TextUtils.isEmpty(newContact.getText().toString())) {
//                    Toast.makeText(MainActivity.this, "Enter note name!", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    alertDialog.dismiss();
//                }
//
//
//                if (isUpdate && note != null) {
//
//                    updateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);
//                } else {
//
//                    newNote(newContact.getText().toString(), contactEmail.getText().toString());
//                }
//            }
//        });
//    }

//
//    private class GetAllContactsAsyncTask extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            //noteArrayList.addAll(notesDatabase.getContactDAO().getAllNotes());
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            contactsAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private class CreateContactAsyncTask extends AsyncTask<Note,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Note... contacts) {
//
//            long id = notesDatabase.getContactDAO().addNewNote(contacts[0]);
//
//
//            Note contact = notesDatabase.getContactDAO().getNote(id);
//
//            if (contact != null) {
//
//                noteArrayList.add(0, contact);
//
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            contactsAdapter.notifyDataSetChanged();
//        }
//
//    }
//    private class UpdateContactAsyncTask extends AsyncTask<Note,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Note... contacts) {
//
//            notesDatabase.getContactDAO().updateContact(contacts[0]);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            contactsAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    private class DeleteContactAsyncTask extends AsyncTask<Note,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Note... contacts) {
//
//            notesDatabase.getContactDAO().deleteNote(contacts[0]);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            contactsAdapter.notifyDataSetChanged();
//        }
//
//    }


}
