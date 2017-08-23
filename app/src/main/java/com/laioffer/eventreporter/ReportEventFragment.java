package com.laioffer.eventreporter;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportEventFragment extends Fragment {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static int RESULT_LOAD_IMAGE = 1;
    private Button mSelectButton;
    private ImageView mImageView;
    private String mPicturePath = "";

    private EditText mTextViewTitle;


    private final static String TAG = ReportEventFragment.class.getSimpleName();
    private EditText mTextViewLocation;
    private EditText getmTextViewDest;
    private Button mReportButton;
    private DatabaseReference database;
    private String username;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public ReportEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
//auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInAnonymously", task.getException());
                }
            }
        });


        View view = inflater.inflate(R.layout.fragment_report_event, container, false);
        mTextViewLocation = (EditText) view.findViewById(R.id.text_event_location);

        checkPermission();//has not been implemeents ??
        mImageView = (ImageView) view.findViewById(R.id.img_event_pic);
        mSelectButton = (Button) view.findViewById(R.id.button_select);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mTextViewTitle = (EditText) view.findViewById(R.id.text_event_title);


        getmTextViewDest = (EditText) view.findViewById(R.id.text_event_description);
        mReportButton = (Button) view.findViewById(R.id.button_report);
        username = ((EventActivity)getActivity()).getUsername();
        database = FirebaseDatabase.getInstance().getReference();



        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//user choose if they'd like to upload or not
                String key = uploadEvent();
                if (!mPicturePath.equals("")) {
                    Log.i(TAG, "key" + key);
                    uploadImage(mPicturePath, key);
                    mPicturePath = "";
                }

            }
        });
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,//action, cadicoro???
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);// here, start acticity for result,
                // after a simple operation of that acticity,  return something, that acticity is in a short time,
            }
        });

        return view;
    }

    /**
     * upload data and set path
     */
    private String uploadEvent() {
        String title = mTextViewTitle.getText().toString();

        String location = mTextViewLocation.getText().toString();
        String description = getmTextViewDest.getText().toString();
        if (location.equals("") || description.equals("")) {
            return "";
        }
        //create event instance
        Event event = new Event();
        event.setLocation(location);
        event.setDescription(description);
        event.setTime(System.currentTimeMillis());
        event.setTitle(title);

        event.setUser(username);
        String key = database.child("events").push().getKey();
        event.setId(key);
        database.child("events").child(key).setValue(event, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast toast = Toast.makeText(getContext(), "The event is failed, please check you network status.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getContext(), "The event is reported", Toast.LENGTH_SHORT);
                    toast.show();
                    mTextViewLocation.setText("");
                    getmTextViewDest.setText("");
                }
            }
        });
        return key;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.e(TAG, picturePath);
            mPicturePath = picturePath;
            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            mImageView.setVisibility(View.VISIBLE);
        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }
    }

    /**
     * Upload image and get file path
     */
    private void uploadImage(final String imgPath, final String eventId) {
        Uri file = Uri.fromFile(new File(imgPath));//
        StorageReference imgRef = storageRef.child("images/" + file.getLastPathSegment() + " " + System.currentTimeMillis());//
//创建了一个云端的reference

        UploadTask uploadTask = imgRef.putFile(file);//建立task

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {//两个linster
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//加两个listner以后才算upload
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();//从clodud 得到的image的
                Log.i(TAG, "upload successfully");
                database.child("events").child(eventId).child("imgUri").setValue(downloadUrl.toString());//存
            }
        });
    }

}
