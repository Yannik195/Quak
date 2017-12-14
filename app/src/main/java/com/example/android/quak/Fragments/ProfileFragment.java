package com.example.android.quak.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.R;

/**
 * Created by YannikSSD on 13.12.2017.
 */

public class ProfileFragment extends Fragment {

    private String mUsername;
    private String mProfilePictureUrl;

    private ImageView mProfilePictureImageView;
    private TextView mProfileUserNameTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");

        mProfilePictureImageView = view.findViewById(R.id.profile_profile_picture_image_view);
        mProfileUserNameTextView = view.findViewById(R.id.profile_user_name_text_view);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            mUsername = bundle.getString("mUsername");
            mProfilePictureUrl = bundle.getString("mProfilePictureUrl");
            Log.i("ProfileFragment", "bunde: " + mUsername + " " + mProfilePictureUrl);
        }



        Glide.with(getContext()).load(mProfilePictureUrl).asBitmap().centerCrop().override(700,700).into(new BitmapImageViewTarget(mProfilePictureImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mProfilePictureImageView.setImageDrawable(circularBitmapDrawable);
            }
        });

        mProfileUserNameTextView.setText(mUsername);


        return view;
    }
}
