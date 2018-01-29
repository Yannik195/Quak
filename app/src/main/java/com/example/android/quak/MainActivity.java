package com.example.android.quak;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.example.android.quak.Fragments.NewQuakPostFragment;
import com.example.android.quak.Fragments.ProfilePostFragment;
import com.example.android.quak.ViewPager.CustomPagerAdapter;
import com.example.android.quak.ViewPager.CustomViewPager;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static BottomNavigationView mBottomNavigationView;

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static ChildEventListener mQuakPostChildEventListener;
    private static ValueEventListener mQuakPostValueEventListener;
    private static ChildEventListener mQuakRepostChildEventListener;
    private static ValueEventListener mQuaRepostValueEventListener;

    public static final int RC_SIGN_IN = 1;

    public static String mUsername;
    public static String mProfilePictureUrl;
    public static String mUid;

    public static List<QuakPost> quakPosts;
    public static List<QuakRepost> quakReposts;

    private static boolean quakPostsLoaded = false;
    private static boolean quakRepostsLoaded = false;
    private static boolean fragmentsLoaded = false;

    public static CustomViewPager pager;
    private static CustomPagerAdapter adapter;

    private static final String ADMOB_APP_ID = "ca-app-pub-8281538631140870~3224312808";
    private static final String ADMOB_TEST_BANNER = "ca-app-pub-3940256099942544/6300978111";

    private static final String LOG_TAG = "MainActivity LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "onCreate");

        pager = findViewById(R.id.custom_pager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view1);

        adapter = new CustomPagerAdapter(getSupportFragmentManager());

        quakPosts = new ArrayList<>();
        quakReposts = new ArrayList<>();

        setupFirebase();
        checkFirstRun();
    }

    public static List<QuakRepost> loadReposts(){
        mQuakRepostChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = (String) dataSnapshot.child("message1").child("message").getValue();
                String photoUrl = (String) dataSnapshot.child("message1").child("photo").getValue();
                String profilePicture = (String) dataSnapshot.child("message1").child("profilePicture").getValue();
                String uid = (String) dataSnapshot.child("message1").child("uid").getValue();
                String userName = (String) dataSnapshot.child("message1").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage1 = new QuakMessage(message, userName, uid, profilePicture, photoUrl);

                String message2 = (String) dataSnapshot.child("message2").child("message").getValue();
                String photoUrl2 = (String) dataSnapshot.child("message2").child("photo").getValue();
                String profilePicture2 = (String) dataSnapshot.child("message2").child("profilePicture").getValue();
                String uid2 = (String) dataSnapshot.child("message2").child("uid").getValue();
                String userName2 = (String) dataSnapshot.child("message2").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage2 = new QuakMessage(message2, userName2, uid2, profilePicture2, photoUrl2);

                String message3 = (String) dataSnapshot.child("message3").child("message").getValue();
                String photoUrl3 = (String) dataSnapshot.child("message3").child("photo").getValue();
                String profilePicture3 = (String) dataSnapshot.child("message3").child("profilePicture").getValue();
                String uid3 = (String) dataSnapshot.child("message3").child("uid").getValue();
                String userName3 = (String) dataSnapshot.child("message3").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage3 = new QuakMessage(message3, userName3, uid3, profilePicture3, photoUrl3);

                int likes = dataSnapshot.child("likes").getValue(Integer.class);
                long time = dataSnapshot.child("repostTime").getValue(Long.class);
                String headline = (String) dataSnapshot.child("headline").getValue();
                String key = dataSnapshot.getKey();

                quakReposts.add(new QuakRepost(quakMessage1, quakMessage2, quakMessage3, likes, key, time, headline));

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

        };

        mDatabaseReference.child("quakRepost").addChildEventListener(mQuakRepostChildEventListener);

        mQuaRepostValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child("quakRepost").removeEventListener(mQuakRepostChildEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("quakRepost").addValueEventListener(mQuaRepostValueEventListener);

        return quakReposts;
    }

    private void setupFirebase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static List<QuakPost> loadQuaks(){
        Log.i(LOG_TAG, "LoadQuaks");
        mQuakPostChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String message = (String) dataSnapshot.child("message").getValue();
                String photoUrl = (String) dataSnapshot.child("photoUrl").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String profilePictureUrl = (String) dataSnapshot.child("profilePictureUrl").getValue();
                String Uid = (String) dataSnapshot.child("uid").getValue();

                Log.i(LOG_TAG, "Message: " + message);
                if (userName == null || TextUtils.isEmpty(userName)){
                    userName = "No Username Available";
                }
                if (profilePictureUrl == null || TextUtils.isEmpty(profilePictureUrl)){
                    profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F37001?alt=media&token=c6d11d41-e033-40ac-a63a-7da212cd6dda";
                }

                long time;
                if (dataSnapshot.child("timeInMillis").getValue() == null){
                    time = 1515682328929L;
                } else {
                    time = (long) dataSnapshot.child("timeInMillis").getValue();
                }

                quakPosts.add(new QuakPost(message, photoUrl, userName, profilePictureUrl, Uid, time ));
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
        };
        mDatabaseReference.child("quak").addChildEventListener(mQuakPostChildEventListener);

        mDatabaseReference.child("quak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child("quak").removeEventListener(mQuakPostChildEventListener);
                NewQuakPostFragment.mQuakPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return quakPosts;
    }

    public static List<QuakRepost> loadRepostsInitialize(){
        Log.i(LOG_TAG, "LoadRepostsIntialize");
        mQuakRepostChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = (String) dataSnapshot.child("message1").child("message").getValue();
                String photoUrl = (String) dataSnapshot.child("message1").child("photo").getValue();
                String profilePicture = (String) dataSnapshot.child("message1").child("profilePicture").getValue();
                String uid = (String) dataSnapshot.child("message1").child("uid").getValue();
                String userName = (String) dataSnapshot.child("message1").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage1 = new QuakMessage(message, userName, uid, profilePicture, photoUrl);

                String message2 = (String) dataSnapshot.child("message2").child("message").getValue();
                String photoUrl2 = (String) dataSnapshot.child("message2").child("photo").getValue();
                String profilePicture2 = (String) dataSnapshot.child("message2").child("profilePicture").getValue();
                String uid2 = (String) dataSnapshot.child("message2").child("uid").getValue();
                String userName2 = (String) dataSnapshot.child("message2").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage2 = new QuakMessage(message2, userName2, uid2, profilePicture2, photoUrl2);

                String message3 = (String) dataSnapshot.child("message3").child("message").getValue();
                String photoUrl3 = (String) dataSnapshot.child("message3").child("photo").getValue();
                String profilePicture3 = (String) dataSnapshot.child("message3").child("profilePicture").getValue();
                String uid3 = (String) dataSnapshot.child("message3").child("uid").getValue();
                String userName3 = (String) dataSnapshot.child("message3").child("userName").getValue();
                Log.i(LOG_TAG, "Message: " + message + photoUrl + profilePicture+ uid+ userName);

                QuakMessage quakMessage3 = new QuakMessage(message3, userName3, uid3, profilePicture3, photoUrl3);

                int likes = dataSnapshot.child("likes").getValue(Integer.class);
                long time = dataSnapshot.child("repostTime").getValue(Long.class);
                String headline = (String) dataSnapshot.child("headline").getValue();
                String key = dataSnapshot.getKey();

                quakReposts.add(new QuakRepost(quakMessage1, quakMessage2, quakMessage3, likes, key, time, headline));

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

        };

        mDatabaseReference.child("quakRepost").addChildEventListener(mQuakRepostChildEventListener);

        mQuaRepostValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child("quakRepost").removeEventListener(mQuakRepostChildEventListener);
                quakRepostsLoaded = true;
                if (quakPostsLoaded && quakRepostsLoaded && !fragmentsLoaded){
                    setupFragments();
                    fragmentsLoaded = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("quakRepost").addValueEventListener(mQuaRepostValueEventListener);

        return quakReposts;
    }

    public static List<QuakPost> loadQuaksInitialize(){
        Log.i(LOG_TAG, "LoadQuaksInitialize");
        mQuakPostChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String message = (String) dataSnapshot.child("message").getValue();
                String photoUrl = (String) dataSnapshot.child("photoUrl").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String profilePictureUrl = (String) dataSnapshot.child("profilePictureUrl").getValue();
                String Uid = (String) dataSnapshot.child("uid").getValue();

                Log.i(LOG_TAG, "Message: " + message);
                if (userName == null || TextUtils.isEmpty(userName)){
                    userName = "No Username Available";
                }
                if (profilePictureUrl == null || TextUtils.isEmpty(profilePictureUrl)){
                    profilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F37001?alt=media&token=c6d11d41-e033-40ac-a63a-7da212cd6dda";
                }

                long time;
                if (dataSnapshot.child("timeInMillis").getValue() == null){
                    time = 1515682328929L;
                } else {
                    time = (long) dataSnapshot.child("timeInMillis").getValue();
                }

                quakPosts.add(new QuakPost(message, photoUrl, userName, profilePictureUrl, Uid, time ));
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
        };
        mDatabaseReference.child("quak").addChildEventListener(mQuakPostChildEventListener);

        mDatabaseReference.child("quak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabaseReference.child("quak").removeEventListener(mQuakPostChildEventListener);
                quakPostsLoaded = true;

                if (quakPostsLoaded && quakRepostsLoaded && !fragmentsLoaded){
                    setupFragments();
                    fragmentsLoaded = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return quakPosts;
    }

    public static void setupFragments(){
        Log.i(LOG_TAG, "SetupFragmetns");
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Log.i(LOG_TAG, "Home");
                        pager.setCurrentItem(0,false);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.trending:
                        pager.setCurrentItem(1,false);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.chat:
                        pager.setCurrentItem(2,false);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.profile:
                        pager.setCurrentItem(3,false);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            Log.i(LOG_TAG, "checkFirstRun: normal run");

            addAuthStateListener();
            loadQuaksInitialize();
            loadRepostsInitialize();
            return;
        } else if (savedVersionCode == DOESNT_EXIST) {

            alertDialogPrivacyPolicyUserGuidelines();
            Log.i(LOG_TAG, "checkFirstRun: new install");
            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {

            Log.i(LOG_TAG, "checkFirstRun: upgrade");
            // TODO This is an upgrade
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void alertDialogPrivacyPolicyUserGuidelines(){
        AlertDialog.Builder alertBox = new AlertDialog.Builder(MainActivity.this);
        alertBox.setCancelable(false);
        final View alertBoxView = View.inflate(MainActivity.this, R.layout.privacy_policy_guidlines_layout, null);
        alertBox.setView(alertBoxView);

        alertBox.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog dialog = alertBox.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox agreeCheckBox = alertBoxView.findViewById(R.id.agree_checkbox);
                if (agreeCheckBox.isChecked()){
                    dialog.dismiss();

                    addAuthStateListener();
                } else {
                    Toast.makeText(MainActivity.this, "Not Checked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RC_SIGN_IN:
                loadQuaksInitialize();
                loadRepostsInitialize();
        }
    }

    private void addAuthStateListener(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("MainActivity", "AuthStateListener ");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUsername = user.getDisplayName();
                    mUid = user.getUid();
                    if (user.getPhotoUrl() == null) {
                        mProfilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/quak-2b421.appspot.com/o/profile_pictures%2F36761?alt=media&token=ed06e95c-f402-4503-b792-8f238c0324e8";
                    } else {
                        mProfilePictureUrl = user.getPhotoUrl().toString();
                    }
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void setProfilePictureUrl(String Url){
        mProfilePictureUrl = Url;
    }

    public static void increaseLike (DataSnapshot dataSnapshot, QuakRepost quakRepost, DatabaseReference databaseReference){
        int likes = dataSnapshot.getValue(Integer.class);
        databaseReference.child(quakRepost.getKey()).child("likes").setValue(likes + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                AuthUI.getInstance().signOut(this);
                pager.setAdapter(null);
                return true;
            case R.id.detach:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
