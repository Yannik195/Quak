package com.example.android.quak;

import java.io.StringReader;

/**
 * Created by YannikSSD on 14.12.2017.
 */

public class QuakMessage {

    private String mUId;
    private String mUserName;
    private String mMessage;
    private String mProfilePicture;
    private String mPhoto;

    public QuakMessage(){}

    public QuakMessage(String message, String userName, String uId, String profilePicture, String photo){
        mUId = uId;
        mUserName = userName;
        mMessage = message;
        mProfilePicture = profilePicture;
        mPhoto = photo;
    }

    public String getUserName(){
        return mUserName;
    }

    public String getUId(){
        return mUId;
    }

    public String getMessage(){
        return mMessage;
    }

    public String getProfilePicture(){
        return mProfilePicture;
    }

    public String getPhoto(){
        return mPhoto;
    }


}
