package com.example.android.quak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.quak.Fragments.ChatActivity;
import com.example.android.quak.Fragments.ChatFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by YannikSSD on 17.12.2017.
 */

public class QuakChatAdapter extends ArrayAdapter<QuakChat>{


    private static final String LOG_TAG = "QuakChatAdapter";

    public QuakChatAdapter(@NonNull Context context, int resource, @NonNull List<QuakChat> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chat_list_item, parent, false);
        }


        final ImageView imageView = convertView.findViewById(R.id.quak_poster_image_view);
        TextView textView = convertView.findViewById(R.id.name_text_view);
        final LinearLayout listItem = convertView.findViewById(R.id.chat_list_item_layout);
        TextView lastMessageTextView = convertView.findViewById(R.id.last_message_text_view);
        TextView timeTextView = convertView.findViewById(R.id.time_text_view_chat_fragment);

        QuakChat currentQuakChat = getItem(position);
        //QuakChat currentQuakChat = getItem(super.getCount() - position -1);

        textView.setText(currentQuakChat.getQuakMessage());
        String pPUrl = currentQuakChat.getQuakPosterPP();

        if (currentQuakChat.getQuakPosterUid().equals(MainActivity.mUid)){
            Glide.with(getContext()).load(currentQuakChat.getCurrentUserPP()).asBitmap().centerCrop().override(200,200).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            Glide.with(getContext()).load(currentQuakChat.getQuakPosterPP()).asBitmap().centerCrop().override(200,200).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        lastMessageTextView.setText(currentQuakChat.getLastMessage());


        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        timeTextView.setText(dateFormat.format(currentQuakChat.getLastMessageTime()));






        return convertView;
    }
}
