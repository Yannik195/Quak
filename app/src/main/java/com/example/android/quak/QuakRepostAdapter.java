package com.example.android.quak;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by YannikSSD on 29.12.2017.
 */

public class QuakRepostAdapter extends ArrayAdapter<QuakRepost> {

    private int likes;
    private ValueEventListener mListener;
    private String mUid = MainActivity.mUid;

    private static final String LOG_TAG = "QuakRepostAdapter";

    public QuakRepostAdapter(@NonNull Context context, int resource, @NonNull List<QuakRepost> object) {
        super(context, resource, object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.repost_list_item, parent, false);
        }

        final QuakRepost currentQuakRepost = getItem(super.getCount() - position -1);
        //QuakRepost currentQuakRepost = getItem(position);


        //reposter
        final ImageView reposterImageView1 = convertView.findViewById(R.id.reposter_image_view_1);
        TextView reposterUserNameTextView1 = convertView.findViewById(R.id.reposter_user_name_text_view_1);

        final String posterUserName = currentQuakRepost.getMessage2().getUserName();
        reposterUserNameTextView1.setText(posterUserName);


        Glide.with(getContext()).load(currentQuakRepost.getMessage2().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(reposterImageView1) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                reposterImageView1.setImageDrawable(circularBitmapDrawable);
            }
        });

        //description
        TextView descriptionTextView = convertView. findViewById(R.id.description_text_view);

        //Message 1
        final ImageView posterImageView1 = convertView.findViewById(R.id.poster_image_view_1);
        TextView posterUserNameTextView1 = convertView.findViewById(R.id.poster_user_name_text_view_1);
        ImageView message1ImageView = convertView.findViewById(R.id.message_1_image_view);
        TextView message1TextView = convertView.findViewById(R.id.message_1_text_view);


        posterUserNameTextView1.setText(currentQuakRepost.getMessage1().getUserName());


        //Photo
        Glide.with(getContext())
                .load(currentQuakRepost.getMessage1().getPhoto())
                .dontAnimate()
                .override(500,500)
                .into(message1ImageView);

        //Message
        message1TextView.setText(currentQuakRepost.getMessage1().getMessage());

        //User Profile Picture
        Glide.with(getContext()).load(currentQuakRepost.getMessage1().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(posterImageView1) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                posterImageView1.setImageDrawable(circularBitmapDrawable);
            }
        });


        //Message 2

        //Username
        TextView reposterUserNameTextView2 = convertView.findViewById(R.id.reposter_user_name_text_view_2);
        reposterUserNameTextView2.setText(currentQuakRepost.getMessage2().getUserName());

        //Message
        TextView message2TextView = convertView.findViewById(R.id.message_2_text_view);
        message2TextView.setText(currentQuakRepost.getMessage2().getMessage());


        //User Profile Picture
        final ImageView reposterImageView2 = convertView.findViewById(R.id.reposter_image_view_2);
        Glide.with(getContext()).load(currentQuakRepost.getMessage2().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(reposterImageView2) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                reposterImageView2.setImageDrawable(circularBitmapDrawable);
            }
        });

        //Message 3

        //UserName
        TextView posterUserNameTextView2 = convertView.findViewById(R.id.poster_user_name_text_view_2);
        posterUserNameTextView2.setText(currentQuakRepost.getMessage3().getUserName());
        ImageView message3ImageView = convertView.findViewById(R.id.message_3_image_view);

        //Profile Picture
        final ImageView posterImageView2 = convertView.findViewById(R.id.poster_image_view_2);

        Glide.with(getContext()).load(currentQuakRepost.getMessage1().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(posterImageView2) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                posterImageView2.setImageDrawable(circularBitmapDrawable);
            }
        });

        //Message
        TextView message3TextView = convertView.findViewById(R.id.message_3_text_view);
        message3TextView.setText(currentQuakRepost.getMessage3().getMessage());

        //Answer Image
        //Photo
        Glide.with(getContext())
                .load(currentQuakRepost.getMessage3().getPhoto())
                .dontAnimate()
                .override(500,500)
                .into(message3ImageView);

        final TextView likeTextView = convertView.findViewById(R.id.like_text_view);
        final ImageView likeImageView = convertView.findViewById(R.id.like_image_view);

        FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference mQuakRepostReference;
        final DatabaseReference mUserReference;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakRepostReference = mFirebaseDatabase.getReference().child("quakRepost");
        mUserReference = mFirebaseDatabase.getReference().child("users").child(mUid).child("points");

        likeTextView.setText(String.valueOf(currentQuakRepost.getLikes()));


        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        likes = dataSnapshot.getValue(Integer.class);
                        mQuakRepostReference.removeEventListener(this);
                        mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").setValue(likes + 1);
                        likeTextView.setText(String.valueOf(likes + 1));
                        currentQuakRepost.setmLikes(likes + 1);

                        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int points = dataSnapshot.getValue(Integer.class);
                                mUserReference.setValue(points + 1);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }
        });



        /*
        //Like
        final TextView likeTextView = convertView.findViewById(R.id.like_text_view);

        FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference mQuakRepostReference;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakRepostReference = mFirebaseDatabase.getReference().child("quakRepost");

        mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String likes = String.valueOf(dataSnapshot.getValue(Integer.class));
                likeTextView.setText(likes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Heart onClickListener to increase likes
        ImageView likeImageView = convertView.findViewById(R.id.like_image_view);
        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentlikes = currentQuakRepost.getLikes() + 1;
                Log.i(LOG_TAG, "Likes: " + currentQuakRepost.getLikes());

                mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").setValue(currentlikes);

            }
        });
        */

        return convertView;
    }
}
