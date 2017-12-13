package com.example.android.quak;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by YannikSSD on 27.11.2017.
 */

public class FirebaseQuakViewHolder extends RecyclerView.ViewHolder{

    View mView;
    Context mContext;


    public FirebaseQuakViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindQuak (QuakPost quakPost){
        TextView messageTextView = mView.findViewById(R.id.quak_message_text_view);

        messageTextView.setText(quakPost.getMessage());
    }
}
