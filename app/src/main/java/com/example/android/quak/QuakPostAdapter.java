package com.example.android.quak;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.Fragments.ChatActivity;
import com.example.android.quak.Fragments.NewQuakPostFragment;
import com.example.android.quak.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class QuakPostAdapter extends ArrayAdapter<QuakPost> {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuakChatReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCurrentChatReference;

    private String chatKey;

    private static final String LOG_TAG = "QuakPostAdapter";

    public QuakPostAdapter(@NonNull Context context, int resource, List<QuakPost> object ) {
        super(context, resource, object);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakChatReference = mFirebaseDatabase.getReference().child("quakChat");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
    }

    /*
    private RequestOptions profilePictureOptions = new RequestOptions()
            .centerCrop()
            .override(50,50)
            .dontAnimate()
            .transform(new RoundedCorners(50));

    private RequestOptions photoRequestOptions = new RequestOptions()
            .dontAnimate()
            .override(Resources.getSystem().getDisplayMetrics().widthPixels, 700)
            .centerCrop();
            */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.quak_list_item, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.quak_message_text_view);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.quak_image_view);
        TextView userNameTextView = convertView.findViewById(R.id.user_text_view);
        final ImageView profilePictureImageView = convertView.findViewById(R.id.user_image_view);
        LinearLayout interact = convertView.findViewById(R.id.interact_layout);

        //QuakPost currentQuakPost = getItem(position);

        final QuakPost currentQuakPost = getItem(super.getCount() - position -1);

        messageTextView.setText(currentQuakPost.getMessage());
        userNameTextView.setText(currentQuakPost.getUserName());


        Glide.with(getContext()).load(currentQuakPost.getProfilePictureUrl()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(profilePictureImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profilePictureImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        Glide.with(getContext())
                .load(currentQuakPost.getPhotoUrl())
                .dontAnimate()
                .centerCrop()
                .into(imageView);

        interact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatName = currentQuakPost.getUserName() + "_" + mFirebaseAuth.getCurrentUser().getDisplayName();
                Log.i(LOG_TAG, "ChatName: " + chatName);

                String mProfilePictureUrl;

                if (mFirebaseAuth.getCurrentUser().getPhotoUrl() == null){
                    mProfilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F36761?alt=media&token=ed06e95c-f402-4503-b792-8f238c0324e8";
                } else {
                    mProfilePictureUrl = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
                }



                final QuakChat quakChat = new QuakChat(
                        null,
                        chatName,
                        currentQuakPost.getUserName(),
                        currentQuakPost.getUid(),
                        currentQuakPost.getProfilePictureUrl(),
                        mFirebaseAuth.getCurrentUser().getDisplayName(),
                        mFirebaseAuth.getCurrentUser().getUid(),
                        mProfilePictureUrl,
                        currentQuakPost.getMessage(),
                        currentQuakPost.getPhotoUrl(),
                        null,
                        null,
                        null,
                        0);

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                View view1 = View.inflate(getContext(), R.layout.interact_alert_dialog, null);

                final EditText messageEditText = (EditText) view1.findViewById(R.id.interact_dialog_edit_text);
                ImageView photoView = (ImageView) view1.findViewById(R.id.photo_preview_image_view);
                Glide.with(view.getContext())
                        .load(quakChat.getQuakPhoto())
                        //.override(800, 800)
                        .dontAnimate()
                        .into(photoView);

                alertbox.setView(view1);
                alertbox.setMessage(currentQuakPost.getMessage());

                alertbox.setPositiveButton("Quak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mQuakChatReference.push().setValue(quakChat, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                //Adding the unique ChatKey generated by Firebase to the QuakInteracters Uid
                                chatKey = databaseReference.getKey();
                                databaseReference.child("quakChatKey").setValue(chatKey);
                                databaseReference.child("reposted").setValue(false);
                                databaseReference.child("messages").push().setValue(new QuakMessage(currentQuakPost.getMessage(), currentQuakPost.getUserName(), currentQuakPost.getUid(), currentQuakPost.getProfilePictureUrl(), currentQuakPost.getPhotoUrl()));
                                databaseReference.child("messages").push().setValue(new QuakMessage(messageEditText.getText().toString().trim(), MainActivity.mUsername, MainActivity.mUid, MainActivity.mProfilePictureUrl, null));
                                DatabaseReference currentUserRef = mUserDatabaseReference.getRef().child(mFirebaseAuth.getCurrentUser().getUid()).child("chats");
                                DatabaseReference quakerUserRef = mUserDatabaseReference.getRef().child(currentQuakPost.getUid()).child("chats");
                                currentUserRef.push().setValue(chatKey);

                                if (!currentQuakPost.getUid().equals(mFirebaseAuth.getCurrentUser().getUid())) {
                                    quakerUserRef.push().setValue(chatKey);
                                }

                            }
                        });
                    }
                });
                alertbox.show();


            }
        });


        return convertView;
    }
}
