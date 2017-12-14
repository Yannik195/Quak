package com.example.android.quak.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.quak.MainActivity;
import com.example.android.quak.NewQuak;
import com.example.android.quak.QuakPost;
import com.example.android.quak.QuakPostAdapter;
import com.example.android.quak.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewQuakPostFragment extends Fragment {

    private ListView listView;
    private QuakPostAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;

    public static final int RC_SIGN_IN = 1;

    private String mUsername;
    private String mProfilePictureUrl;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final String LOG_TAG = "NewQuakPostFragment LOG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Started");
        View view = inflater.inflate(R.layout.new_quak_post_fragment, container, false);
        getActivity().setTitle("Quak");

        mFloatingActionButton = view.findViewById(R.id.new_quak_post_fragment_fab);



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("quak");
        mFirebaseAuth = FirebaseAuth.getInstance();

        listView = view.findViewById(R.id.new_quak_post_list_view);

        final List<QuakPost> quakPosts = new ArrayList<>();
        mAdapter = new QuakPostAdapter(getContext(), R.layout.quak_list_item, quakPosts);
        listView.setAdapter(mAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();

                Log.i("User Info ", "providerid, uid, name, email, photoUrl: " + providerId + uid + name + email + photoUrl);
            }
        }



        mDatabaseReference.limitToLast(25).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String message = (String) dataSnapshot.child("message").getValue();
                String photoUrl = (String) dataSnapshot.child("photoUrl").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String profilePictureUrl = (String) dataSnapshot.child("profilePictureUrl").getValue();
                if (userName == null || TextUtils.isEmpty(userName)){
                    userName = "No Username Available";
                }

                if (profilePictureUrl == null || TextUtils.isEmpty(profilePictureUrl)){
                    Log.i("emptyProfilePicture", "pp " + message);
                    profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F37001?alt=media&token=c6d11d41-e033-40ac-a63a-7da212cd6dda";
                }

                quakPosts.add(new QuakPost(message, photoUrl, userName, profilePictureUrl));
                mAdapter.notifyDataSetChanged();
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


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    mUsername = user.getDisplayName();
                    mProfilePictureUrl = user.getPhotoUrl().toString();

                    Log.i(LOG_TAG, "Username: " + mUsername);
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewQuak.class);
                intent.putExtra("userName", mUsername);
                intent.putExtra("profilePictureUrl", mProfilePictureUrl);
                startActivity(intent);
            }
        });

        return view;
    }
}
