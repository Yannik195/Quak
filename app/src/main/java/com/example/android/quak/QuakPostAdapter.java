package com.example.android.quak;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;


import java.util.List;

public class QuakPostAdapter extends ArrayAdapter<QuakPost> {

    public QuakPostAdapter(@NonNull Context context, int resource, List<QuakPost> object ) {
        super(context, resource, object);
    }

    /*
    private RequestOptions profilePictureOptions = new RequestOptions()
            .centerCrop()
            .override(50,50)
            .dontAnimate()
            .transform(new RoundedCorners(50));

    private RequestOptions photoRequestOptions = new RequestOptions()
            .dontAnimate()
            .override(Resources.getSystem().getDisplayMetrics().widthPixels, 700)
            .centerCrop();
            */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.quak_list_item, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.quak_message_text_view);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.quak_image_view);
        TextView userNameTextView = convertView.findViewById(R.id.user_text_view);
        final ImageView profilePictureImageView = convertView.findViewById(R.id.user_image_view);

        //QuakPost currentQuakPost = getItem(position);

        QuakPost currentQuakPost = getItem(super.getCount() - position -1);

        messageTextView.setText(currentQuakPost.getMessage());
        userNameTextView.setText(currentQuakPost.getUserName());

 /*
        Glide.with(getContext())
                .load(currentQuakPost.getProfilePictureUrl())
                .apply(profilePictureOptions)
                .into(profilePictureImageView);

        Glide.with(getContext())
                .load(currentQuakPost.getPhotoUrl())
                .apply(photoRequestOptions)
                .into(imageView);
*/


        Glide.with(getContext()).load(currentQuakPost.getProfilePictureUrl()).asBitmap().centerCrop().override(100,100).into(new BitmapImageViewTarget(profilePictureImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profilePictureImageView.setImageDrawable(circularBitmapDrawable);
            }
        });




 /*
        Glide.with(getContext())
                .load(currentQuakPost.getProfilePictureUrl())
                .dontAnimate()
                .override(120,120)
                .centerCrop()
                .into(profilePictureImageView);
*/


        Glide.with(getContext())
                .load(currentQuakPost.getPhotoUrl())
                .dontAnimate()
                .override(Resources.getSystem().getDisplayMetrics().widthPixels, 700)
                .centerCrop()
                .into(imageView);


        return convertView;
    }
}
