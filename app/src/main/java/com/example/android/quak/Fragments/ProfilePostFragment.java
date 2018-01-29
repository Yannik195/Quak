package com.example.android.quak.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.quak.Adapters.NewQuakPostAdapterAlternative;
import com.example.android.quak.Adapters.NewQuakPostAdapterAlternativeProfile;
import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakPost;
import com.example.android.quak.QuakPostProfile;
import com.example.android.quak.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YannikSSD on 25.01.2018.
 */

public class ProfilePostFragment extends android.support.v4.app.Fragment{


    private static ListView mListView;
    private static List<QuakPost> quakPosts;
    private static List<QuakPost> userQuakPosts;
    private static NewQuakPostAdapterAlternative mAdapter;


    private static final String LOG_TAG = "ProfilePostFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_post,container ,false);

        Log.i(LOG_TAG, "onCreateView");

        mListView = view.findViewById(R.id.fragment_profile_post_list_view);
        quakPosts = new ArrayList<>();
        quakPosts = MainActivity.quakPosts;
        userQuakPosts = new ArrayList<>();
        for (int i = 0; i < quakPosts.size(); i++){
            if (quakPosts.get(i).getUid() != null){
                if (quakPosts.get(i).getUid().equals(MainActivity.mUid)){
                    userQuakPosts.add(quakPosts.get(i));
                }
            }
        }
        mAdapter = new NewQuakPostAdapterAlternative(getContext(), R.layout.quak_list_item_alternative, userQuakPosts);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }
}
