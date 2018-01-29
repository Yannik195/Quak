package com.example.android.quak.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakMessage;
import com.example.android.quak.QuakMessageAdapter;
import com.example.android.quak.QuakRepost;
import com.example.android.quak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuakChatReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCurrentChatReference;
    private DatabaseReference mActiveChatMessagesReference;
    private DatabaseReference mActiveChatReference;
    private DatabaseReference mQuakRepostReference;
    private DatabaseReference mUserQuakRepost;
    private DatabaseReference mLastMessageReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;
    private FirebaseAuth mFirebaseAuth;

    private String mCurrentQuakUsername;
    private String mCurrentQuakProfilePictureUrl;
    private String mCurrentQuakUid;

    private String mUsername;
    private String mProfilePictureUrl;
    private String mUid;
    private String mChatKey;
    private String mQuakMessage;
    private String mQuakPhoto;
    private boolean mReposted;

    private EditText editText;
    private ImageView sendButton;
    private ImageButton addPhotoImageButton;
    private TextView repostTextView;
    private ImageView imagePreview;

    private String latestMessageUid;
    private int totalMessages;

    private List<QuakMessage> quakMessageList;
    private QuakMessageAdapter mAdapter;
    private ListView mListView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri selectedImageUri = null;
    private byte[] bytes;

    private static final String LOG_TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editText = findViewById(R.id.edit_text_activity_chat);
        sendButton = findViewById(R.id.send_button_activity_chat);
        mListView = findViewById(R.id.chat_message_list_view);
        repostTextView = findViewById(R.id.repost_text_view);
        addPhotoImageButton = findViewById(R.id.add_photo_image_button);
        imagePreview = findViewById(R.id.image_preview);
        imagePreview.setVisibility(View.GONE);

        editText.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
        repostTextView.setVisibility(View.GONE);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakChatReference = mFirebaseDatabase.getReference().child("quakChat");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mQuakRepostReference = mFirebaseDatabase.getReference().child("quakRepost");


        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("quak_photos");
        mFirebaseAuth = FirebaseAuth.getInstance();

        quakMessageList = new ArrayList<>();
        mAdapter = new QuakMessageAdapter(this, R.layout.message_list_item, quakMessageList);
        mListView.setAdapter(mAdapter);

        mUid = MainActivity.mUid;
        mUsername = MainActivity.mUsername;
        mProfilePictureUrl = MainActivity.mProfilePictureUrl;

        if (getIntent().getExtras() != null){
            mCurrentQuakUsername = getIntent().getExtras().get("userName").toString();
            mCurrentQuakUid = getIntent().getExtras().getString("mUid");
            mChatKey = getIntent().getExtras().getString("chatKey");
            mQuakMessage = getIntent().getExtras().getString("quakMessage");
            mQuakPhoto = getIntent().getExtras().getString("quakPhoto");

            if (getIntent().getExtras().get("profilePictureUrl") == null){
                mCurrentQuakProfilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F36761?alt=media&token=ed06e95c-f402-4503-b792-8f238c0324e8";
            } else {
                mCurrentQuakProfilePictureUrl = getIntent().getExtras().get("profilePictureUrl").toString();
            }
        }

        mActiveChatMessagesReference = mFirebaseDatabase.getReference().child("quakChat").child(mChatKey).child("messages");
        mActiveChatReference = mFirebaseDatabase.getReference().child("quakChat").child(mChatKey).child("reposted");
        mLastMessageReference = mFirebaseDatabase.getReference().child("quakChat").child(mChatKey);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!latestMessageUid.equals(MainActivity.mUid)){
                    if (!TextUtils.isEmpty(editText.getText().toString().trim())){
                        String message = editText.getText().toString().trim();
                        QuakMessage quakMessage = new QuakMessage(message, mUsername, mUid, mProfilePictureUrl, null);
                        mActiveChatMessagesReference.push().setValue(quakMessage);
                        mLastMessageReference.child("lastMessage").setValue(message);
                        mLastMessageReference.child("lastMessageTime").setValue(System.currentTimeMillis());
                        editText.setText("");
                    } else{
                        Toast.makeText(getApplicationContext(),"Insert Text", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Wait for reply", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mActiveChatMessagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = (String) dataSnapshot.child("message").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String uID = (String) dataSnapshot.child("uid").getValue();
                String profilePicture = (String) dataSnapshot.child("profilePicture").getValue();
                String photo = (String) dataSnapshot.child("photo").getValue();

                latestMessageUid = (String) dataSnapshot.child("uid").getValue();

                quakMessageList.add(new QuakMessage(message, userName, uID, profilePicture, photo));
                mAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(mAdapter.getCount() -1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        final View dialogView = View.inflate(ChatActivity.this, R.layout.repost_alert_dialog, null);


        //Reference to the users quakreposts
        mUserQuakRepost = mFirebaseDatabase.getReference().child("users").child(mUid).child("quakRepost");

        mActiveChatMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalMessages = (int) dataSnapshot.getChildrenCount();

                mActiveChatReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mReposted = (boolean) dataSnapshot.getValue();
                        Log.i(LOG_TAG, "Reposted " + mReposted);

                        //if NOT reposted
                        if (!mReposted) {
                            Log.i(LOG_TAG, "1. if Reposted " + mReposted);
                            //Chat finished
                            if (totalMessages >= 3) {

                                //The one supposed to repost
                                if (!latestMessageUid.equals(mUid)) {
                                    repostTextView.setVisibility(View.VISIBLE);
                                    editText.setVisibility(View.GONE);
                                    repostTextView.setText("REPOST");
                                    sendButton.setVisibility(View.GONE);
                                    repostTextView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            AlertDialog.Builder alertBox = new AlertDialog.Builder(ChatActivity.this);
                                            alertBox.setTitle("Repost");
                                            alertBox.setPositiveButton("Repost", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    EditText editText = dialogView.findViewById(R.id.repost_alert_edit_text);
                                                    String headline = editText.getText().toString().trim();
                                                    int likes = 0;

                                                    long time = System.currentTimeMillis();
                                                    QuakRepost quakRepost = new QuakRepost(quakMessageList.get(0), quakMessageList.get(1), quakMessageList.get(2), likes, null, time, headline);

                                                    mQuakRepostReference.push().setValue(quakRepost, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            mActiveChatReference.setValue(true);
                                                            String key = databaseReference.getKey();
                                                            mUserQuakRepost.push().setValue(key);
                                                        }
                                                    });
                                                }
                                            });

                                            alertBox.setView(dialogView);
                                            alertBox.show();

                                        }
                                    });

                                    //The one who quaked
                                } else {
                                    repostTextView.setVisibility(View.VISIBLE);
                                    repostTextView.setTextColor(Color.parseColor("#BDBDBD"));
                                    editText.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.GONE);
                                }

                            } else {
                                //The one supposed to repost
                                if (latestMessageUid.equals(mUid)) {
                                    repostTextView.setVisibility(View.VISIBLE);
                                    repostTextView.setText("Wait for answer");
                                    editText.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.GONE);

                                    //The one supposed to answer
                                } else {
                                    editText.setVisibility(View.VISIBLE);
                                    sendButton.setVisibility(View.GONE);
                                    addPhotoImageButton.setVisibility(View.VISIBLE);
                                    addPhotoImageButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.i(LOG_TAG, "else Reposted " + mReposted);
                            repostTextView.setText("Quak has been posted!");
                            repostTextView.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.GONE);
                            sendButton.setVisibility(View.GONE);
                        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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

            imagePreview.setImageBitmap(bitmap);
            imagePreview.setVisibility(View.VISIBLE);
            addPhotoImageButton.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
                    photoRef.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Uri downloadUri = task.getResult().getDownloadUrl();
                            String message = editText.getText().toString().trim();
                            mLastMessageReference.child("lastMessage").setValue(message);
                            QuakMessage quakMessage = new QuakMessage(message, mUsername, mUid, mProfilePictureUrl, downloadUri.toString());
                            mActiveChatMessagesReference.push().setValue(quakMessage);
                            editText.setText("");
                            imagePreview.setImageResource(0);




                        }
                    });
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        MainActivity.setupFragments();
        MainActivity.pager.setCurrentItem(2, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Log.i(LOG_TAG, "HomeAsUp");
                finish();
                MainActivity.setupFragments();
                MainActivity.pager.setCurrentItem(2, false);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
