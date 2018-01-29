package com.example.android.quak;

/**
 * Created by YannikSSD on 27.11.2017.
 */

public class QuakPost {

    private String mMessage;
    private String mPhotoUrl;
    private String mUserName;
    private String mProfilePictureUrl;
    private String mUid;
    private long mTimeInMillis;

    public QuakPost(){
    }

    public QuakPost(String message, String photoUrl, String userName, String profilePictureUrl, String Uid, long timeInMillis){
        mMessage = message;
        mPhotoUrl = photoUrl;
        mUserName = userName;
        mProfilePictureUrl = profilePictureUrl;
        mUid = Uid;
        mTimeInMillis = timeInMillis;
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

    public String getUid(){
        return mUid;
    }

    public long getTimeInMillis(){
        return mTimeInMillis;
    }

}
