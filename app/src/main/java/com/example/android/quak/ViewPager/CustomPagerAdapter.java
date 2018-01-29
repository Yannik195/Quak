package com.example.android.quak.ViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.quak.Fragments.ChatFragment;
import com.example.android.quak.Fragments.NewQuakPostFragment;
import com.example.android.quak.Fragments.ProfileFragment;
import com.example.android.quak.Fragments.RepostFragment;

/**
 * Created by YannikSSD on 26.01.2018.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {


    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0){
            return new NewQuakPostFragment();
        } else if (position == 1){
            return new RepostFragment();
        } else if (position == 2){
            return new ChatFragment();
        } else
            return new ProfileFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }
}
