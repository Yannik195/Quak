package com.example.android.quak;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.Currency;
import java.util.List;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.android.quak.MainActivity;


public class QuakMessageAdapter extends ArrayAdapter<QuakMessage> {


    public QuakMessageAdapter(@NonNull Context context, int resource, @NonNull List<QuakMessage> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_list_item, parent, false);
        }

        String uID = MainActivity.mUid;
        String userPhoto = MainActivity.mProfilePictureUrl;
        QuakMessage currentQuakMessage = getItem(position);
        Log.i("Adapter", "PP" + userPhoto + currentQuakMessage.getProfilePicture());


        Log.i("Adapter", "GetView");

        Log.i("Adapter" , "Uid + currentUI: " + MainActivity.mUid + currentQuakMessage.getUId());


        TextView messageTextView = (TextView) convertView.findViewById(R.id.message_text_view);
        TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name_text_view);
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.message_photo_image_view);
        final ImageView profilePictureImageViewRight = convertView.findViewById(R.id.profile_picture_image_view_messages_right);
        final ImageView profilePictureImageViewLeft = convertView.findViewById(R.id.profile_picture_image_view_messages_left);
        LinearLayout linearLayout = convertView.findViewById(R.id.message_list_item_layout);
        RelativeLayout layout = convertView.findViewById(R.id.relative_wrap_layout);




        /*
        RelativeLayout.LayoutParams myppParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        myppParam.addRule(RelativeLayout.END_OF, R.id.message_list_item_layout);

        RelativeLayout.LayoutParams heppParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        heppParam.addRule(RelativeLayout.START_OF, R.id.message_list_item_layout);
        */



        String message = currentQuakMessage.getMessage();
        String userName = currentQuakMessage.getUserName();
        String photo = currentQuakMessage.getPhoto();
        String quakerPhoto = currentQuakMessage.getProfilePicture();

        messageTextView.setText(message);
        userNameTextView.setText(userName);


        if (photo != null){
            Glide.with(getContext())
                    .load(photo)
                    .dontAnimate()
                    .override(600,600)
                    .into(photoImageView);
        } else {
            photoImageView.setVisibility(View.GONE);
        }



        if (uID.equals(currentQuakMessage.getUId())){
            //profilePictureImageView.setLayoutParams(myppParam);
            layout.setGravity(Gravity.END);
            messageTextView.setGravity(Gravity.END);
            userNameTextView.setGravity(Gravity.END);
            linearLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.message_background_1));


            profilePictureImageViewLeft.setVisibility(View.GONE);
            profilePictureImageViewRight.setVisibility(View.VISIBLE);
            /*
            Glide.with(getContext())
                    .load(userPhoto)
                    .dontAnimate()
                    .override(200,200)
                    .centerCrop()
                    .into(profilePictureImageViewRight);*/

            Glide.with(getContext()).load(userPhoto).asBitmap().centerCrop().override(200,200).into(new BitmapImageViewTarget(profilePictureImageViewRight) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profilePictureImageViewRight.setImageDrawable(circularBitmapDrawable);
                }
            });


        } else {
            //profilePictureImageView.setLayoutParams(heppParam);

            layout.setGravity(Gravity.START);
            messageTextView.setGravity(Gravity.START);
            userNameTextView.setGravity(Gravity.START);
            linearLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.message_background_2));

            profilePictureImageViewRight.setVisibility(View.GONE);
            profilePictureImageViewLeft.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(userPhoto).asBitmap().centerCrop().override(200,200).into(new BitmapImageViewTarget(profilePictureImageViewLeft) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profilePictureImageViewLeft.setImageDrawable(circularBitmapDrawable);
                }
            });
        }


        return convertView;
    }


}
