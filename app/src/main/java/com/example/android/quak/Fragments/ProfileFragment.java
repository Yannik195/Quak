package com.example.android.quak.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.ChooseProfilePictureActivity;
import com.example.android.quak.MainActivity;
import com.example.android.quak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * Created by YannikSSD on 13.12.2017.
 */

public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserReposts;
    private DatabaseReference mRepostKey;
    private DatabaseReference mRepostReference;
    private DatabaseReference mUserReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePictureReference;

    private String mUsername;
    private String mProfilePictureUrl;

    private ImageView mProfilePictureImageView;
    private TextView mProfileUserNameTextView;
    private TextView mProfilePointsTextView;

    private Uri selectedImageUri;

    private static final int REQUEST_IMAGE_CHOOSER = 1;

    private static final String LOG_TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");

        Log.i(LOG_TAG, "onCreateView");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRepostKey = mFirebaseDatabase.getReference().child("users").child(MainActivity.mUid).child("quakRepost");
        mRepostReference = mFirebaseDatabase.getReference().child("quakRepost");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePictureReference = mFirebaseStorage.getReference().child("profile_pictures");

        mProfilePictureImageView = view.findViewById(R.id.profile_profile_picture_image_view);
        mProfileUserNameTextView = view.findViewById(R.id.profile_user_name_text_view);
        mProfilePointsTextView = view.findViewById(R.id.profile_points_text_view);

        mUsername = MainActivity.mUsername;
        mProfileUserNameTextView.setText(mUsername);

        mProfilePictureUrl = MainActivity.mProfilePictureUrl;

        Glide.with(getContext()).load(mProfilePictureUrl).asBitmap().centerCrop().override(300,300).into(new BitmapImageViewTarget(mProfilePictureImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mProfilePictureImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        mProfilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_IMAGE_CHOOSER);
            }
        });




        //user points
        mUserReference = mFirebaseDatabase.getReference().child("users").child(MainActivity.mUid).child("points");

        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProfilePointsTextView.setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE_CHOOSER && resultCode == RESULT_OK){

            selectedImageUri = data.getData();

            final AlertDialog.Builder alerDialog = new AlertDialog.Builder(getContext());
            View view = View.inflate(getContext(), R.layout.change_profile_picture_alert_dialog, null);
            final ImageView imageView = view.findViewById(R.id.profile_picture_preview);


            Glide.with(getContext()).load(selectedImageUri).asBitmap().centerCrop().override(400,400).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });

            alerDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog loadingAlert = new AlertDialog.Builder(getContext()).create();
                    View loadingView = View.inflate(getContext(), R.layout.loading_alert, null);
                    loadingAlert.setView(loadingView);
                    loadingAlert.setCancelable(false);
                    loadingAlert.show();
                    StorageReference photoRef = mProfilePictureReference.child(MainActivity.mUid.trim() + "_" + System.currentTimeMillis());
                    photoRef.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            setProfilePicture(task.getResult().getDownloadUrl());
                            MainActivity.setProfilePictureUrl(task.getResult().getDownloadUrl().toString());
                            Log.i(LOG_TAG, "Picture Saved");
                            loadingAlert.dismiss();
                        }
                    });

                    Glide.with(getContext()).load(selectedImageUri).asBitmap().centerCrop().override(400,400).into(new BitmapImageViewTarget(mProfilePictureImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            mProfilePictureImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });

                }
            });

            alerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alerDialog.setView(view);
            alerDialog.show();
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
                        Toast.makeText(getContext(), "Profilepicture updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
