package com.example.android.quak;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class NewQuak extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuakReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    private EditText mMessageEditText;
    private TextView mImageChooserTextView;
    private ImageView mImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Uri selectedImageUri = null;

    private String mUsername;
    private String mProfilePictureUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quak);
        setTitle("New Quak");

        mFirebaseStorage = FirebaseStorage.getInstance();

        mPhotosStorageReference = mFirebaseStorage.getReference().child("quak_photos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakReference = mFirebaseDatabase.getReference().child("quak");
        mMessageEditText = findViewById(R.id.message_edit_text);
        mImageChooserTextView = findViewById(R.id.choose_an_image_text_view);
        mImageView = findViewById(R.id.imageView);

        mUsername = getIntent().getExtras().get("userName").toString();
        mProfilePictureUrl = getIntent().getExtras().get("profilePictureUrl").toString();


        mImageChooserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                Log.i("LOG", "Uri: " + uri.toString());
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Log.i("LOG", "uri" + selectedImageUri);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("LOG", "btm " + bitmap);

            mImageView.setImageBitmap(bitmap);
        }
    }

    
    private void saveQuak(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewQuak.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.new_quak_loading_alert, null));
        final AlertDialog dialog = builder.create();
        dialog.show();

        StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Uri downloadUri = task.getResult().getDownloadUrl();
                Log.i("LOG", "downloadUri " + downloadUri);
                QuakPost quakPost = new QuakPost(mMessageEditText.getText().toString().trim(), downloadUri.toString(), mUsername, mProfilePictureUrl);
                mQuakReference.push().setValue(quakPost);
                finish();
                dialog.dismiss();
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
