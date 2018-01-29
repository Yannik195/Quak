package com.example.android.quak;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class NewQuak extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuakReference;
    private DatabaseReference mUserReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    private EditText mMessageEditText;

    private ImageView mImageView;
    private ImageButton mTakePicture;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_CONTACT_REQUEST = 2;


    private Uri selectedImageUri = null;
    private byte[] bytes;

    private String mUsername;
    private String mProfilePictureUrl;
    private String mUid;
    ProgressDialog mProgressDialog;


    private static final String LOG_TAG = "NewQuakActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quak);
        setTitle("New Quak");


        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("quak_photos");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakReference = mFirebaseDatabase.getReference().child("quak");
        mUserReference = mFirebaseDatabase.getReference().child("users").child(MainActivity.mUid).child("quakPost");

        mMessageEditText = findViewById(R.id.message_edit_text);
        mImageView = findViewById(R.id.imageView);
        mTakePicture = findViewById(R.id.take_picture_image_circle);


        mUsername = MainActivity.mUsername;
        mUid = MainActivity.mUid;
        mProfilePictureUrl = MainActivity.mProfilePictureUrl;

        Log.i(LOG_TAG, "User Info: "+ mUsername + mUid + mProfilePictureUrl);


        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mTakePicture.setVisibility(View.GONE);
            selectedImageUri = data.getData();

            if (selectedImageUri == null){
                selectedImageUri =
                        Uri.parse("https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/quak_photos%2F37716?alt=media&token=da71c70a-f83c-49ca-8e4a-45d63c50f073");
            }
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                bytes = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }

            mImageView.setImageBitmap(bitmap);
        }

    }




    
    private void saveQuak(){

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.loading_alert);
        final long timeInMillis = System.currentTimeMillis();

        StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
        photoRef.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Uri downloadUri = task.getResult().getDownloadUrl();
                QuakPost quakPost = new QuakPost(mMessageEditText.getText().toString().trim(), downloadUri.toString(), mUsername, mProfilePictureUrl, mUid, timeInMillis);
                mQuakReference.push().setValue(quakPost, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        mUserReference.push().setValue(databaseReference.getKey());
                    }
                });
                finish();
                mProgressDialog.dismiss();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_quak_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.post_quak:
                saveQuak();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
