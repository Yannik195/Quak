package com.example.android.quak;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.quak.Fragments.NewQuakPostFragment;
import com.example.android.quak.Fragments.ProfileFragment;
import com.example.android.quak.Fragments.ViewPagerAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ListView listView;
    private BottomNavigationView mBottomNavigationView;

    private QuakPostAdapter mAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final int RC_SIGN_IN = 1;

    private String mUsername;
    private String mProfilePictureUrl;

    private static final String LOG_TAG = "MainActivity LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_view_pager);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("quak");
        mFirebaseAuth = FirebaseAuth.getInstance();

        /*
        final ViewPager viewPager = findViewById(R.id.pager);
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new NewQuakPostFragment(),"new Quak");
        viewPager.setAdapter(viewPagerAdapter);
        */


/*
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pager, new NewQuakPostFragment()).commit();
        */

        final FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().addToBackStack("home").add(R.id.frame_layout, new NewQuakPostFragment()).commit();



        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:

                        Log.i("MainActivity", "BNV: Home");
                        manager.beginTransaction().replace(R.id.frame_layout, new NewQuakPostFragment()).commit();


                    case R.id.trending:
                        fabVisibilityGone();
                        Log.i("MainActivity", "BNV: Trending");

                        break;
                    case R.id.chat:
                        fabVisibilityGone();
                        break;
                    case R.id.profile:
                        fabVisibilityGone();
                        Bundle profileArguments = new Bundle();
                        profileArguments.putString("mUsername", mUsername);
                        profileArguments.putString("mProfilePictureUrl", mProfilePictureUrl);
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileFragment.setArguments(profileArguments);
                        manager.beginTransaction().replace(R.id.frame_layout,profileFragment).commit();
                        Log.i("MainActivity", "BNV: Profile");
                        break;
                    case R.id.cloud:
                        fabVisibilityGone();
                        Log.i("MainActivity", "BNV: Cloud");
                        break;

                }
                return true;
            }
        });

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

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

/*
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewQuak.class);
                intent.putExtra("userName", mUsername);
                intent.putExtra("profilePictureUrl", mProfilePictureUrl);
                startActivity(intent);
            }
        });









        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("quak");
        mFirebaseAuth = FirebaseAuth.getInstance();

        fab = findViewById(R.id.fab);
        listView = findViewById(R.id.list_view);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        final List<QuakPost> quakPosts = new ArrayList<>();
        mAdapter = new QuakPostAdapter(this, R.layout.quak_list_item, quakPosts);
        listView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewQuak.class);
                intent.putExtra("userName", mUsername);
                intent.putExtra("profilePictureUrl", mProfilePictureUrl);
                startActivity(intent);
            }
        });

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

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_3:
                        Intent intent = new Intent(MainActivity.this, ProfileAcitvity.class);
                        startActivity(intent);
                        return true;
                    default: return true;
                }
            }
        });

        
    }

    private void changeProfilePicture(){
        Intent intent = new Intent(MainActivity.this, ChooseProfilePictureActivity.class);
        startActivity(intent);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.change_profile_picture:
                changeProfilePicture();
                return true;
            case R.id.time_stamp:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        */
    }

    private void fabVisibilityGone(){
        fab.setVisibility(View.GONE);
    }

}
