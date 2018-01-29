package com.example.android.quak;

/**
 * Created by YannikSSD on 29.12.2017.
 */

public class QuakRepost {

    private QuakMessage mMessage1;
    private QuakMessage mMessage2;
    private QuakMessage mMessage3;
    private int mLikes;
    private String mKey;
    private long mTime;
    private String mHeadline;

    public void setmLikes(int mLikes) {
        this.mLikes = mLikes;
    }




    public QuakRepost(){}



    public QuakRepost(QuakMessage message1, QuakMessage message2, QuakMessage message3, int likes, String key, long time, String headline){

        mMessage1 = message1;
        mMessage2 = message2;
        mMessage3 = message3;
        mLikes = likes;
        mKey = key;
        mTime = time;
        mHeadline = headline;

    }

    public QuakMessage getMessage1(){
        return mMessage1;
    }

    public QuakMessage getMessage2(){
        return mMessage2;
    }

    public QuakMessage getMessage3(){
        return mMessage3;
    }

    public int getLikes(){
        return mLikes;
    }

    public String getKey(){
        return mKey;
    }

    public long getRepostTime() {
        return mTime;
    }

    public String getHeadline() {
        return mHeadline;
    }


}
