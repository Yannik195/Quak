package com.example.android.quak.Adapters;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.ExpandAnimation;
import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakChat;
import com.example.android.quak.QuakMessage;
import com.example.android.quak.QuakPost;
import com.example.android.quak.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by YannikSSD on 03.01.2018.
 */

public class NewQuakPostAdapterAlternative extends ArrayAdapter<QuakPost> {

    private ImageView photoImageView;
    boolean visible = false;
    private Animation decrease;
    private Animation increase;

    private DatabaseReference mQuakChatReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private String chatKey;

    public NewQuakPostAdapterAlternative(@NonNull Context context, int resource, @NonNull List<QuakPost> objects) {
        super(context, resource, objects);
        decrease = AnimationUtils.loadAnimation(getContext(), R.anim.decrease_anim);
        increase = AnimationUtils.loadAnimation(getContext(), R.anim.increase_anim);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakChatReference = mFirebaseDatabase.getReference().child("quakChat");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.quak_list_item_alternative, parent, false);
        }

        final QuakPost currentQuakPost = getItem(super.getCount() - position -1);
        //final QuakPost currentQuakPost = getItem(position);

        //final RelativeLayout layout = convertView.findViewById(R.id.relative_layout);
        final ViewGroup viewGroup = convertView.findViewById(R.id.relative_layout);
        final TextView descriptionTextView = convertView.findViewById(R.id.description_text_view);

        //Photo
        photoImageView = convertView.findViewById(R.id.photo_image_view);
        Glide.with(getContext())
                .load(currentQuakPost.getPhotoUrl())
                //.override(Resources.getSystem().getDisplayMetrics().widthPixels, 700)
                .centerCrop()
                .into(photoImageView);


        //Interact like click listener

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String chatName = currentQuakPost.getUserName() + "_" + MainActivity.mUsername;

                final QuakChat quakChat = new QuakChat(
                        null,
                        chatName,
                        currentQuakPost.getUserName(),
                        currentQuakPost.getUid(),
                        currentQuakPost.getProfilePictureUrl(),
                        MainActivity.mUsername,
                        MainActivity.mUid,
                        MainActivity.mProfilePictureUrl,
                        currentQuakPost.getMessage(),
                        currentQuakPost.getPhotoUrl(),
                        null,
                        null,
                        null,
                        0);

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());

                View view1 = View.inflate(getContext(), R.layout.interact_alert_dialog, null);
                final EditText messageEditText = (EditText) view1.findViewById(R.id.interact_dialog_edit_text);
                alertbox.setMessage(currentQuakPost.getMessage());

                alertbox.setPositiveButton("Quak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        mQuakChatReference.push().setValue(quakChat, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                //Adding the unique ChatKey generated by Firebase to the QuakInteracters Uid
                                chatKey = databaseReference.getKey();
                                databaseReference.child("quakChatKey").setValue(chatKey);
                                databaseReference.child("reposted").setValue(false);
                                databaseReference.child("lastMessage").setValue(messageEditText.getText().toString().trim());
                                databaseReference.child("lastMessageTime").setValue(System.currentTimeMillis());
                                databaseReference.child("messages").push().setValue(new QuakMessage(currentQuakPost.getMessage(), currentQuakPost.getUserName(), currentQuakPost.getUid(), currentQuakPost.getProfilePictureUrl(), currentQuakPost.getPhotoUrl()));
                                databaseReference.child("messages").push().setValue(new QuakMessage(messageEditText.getText().toString().trim(), MainActivity.mUsername, MainActivity.mUid, MainActivity.mProfilePictureUrl, null));
                                DatabaseReference currentUserRef = mUserDatabaseReference.getRef().child(MainActivity.mUid).child("chats");
                                DatabaseReference quakerUserRef = mUserDatabaseReference.getRef().child(currentQuakPost.getUid()).child("chats");
                                currentUserRef.push().setValue(chatKey, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        mQuakChatReference.child(chatKey).child("interactUserRefKey").setValue(databaseReference.getKey());
                                    }
                                });

                                if (!currentQuakPost.getUid().equals(MainActivity.mUid)) {
                                    quakerUserRef.push().setValue(chatKey, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            mQuakChatReference.child(chatKey).child("quakerUserRefKey").setValue(databaseReference.getKey());
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                ImageView photoView = view1.findViewById(R.id.photo_preview_image_view);
                Glide.with(view.getContext())
                        .load(quakChat.getQuakPhoto())
                        .dontAnimate()
                        .into(photoView);
                alertbox.setView(view1);

                alertbox.show();
            }
        });


        //Profile Picture
        final ImageView profilePictureImageView = convertView.findViewById(R.id.pp_image_view);
        Glide.with(getContext()).load(currentQuakPost.getProfilePictureUrl()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(profilePictureImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profilePictureImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        //Description
        descriptionTextView.setText(currentQuakPost.getMessage());

        //UserName
        TextView userNameTextView = convertView.findViewById(R.id.username_text_view);
        userNameTextView.setText(currentQuakPost.getUserName());

        //Time Stamp

        TextView timeStampTextView = convertView.findViewById(R.id.time_stamp_text_view);
        SimpleDateFormat minFormat = new SimpleDateFormat("mm");
        SimpleDateFormat hourFormat =new SimpleDateFormat("h");
        SimpleDateFormat oneDigitHFormat = new SimpleDateFormat("h");
        SimpleDateFormat doubleDigitHFormat = new SimpleDateFormat("hh");
        SimpleDateFormat othersFormat = new SimpleDateFormat("dd.mm.yy hh:mm");

        long postedTimeInMillis = currentQuakPost.getTimeInMillis();
        long currentTimeInMillis = System.currentTimeMillis();
        Log.i("NewQuakPostAdapter", postedTimeInMillis + " " + currentTimeInMillis);

        long posted = currentTimeInMillis - postedTimeInMillis;

        if (posted <= 3600000){
            timeStampTextView.setText("vor " + minFormat.format(posted) + " min");
        } else if (posted >= 3600000 && posted <7200000){
            timeStampTextView.setText(hourFormat.format(posted) + " Stunde");
        } else if (posted >= 7200000&& posted < 36000000){
            timeStampTextView.setText(oneDigitHFormat.format(posted) + " Stunden");
        } else if (posted >= 36000000){
            timeStampTextView.setText(doubleDigitHFormat.format(posted) + " Stunden");
        } else {
            timeStampTextView.setText(othersFormat.format(posted));
        }

        return convertView;
    }
}
