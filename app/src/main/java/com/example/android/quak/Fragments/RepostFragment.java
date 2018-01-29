package com.example.android.quak.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.quak.Adapters.QuakRepostAdapterAlternative;
import com.example.android.quak.MainActivity;
import com.example.android.quak.QuakChat;
import com.example.android.quak.QuakMessage;
import com.example.android.quak.QuakRepost;
import com.example.android.quak.QuakRepostAdapter;
import com.example.android.quak.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by YannikSSD on 29.12.2017.
 */

public class RepostFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuakRepostReference;
    private ChildEventListener mChildEventListener;

    private List<QuakRepost> reposts;
    private QuakRepostAdapterAlternative mAdapter;
    private ListView mListView;

    private static final String LOG_TAG = "RepostFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repost, container, false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mQuakRepostReference = mFirebaseDatabase.getReference().child("quakRepost");

        mListView = view.findViewById(R.id.repost_fragment_list);
        reposts = new ArrayList<>();
        reposts = MainActivity.quakReposts;
        mAdapter = new QuakRepostAdapterAlternative(getContext(), R.layout.quak_repost_list_item_alternative, reposts);
        mListView.setAdapter(mAdapter);


        return view;
    }
}
