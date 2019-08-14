package com.homie.takeanote.Home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.homie.takeanote.DatabaseTransactions;
import com.homie.takeanote.MainActivity;
import com.homie.roompersistence.R;
import com.homie.takeanote.Room.Note;
import com.homie.takeanote.Settings.SettingsActivity;
import com.homie.takeanote.adapters.NotesAdapter;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {


    private static final String TAG = "HomeFragment";


    private ExtractEditText editText;
    private NotesAdapter notesAdapter;
    private RecyclerView recyclerView;
    private CompositeDisposable mDisposible= new CompositeDisposable();
    private FloatingActionButton floatingActionButton;
    private Observable<List<Note>> notes_list_observable;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseTransactions mDatabaseTransactions;
    private ItemTouchHelper itemTouchHelper;
    private AlertDialog alertDialog;
    private ImageView mSettingsBtn;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        editText=view.findViewById(R.id.new_note_Edittext);
        recyclerView = view.findViewById(R.id.recycler_view_contacts);
        floatingActionButton= (FloatingActionButton) view.findViewById(R.id.fab);
        mSettingsBtn=view.findViewById(R.id.settings_btn);
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeStatusBarColor(R.color.colorPrimaryDark);
        //second parameter same databaseClass
        //third parameter the database name
        //allow mainThread is used for small projects and uses MainThread to perform Queries on Data

        mDatabaseTransactions=new DatabaseTransactions(getActivity());
        itemTouchHelper= new ItemTouchHelper(simpleItemTouchCallback);


        setupAllNotesFromDB();
        setupRecyclerView();


        //listeners
        floatingActionButton.setOnClickListener(FloatingButtonClickListener);
        mSettingsBtn.setOnClickListener(SettingsClickListener);




    }

    private void changeStatusBarColor(int color){
        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),color));

    }

    private void setupRecyclerView(){

        notesAdapter = new NotesAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    View.OnClickListener SettingsClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            startActivity(new Intent(getActivity(), SettingsActivity.class));

        }
    };

    private void setupAllNotesFromDB(){
        notes_list_observable=mDatabaseTransactions.getAllNotes();
        notes_list_observable.observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Note>, ObservableSource<Note>>() {
                    @Override
                    public ObservableSource<Note> apply(List<Note> notes) throws Exception {

                        notesAdapter.setNotes_list(notes);


                        return Observable.fromIterable(notes)
                                .subscribeOn(Schedulers.io());

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {



                    }

                    @Override
                    public void onNext(Note note) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    View.OnClickListener FloatingButtonClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getActivity(), MainActivity.class);
            intent.putExtra("fromFloatingButton","toMain");
            startActivity(intent);

        }
    };

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {




            DialogInterface.OnClickListener DeleteDialogConfirmListener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(viewHolder.getAdapterPosition() != 0){

                        deleteNote(viewHolder.getAdapterPosition());
                        Toast.makeText(getActivity(), "Note Deleted", Toast.LENGTH_LONG).show();

                    }


                }
            };
            DialogInterface.OnClickListener DeleteDialogCancelListener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

            AlertDialog.Builder temmAlertDialogBuilder=getAlertDialogBuilder().setPositiveButton("CONFIRM",DeleteDialogConfirmListener).
                    setNegativeButton("CANCEL",DeleteDialogCancelListener);



            // create alert dialog
            alertDialog = temmAlertDialogBuilder.create();
            alertDialog.show();

        }
    };



    private AlertDialog.Builder getAlertDialogBuilder(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.MyAlertDialogTheme);

        // set title
        alertDialogBuilder.setTitle("Are You Sure?");
        // set dialog message
        alertDialogBuilder
                .setMessage("This Note Will Be Deleted Permanently")
                .setCancelable(false);

        return alertDialogBuilder;
    }


    private void deleteNote(final int position){

        Note note=notesAdapter.getNotes_list().get(position);
        Completable deleteCompletable=mDatabaseTransactions.deleteNote
                (note);

        if(note.getImage() != null){

            for(int i=0;i<note.getImage().size();i++){
                StorageReference imageRef= FirebaseStorage.getInstance().getReferenceFromUrl(note.getImage().get(i));
                imageRef.delete();
            }

        }
        deleteCompletable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        mDisposible.add(d);
                    }

                    @Override
                    public void onComplete() {

                        notesAdapter.deleteNoteFromList(position);
                        Log.d(TAG, "onComplete: note permanently Deleted");

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposible.clear();
    }
}
