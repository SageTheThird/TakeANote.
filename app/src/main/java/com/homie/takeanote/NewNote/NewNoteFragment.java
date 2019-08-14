package com.homie.takeanote.NewNote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homie.takeanote.DatabaseTransactions;
import com.homie.roompersistence.R;
import com.homie.takeanote.Room.Note;
import com.homie.takeanote.Utils.DateCreated;
import com.homie.takeanote.Utils.Permissions;
import com.homie.takeanote.Utils.UniversalImageLoader;
import com.homie.takeanote.adapters.NewNotePagerAdapter;
import com.homie.takeanote.adapters.NotesAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rd.PageIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class NewNoteFragment extends Fragment implements OnProgressBarListener {

    private static final String TAG = "NewNoteFragment";
    public static final int PICK_IMAGE_GALLERY = 1;
    public static final int PICK_IMAGE_CAMERA = 3;
    public static final int IMAGEVIEW_HEIGHT=900;

    private EditText editText;
    private NotesAdapter notesAdapter;
    private DatabaseTransactions mDatabaseTransactions;
    private Note note;
    private ImageView mNewImageBtn,mClearBtn, mSendNote,mBackBtn;
    private AlertDialog alertDialog;
    private Uri imageUri;
    private StorageReference mImageRef;
    private NewNotePagerAdapter adapter;
    private ViewPager viewPager;
    private List<String> walls_list;
    private Timer timer;
    private NumberProgressBar numbers_progressBar;
    private PageIndicatorView pageIndicatorView;
    private AlertDialog onDestroyAlert;
    private TextView date_tv,time_tv;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private CompositeDisposable mDisposibles=new CompositeDisposable();
    private boolean noteCleared=false;
    private List<String> tempList=new ArrayList<>();







    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_new_note,container,false);

        editText=view.findViewById(R.id.new_note_Edittext);
        mNewImageBtn =view.findViewById(R.id.new_note_btn);
        mClearBtn=view.findViewById(R.id.clear_note_btn);
        mSendNote =view.findViewById(R.id.change_color_note_btn);
        numbers_progressBar=view.findViewById(R.id.numbers_progressBar);
        viewPager=view.findViewById(R.id.newNote_viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);
        mBackBtn=view.findViewById(R.id.newNote_backBtn);
        viewPager.requestLayout();
        numbers_progressBar.setOnProgressBarListener(this);
        date_tv=view.findViewById(R.id.date_tv);
        time_tv=view.findViewById(R.id.time_tv);






        return view;


    }

    private void startProgressBar(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numbers_progressBar.incrementProgressBy(1);
                    }
                });
            }
        }, 2000, 100);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeStatusBarColor(R.color.colorPrimaryDark);


        Bundle bundle = null;
        mImageRef = FirebaseStorage.getInstance().getReference("takeANote");
        initImageLoader();
        try {
            bundle=getArguments();
            note=bundle.getParcelable("note");


            editText.setText(note.getNote_body());

            date_tv.setText(note.getDate_created());
            time_tv.setText(note.getTime_created());

            walls_list=note.getImage();

            Log.d(TAG, "onActivityCreated: Note id :  "+note.getNote_id());

            if(walls_list.size() > 0){

                if(internetConnectivity() == null || !internetConnectivity().isConnected()){
                    Toast.makeText(getActivity(), "Check Your Internet And Retry To Load Images", Toast.LENGTH_LONG).show();
                }else {

                    viewPager.getLayoutParams().height=IMAGEVIEW_HEIGHT;
                    adapter = new NewNotePagerAdapter(walls_list,
                            getActivity());
                    viewPager.setAdapter(adapter);

                }

            }






        }catch (NullPointerException e){
            e.printStackTrace();
        }

        notesAdapter=new NotesAdapter();
        mDatabaseTransactions=new DatabaseTransactions(getActivity());


        mNewImageBtn.setOnClickListener(NewImageClickListener);
        mClearBtn.setOnClickListener(ClearNoteClickListener);
        mSendNote.setOnClickListener(mSendNoteClickListener);
        mBackBtn.setOnClickListener(mBackClickListener);



    }
    private NetworkInfo internetConnectivity(){
        //Internet Connectivity
        connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo;
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
    }
    private void opencamera()
    {
        if(checkPermission(Permissions.CAMERA_PERMISSIONS[0])){
            Log.d(TAG, "onClick: Starting camera");
            Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent,PICK_IMAGE_CAMERA);

        }
    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap,int quality){
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }

    private void deleteNote(Note note){

        /*
        * Deletes note from the database
        * */
        Completable deleteCompletable=mDatabaseTransactions.deleteNote
                (note);

        deleteCompletable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        mDisposibles.add(d);
                    }

                    @Override
                    public void onComplete() {

                        //notesAdapter.deleteNoteFromList(position);
                        Log.d(TAG, "onComplete: note permanently Deleted");

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    public void deleteLastCharacter() {

        /*
        * Deletes the last character from edittext to save notes with just images
        * */
        int length = editText.getText().length();
        if (length > 0) {
            editText.getText().delete(length - 1, length);
        }
    }

    private void uploadImageToStorage() {

        /*
        * Converting Uri to Bitmap to BytesArray
        * and uploading that bytesArray to firebase storage
        * */
        if(imageUri != null){

            Bitmap bitmap=null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
            }catch (IOException e){
                Log.d(TAG, "uploadResizedToStorage: IOException : "+e.getMessage());
            }
            byte[] bytes=getBytesFromBitmap(bitmap,100);

            final StorageReference uploadReference=mImageRef.child(String.valueOf(note.getNote_id())).child(System.currentTimeMillis()
                    +"."+ ".jpg");

            UploadTask uploadTask = uploadReference.putBytes(bytes);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        uploadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String image_url=uri.toString();

                                Log.d(TAG, "onSuccess: small_url : "+image_url  );
                                //save this url in databse



                                if(walls_list != null){

                                    /*
                                    * If it is not the first image then add image to the list
                                    * */

                                    int walls_list_size;
                                    try {

                                        adapter.addNewImage(image_url);
                                        walls_list_size=walls_list.size();
                                        expandViewPager(walls_list_size);
                                        numbers_progressBar.setVisibility(View.INVISIBLE);
                                        deleteLastCharacter();
                                        timer.cancel();
                                        Log.d(TAG, "onSuccess: "+walls_list_size);

                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }


                                }else {

                                    /*
                                    * If it is the first image then setup viewpager
                                    *
                                    * */

                                    int walls_list_size = 0;
                                    try {

                                        walls_list=new ArrayList<>();
                                        walls_list.add(image_url);
                                        walls_list_size=walls_list.size();
                                        deleteLastCharacter();
                                        adapter = new NewNotePagerAdapter(walls_list,
                                                getActivity());
                                        viewPager.setAdapter(adapter);
                                        expandViewPager(walls_list_size);
                                        numbers_progressBar.setVisibility(View.INVISIBLE);

                                        timer.cancel();


                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }



                                }



                            }
                        });
                    }

                }
            });
        }else{

            Toast.makeText(getActivity(), "Error Selecting Image", Toast.LENGTH_LONG).show();

        }
    }

    private void expandViewPager(int list_size){

        pageIndicatorView.setCount(list_size); // specify total count of indicators
        viewPager.getLayoutParams().height=IMAGEVIEW_HEIGHT;
        pageIndicatorView.setVisibility(View.VISIBLE);
        pageIndicatorView.setSelection(list_size-1);
        mBackBtn.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(list_size-1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*
        * Image picked from gallery is processed here
        * and sent to firebase storage
        * */
        if (requestCode == PICK_IMAGE_GALLERY) {
            Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_LONG).show();


            imageUri = data.getData();
            numbers_progressBar.setVisibility(View.VISIBLE);
            timer = new Timer();
            startProgressBar();
            uploadImageToStorage();


        }

        //CAMERA
        if(requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK){


            Bitmap bitmap = null;


            bitmap=(Bitmap) data.getExtras().get("data");
            //image_.setImageBitmap(bitmap);

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        /*
        * When Permission is granted
        * */
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 69) {
                opencamera();
            }
        }
    }
    @Override
    public void onDestroyView() {
        if(note.getNote_body().equals(editText.getText().toString()) || TextUtils.isEmpty(editText.getText().toString())){

            /*
            * if note has not been changed or edit text is empty
            * */

            if(noteCleared && note.getNote_id() != 1){

                /*
                * if note is cleared and note is not the first note (Dummy Note) then delete the note and
                * images from database onDestroyView
                * */

                deleteNote(note);

                int list_size = 0;
                try{

                    list_size=tempList.size();

                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                if(list_size > 0){

                    for(int i=0;i<list_size;i++){

                        String tempImageUrl=tempList.get(i);
                        Log.d(TAG, "onDestroyView: url : "+tempImageUrl);
                        StorageReference imageRef=FirebaseStorage.getInstance().getReferenceFromUrl(tempImageUrl);
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(getActivity(), "Wallpaper Removed", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onSuccess: image deleted from database");
                            }
                        });
                    }
                }
                super.onDestroyView();


            }else if (noteCleared && note.getNote_id() == 1){

                /*
                * If not is cleared but note is first dummy note then do nothing
                * */
                super.onDestroyView();

            }


            super.onDestroyView();

        }else {

            /*
            * If note is the dummy note and body is changed and edit text is not empty
            * then save the note as new note onDestroy
            * */
            if(note.getNote_id() == 1){
                //New Note


                /*
                * if the note has images or not
                * */
                final Note noteTemp;
                if(walls_list != null){
                    noteTemp=new Note(editText.getText().toString(),0, DateCreated.getDate_Created(),walls_list,DateCreated.getCurrentTimeUsingDate());
                }else {
                    noteTemp=new Note(editText.getText().toString(),0, DateCreated.getDate_Created(),null,DateCreated.getCurrentTimeUsingDate());
                }


                Completable newNoteCompletable=mDatabaseTransactions.addNewNote(noteTemp);

                newNoteCompletable.
                        subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                                Log.d(TAG, "onComplete: NewNoteFragment new note added");
                                notesAdapter.addNote(note);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                super.onDestroyView();

            }else {

                /*
                * if it is existing note then update the note
                * */

                //Update Current Note

                long note_id=note.getNote_id();

                Note noteTemp;
                if(walls_list != null){
                    noteTemp=new Note(editText.getText().toString(),note_id,DateCreated.getDate_Created(),walls_list,DateCreated.getCurrentTimeUsingDate());
                }else {
                    noteTemp=new Note(editText.getText().toString(),note_id,DateCreated.getDate_Created(),null,DateCreated.getCurrentTimeUsingDate());
                }


                Completable updateNoteCompletable=mDatabaseTransactions.updateNote(noteTemp);

                updateNoteCompletable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {


                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                super.onDestroyView();

            }


        }


    }

    View.OnClickListener mBackClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
            * back button will have all the code of onDestroyView but with finish()
            * */

            if(note.getNote_body().equals(editText.getText().toString()) || TextUtils.isEmpty(editText.getText().toString())){

                if(noteCleared && note.getNote_id() != 1){

                    deleteNote(note);

                    Objects.requireNonNull(getActivity()).finish();


                }else if (noteCleared && note.getNote_id() == 1){

                    Objects.requireNonNull(getActivity()).finish();

                }

                Objects.requireNonNull(getActivity()).finish();

            }else {

                if(note.getNote_id() == 1){
                    //New Note


                    final Note noteTemp;
                    if(walls_list != null){
                        noteTemp=new Note(editText.getText().toString(),0, DateCreated.getDate_Created(),walls_list,DateCreated.getCurrentTimeUsingDate());
                    }else {
                        noteTemp=new Note(editText.getText().toString(),0, DateCreated.getDate_Created(),null,DateCreated.getCurrentTimeUsingDate());
                    }


                    Completable newNoteCompletable=mDatabaseTransactions.addNewNote(noteTemp);

                    newNoteCompletable.
                            subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {

                                    Log.d(TAG, "onComplete: NewNoteFragment new note added");
                                    notesAdapter.addNote(note);

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                    Objects.requireNonNull(getActivity()).finish();
                }else {
                    //Update Current Note

                    long note_id=note.getNote_id();

                    Note noteTemp;
                    if(walls_list != null){
                        noteTemp=new Note(editText.getText().toString(),note_id,DateCreated.getDate_Created(),walls_list,DateCreated.getCurrentTimeUsingDate());
                    }else {
                        noteTemp=new Note(editText.getText().toString(),note_id,DateCreated.getDate_Created(),null,DateCreated.getCurrentTimeUsingDate());
                    }


                    Completable updateNoteCompletable=mDatabaseTransactions.updateNote(noteTemp);

                    updateNoteCompletable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {


                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });

                    Objects.requireNonNull(getActivity()).finish();
                }


            }



        }
    };
    View.OnClickListener NewImageClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
            * Checks if internet is connected then opens the gallery to pick an image
            * */
            if(internetConnectivity() == null || !internetConnectivity().isConnected()){
                Toast.makeText(getActivity(), "Check Your Internet To Upload Images", Toast.LENGTH_LONG).show();
            }else {
                openGallery();
            }

        }
    };

    View.OnClickListener ClearNoteClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            /*
            * on Clear Note an alert dialog will pop up
            * */

            DialogInterface.OnClickListener DeleteDialogConfirmListener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /*
                    * on Confirm will clear the complete note with or without images
                    * */
                    Toast.makeText(getActivity(), "Clearing Everyting", Toast.LENGTH_LONG).show();
                    editText.setText("");
                    if(walls_list != null){

                        tempList.addAll(walls_list);
                        walls_list.clear();
                        walls_list=new ArrayList<>();

                    }


                    viewPager.getLayoutParams().height=0;
                    if(adapter !=null){
                        adapter.notifyDataSetChanged();
                    }

                    pageIndicatorView.setVisibility(View.INVISIBLE);
                    mBackBtn.setVisibility(View.GONE);

                    noteCleared=true;


                }
            };
            DialogInterface.OnClickListener DeleteDialogCancelListener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    alertDialog.dismiss();

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
                .setMessage("Clear Note Without Saving")
                .setCancelable(false);

        return alertDialogBuilder;
    }


    View.OnClickListener mSendNoteClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
            * Sending note_body to other apps through intent
            * Only apply to saved notes
            * */
            if(note.getNote_id() == 1){
                Toast.makeText(getActivity(), "Create a note first.", Toast.LENGTH_LONG).show();
            }else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, note.getNote_body());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }

        }
    };


    //Checks a single permission passed from checkpermissionArray
    public boolean checkPermission(String permission) {

        /*
        * Checks permission and returns true if it is granted
        * */
        Log.d(TAG, "checkPermission: checking permission"+permission);
        int permissionRequest= ActivityCompat.checkSelfPermission(getActivity(),permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermission:  permission was not granted for "+permission);
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 69);
            return false;
        }
        else{
            Log.d(TAG, "checkPermission: Permission was granted for " +permission);
            //ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 69);

            return true;
        }
    }

    @Override
    public void onProgressChange(int current, int max) {
        /*
        * when numbers-progressbar reaches 100% this toast will pop up
        * */
        if(current == max) {
            Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
            numbers_progressBar.setProgress(0);
        }
    }
}
