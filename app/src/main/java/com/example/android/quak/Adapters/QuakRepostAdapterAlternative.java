package com.example.android.quak.Adapters;

import android.app.Activity;
import android.arch.core.executor.TaskExecutor;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakRepost;
import com.example.android.quak.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by YannikSSD on 17.01.2018.
 */

public class QuakRepostAdapterAlternative extends ArrayAdapter<QuakRepost> {


    private boolean expanded = false;

    private int likes;

    private static final String LOG_TAG = "QuakRepostAdapter";



    public QuakRepostAdapterAlternative(@NonNull Context context, int resource, @NonNull List<QuakRepost> object) {
        super(context, resource, object);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.quak_repost_list_item_alternative, parent, false);
        }

        //QuakRepost currentQuakRepost = getItem(super.getCount() - position - 1);
        final QuakRepost currentQuakRepost = getItem(position);



        //reposter
        final ImageView reposterPPImageView = convertView.findViewById(R.id.reposter_pp_image_view);
        TextView reposterUsername = convertView.findViewById(R.id.reposter_username);

        //Headline
        TextView headlineTextView = convertView.findViewById(R.id.repost_list_item_headline);
        headlineTextView.setText(currentQuakRepost.getHeadline());

        //Time
        TextView timeTextView = convertView.findViewById(R.id.repost_list_item_time);
        timeTextView.setText(String.valueOf(currentQuakRepost.getRepostTime()));


        Glide.with(getContext()).load(currentQuakRepost.getMessage2().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(reposterPPImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                reposterPPImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        reposterUsername.setText(currentQuakRepost.getMessage2().getUserName());

        //Message 1
        //profile picture
        final ImageView message1PPImageView = convertView.findViewById(R.id.message_1_pp_image_view);
        Glide.with(getContext()).load(currentQuakRepost.getMessage1().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(message1PPImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                message1PPImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        //username
        TextView message1Username = convertView.findViewById(R.id.message_1_username);
        message1Username.setText(currentQuakRepost.getMessage1().getUserName());

        //Message
        TextView message1Message = convertView.findViewById(R.id.message_1_message_text_view);
        message1Message.setText(currentQuakRepost.getMessage1().getMessage());

        //Photo

        ImageView message1ImageView = convertView.findViewById(R.id.imageView5);
        Glide.with(getContext())
                .load(currentQuakRepost.getMessage1().getPhoto())
                .dontAnimate()
                .into(message1ImageView);

        message1ImageView.setClipToOutline(true);


        //like
        ImageButton likeImageButton = convertView.findViewById(R.id.like_image_button);
        final TextView likeTextView = convertView.findViewById(R.id.like_text_view);

        likeTextView.setText(String.valueOf(currentQuakRepost.getLikes()));


        FirebaseDatabase mFirebaseDatabase;
        final DatabaseReference mQuakRepostReference;
        final DatabaseReference mUserReference;
        final DatabaseReference mQuakLikesReference;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakRepostReference = mFirebaseDatabase.getReference().child("quakRepost");
        mUserReference = mFirebaseDatabase.getReference().child("users").child(MainActivity.mUid).child("points");
        mQuakLikesReference =  mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes");




        likeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        likes = dataSnapshot.getValue(Integer.class);
                        Log.i(LOG_TAG, "Likes: " + likes);

                        likeTextView.setText(String.valueOf(likes + 1));
                        currentQuakRepost.setmLikes(likes + 1);
                        likes += 1;
                        mQuakRepostReference.child(currentQuakRepost.getKey()).child("likes").setValue(likes);
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

        //Message 2

        final LinearLayout layoutMessage2 = convertView.findViewById(R.id.layout_message_2);

        //PP
        final ImageView message2PPImageView = convertView.findViewById(R.id.message_2_pp_image_view);
        Glide.with(getContext()).load(currentQuakRepost.getMessage2().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(message2PPImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                message2PPImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        //Username
        final TextView message2Username = convertView.findViewById(R.id.message_2_username);
        message2Username.setText(currentQuakRepost.getMessage2().getUserName());

        //Message
        final TextView message2Message = convertView.findViewById(R.id.message_2_message_text_view);
        message2Message.setText(currentQuakRepost.getMessage2().getMessage());




        //Message 3
        final ImageView message3ImageView = convertView.findViewById(R.id.imageView6);
        final ImageView message3ppImageView = convertView.findViewById(R.id.message_3_pp_image_view);
        final TextView message3MessageTextView = convertView.findViewById(R.id.message_3_message_text_view);
        final TextView message3UsernameTextView = convertView.findViewById(R.id.message_3_username);
        message3ImageView.setClipToOutline(true);


        Glide.with(getContext()).load(currentQuakRepost.getMessage3().getProfilePicture()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(message3ppImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                message3ppImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        message3MessageTextView.setText(currentQuakRepost.getMessage3().getMessage());
        message3UsernameTextView.setText(currentQuakRepost.getMessage3().getUserName());





        Glide.with(getContext())
                .load(currentQuakRepost.getMessage3().getPhoto())
                .dontAnimate()
                .into(message3ImageView);


        final ImageButton imageButton = convertView.findViewById(R.id.image_button);
        final ImageButton imageButton2 = convertView.findViewById(R.id.image_button_2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    message2Username.setVisibility(View.VISIBLE);
                    message2PPImageView.setVisibility(View.VISIBLE);
                    message2Message.setVisibility(View.VISIBLE);

                    message3ImageView.setVisibility(View.VISIBLE);
                    message3ppImageView.setVisibility(View.VISIBLE);
                    message3MessageTextView.setVisibility(View.VISIBLE);
                    message3UsernameTextView.setVisibility(View.VISIBLE);

                    layoutMessage2.setVisibility(View.VISIBLE);

                    imageButton.setVisibility(View.GONE);
                    imageButton2.setVisibility(View.VISIBLE);

                    view.setHasTransientState(true);

                    expanded = true;
                }

        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message2Username.setVisibility(View.GONE);
                message2PPImageView.setVisibility(View.GONE);
                message2Message.setVisibility(View.GONE);

                message3ImageView.setVisibility(View.GONE);
                message3ppImageView.setVisibility(View.GONE);
                message3MessageTextView.setVisibility(View.GONE);
                message3UsernameTextView.setVisibility(View.GONE);


                layoutMessage2.setVisibility(View.GONE);

                imageButton.setVisibility(View.VISIBLE);
                imageButton2.setVisibility(View.GONE);

                view.setHasTransientState(false);

                expanded = false;
            }
        });



        return convertView;
    }
}
