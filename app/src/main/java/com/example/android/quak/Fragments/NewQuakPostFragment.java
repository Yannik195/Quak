package com.example.android.quak.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.quak.Adapters.NewQuakPostAdapterAlternative;
import com.example.android.quak.MainActivity;
import com.example.android.quak.NewQuak;
import com.example.android.quak.QuakPost;
import com.example.android.quak.R;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class NewQuakPostFragment extends Fragment {

    private ListView listView;
    public static NewQuakPostAdapterAlternative mQuakPostAdapter;
    private FloatingActionButton mFloatingActionButton;
    private ProgressBar loadingBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean loadingInProgress = false;

    private AbsListView.OnScrollListener onScrollListener;

    private View view;
    private ImageView photoImageView;


    private String mUsername;
    private String mProfilePictureUrl;
    private String mUid;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    public static List<QuakPost> quakPosts;

    private String lastPostId;
    private List<String> loadedKeyList;
    private boolean loaded = false;

    private AdView mAdView;

    private static final String LOG_TAG = "NewQuakPostFragment LOG";

    public NewQuakPostFragment(){}

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        view = inflater.inflate(R.layout.new_quak_post_fragment, container, false);

        getActivity().setTitle("Quak");

        mFloatingActionButton = view.findViewById(R.id.new_quak_post_fragment_fab);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        loadingBar = view.findViewById(R.id.progress_bar);
        mAdView = view.findViewById(R.id.adView);
        /*
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("quak");

        photoImageView = view.findViewById(R.id.photo_image_view);

        listView = view.findViewById(R.id.new_quak_post_list_view);

        quakPosts = new ArrayList<>();
        quakPosts = MainActivity.quakPosts;
        mQuakPostAdapter = new NewQuakPostAdapterAlternative(getContext(), R.layout.quak_list_item_alternative, quakPosts);
        listView.setAdapter(mQuakPostAdapter);

        loadedKeyList = new ArrayList<>();

        mUsername = MainActivity.mUsername;
        mUid = MainActivity.mUid;
        mProfilePictureUrl = MainActivity.mProfilePictureUrl;

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewQuak.class);
                intent.putExtra("userName", mUsername);
                intent.putExtra("profilePictureUrl", mProfilePictureUrl);
                intent.putExtra("mUid", mUid);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                quakPosts.clear();
                quakPosts = MainActivity.loadQuaks();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        return view;
    }
}
