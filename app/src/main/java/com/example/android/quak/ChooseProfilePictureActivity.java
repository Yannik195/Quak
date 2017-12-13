package com.example.android.quak;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL;

public class ChooseProfilePictureActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePicturesReference;

    private static final int PICK_IMAGE = 1;

    private Uri selectedImageUri = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_profile_picture);

        profilePictureImageView = findViewById(R.id.profile_picture_image_view);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicturesReference = mFirebaseStorage.getReference().child("profile_pictures");

        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            selectedImageUri = data.getData();
/*
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .override(1200,1200)
                    .transform(new RoundedCorners(600));

            Glide.with(getApplicationContext())
                    .load(selectedImageUri)
                    .apply(options)
                    .into(profilePictureImageView);
*/


            Glide.with(getApplicationContext()).load(selectedImageUri).asBitmap().centerCrop().override(1200,1200).into(new BitmapImageViewTarget(profilePictureImageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profilePictureImageView.setImageDrawable(circularBitmapDrawable);

                    //setProfilePicture(selectedImageUri);

                }
            });


            StorageReference profilePictureReference = mProfilePicturesReference.child(selectedImageUri.getLastPathSegment());
            profilePictureReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Uri downloadUri = task.getResult().getDownloadUrl();
                    setProfilePicture(downloadUri);

                }
            });

        }
    }

    private void setProfilePicture(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ChooseProfilePictureActivity.this, "Profilepicture updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
