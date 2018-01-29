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
import com.example.android.quak.QuakRepost;
import com.example.android.quak.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by YannikSSD on 29.01.2018.
 */

public class ProfileRepostFragment extends Fragment {

    private static ListView mListView;
    private static List<QuakRepost> quakReposts;
    private static List<QuakRepost> userQuakReposts;
    private static QuakRepostAdapterAlternative mAdapter;

    private static final String LOG_TAG = "ProfileRepostFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_repost, container, false);

        Log.i(LOG_TAG, "onCreateView");
        mListView = view.findViewById(R.id.fragment_profile_repost_list_view);
        quakReposts = new ArrayList<>();
        quakReposts = MainActivity.quakReposts;
        userQuakReposts = new ArrayList<>();
        for (int i = 0; i < quakReposts.size(); i++){
            Log.i(LOG_TAG, "for loop");
            if (quakReposts.get(i).getMessage2().getUId() != null){
                Log.i(LOG_TAG, "message 2 Uid not null");
                if (quakReposts.get(i).getMessage2().getUId().equals(MainActivity.mUid)){
                    Log.i(LOG_TAG, "message 2 Uid == main uid");
                    userQuakReposts.add(quakReposts.get(i));
                }
            }
        }
        mAdapter = new QuakRepostAdapterAlternative(getContext(), R.layout.quak_repost_list_item_alternative, userQuakReposts);
        mListView.setAdapter(mAdapter);


        return view;
    }
}
