package com.example.android.quak.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakChat;
import com.example.android.quak.QuakChatAdapter;
import com.example.android.quak.QuakMessage;
import com.example.android.quak.QuakRepost;
import com.example.android.quak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YannikSSD on 14.12.2017.
 */

public class ChatFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mCurrentUserChats;
    private DatabaseReference mQuakChatReference;
    private String mQuakChatKey;

    private String mUid;

    private List<QuakChat> chats;
    public static List<String> chatKeys;
    private QuakChatAdapter mQuakChatAdapter;
    private ListView mListView;
    private static final String LOG_TAG = "ChatFragment";
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
        }


        getActivity().setTitle("Chat");
        Log.i(LOG_TAG, "onCreateView");
        chatKeys = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mQuakChatReference = mFirebaseDatabase.getReference().child("quakChat");

        mUid = MainActivity.mUid;

        mCurrentUserChats = mUsersDatabaseReference.child(mUid).child("chats");

        mListView = view.findViewById(R.id.chat_list_view);

        chats = new ArrayList<>();
        mQuakChatAdapter = new QuakChatAdapter(getContext(), R.layout.chat_list_item, chats);
        mListView.setAdapter(mQuakChatAdapter);


        mCurrentUserChats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mQuakChatKey = (String) dataSnapshot.getValue();

                mQuakChatReference.child(mQuakChatKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String chatKey = dataSnapshot.getKey();
                        Log.i(LOG_TAG, "Listener Key " + dataSnapshot.getKey());
                        String chatName = (String) dataSnapshot.child("chatName").getValue();
                        String quakPosterDisplayName = (String) dataSnapshot.child("quakPosterDisplayName").getValue();
                        String quakPosterUid = (String) dataSnapshot.child("quakPosterUid").getValue();
                        String quakPosterPP = (String) dataSnapshot.child("quakPosterPP").getValue();
                        String currentUserDisplayName = (String) dataSnapshot.child("currentUserDisplayName").getValue();
                        String currentUserUid = (String) dataSnapshot.child("currentUserUid").getValue();
                        String currentUserPP = (String) dataSnapshot.child("currentUserPP").getValue();
                        String quakMessage = (String) dataSnapshot.child("quakMessage").getValue();
                        String quakPhoto = (String) dataSnapshot.child("quakPhoto").getValue();
                        String interactUserRefKey = (String) dataSnapshot.child("interactUserRefKey").getValue();
                        String quakPosterChatKey = (String) dataSnapshot.child("quakerUserRefKey").getValue();
                        String lastMessage = (String) dataSnapshot.child("lastMessage").getValue();
                        long lastMessageTime = (long) dataSnapshot.child("lastMessageTime").getValue();


                        chats.add(new QuakChat(
                                chatKey,
                                chatName,
                                quakPosterDisplayName,
                                quakPosterUid, quakPosterPP,
                                currentUserDisplayName,
                                currentUserUid,
                                currentUserPP,
                                quakMessage,
                                quakPhoto,
                                interactUserRefKey,
                                quakPosterChatKey,
                                lastMessage,
                                lastMessageTime
                        ));

                        mQuakChatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //QuakChat currentChat = chats.get(i);
                QuakChat currentQuakChat = (QuakChat) adapterView.getItemAtPosition(i);
                String chatKey = currentQuakChat.getQuakChatKey();
                Log.i(LOG_TAG, "Key: " + chatKey);
                Intent intent = new Intent(getContext(), ChatActivity.class);

                intent.putExtra("userName", currentQuakChat.getQuakPosterDisplayName());
                intent.putExtra("profilePictureUrl", currentQuakChat.getQuakPosterPP());
                intent.putExtra("mUid", currentQuakChat.getQuakPosterUid());
                intent.putExtra("quakMessage", currentQuakChat.getQuakMessage());
                intent.putExtra("quakPhoto", currentQuakChat.getQuakPhoto());

                intent.putExtra("chatKey", chatKey);
                getContext().startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int chatInt = i;
                final QuakChat currentQuakChat = (QuakChat) adapterView.getItemAtPosition(i);
                AlertDialog.Builder alertBox = new AlertDialog.Builder(getContext());
                alertBox.setTitle("Delete Chat?");
                alertBox.setMessage("Delete Chat?");
                alertBox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mQuakChatReference.child(currentQuakChat.getQuakChatKey()).removeValue();
                        chats.remove(chatInt);
                        mUsersDatabaseReference.child(currentQuakChat.getCurrentUserUid()).child("chats").child(currentQuakChat.getInteracterRefKey()).removeValue();

                        //Causes Problems if interactor and poster are the same person
                        mUsersDatabaseReference.child(currentQuakChat.getQuakPosterUid()).child("chats").child(currentQuakChat.getQuakPosterRefKey()).removeValue();

                        mQuakChatAdapter.notifyDataSetChanged();

                    }
                });
                alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertBox.show();


                return true;
            }
        });


        return view;
    }

    public static List<String> getChatKeys(){
        return chatKeys;
    }

}