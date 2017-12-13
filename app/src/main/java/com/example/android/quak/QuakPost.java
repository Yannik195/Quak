package com.example.android.quak;

/**
 * Created by YannikSSD on 27.11.2017.
 */

public class QuakPost {

    private String mMessage;
    private String mPhotoUrl;
    private String mUserName;
    private String mProfilePictureUrl;

    public QuakPost(){
    }

    public QuakPost(String message, String photoUrl, String userName, String profilePictureUrl){
        mMessage = message;
        mPhotoUrl = photoUrl;
        mUserName = userName;
        mProfilePictureUrl = profilePictureUrl;
    }

    public String getMessage(){
        return mMessage;
    }

    public String getPhotoUrl(){
        return mPhotoUrl;
    }

    public String getUserName(){
        return mUserName;
    }

    public String getProfilePictureUrl(){
        return mProfilePictureUrl;
    }

}
