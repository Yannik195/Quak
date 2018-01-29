package com.example.android.quak;


/**
 * Created by YannikSSD on 16.12.2017.
 */

public class QuakChat {

    private String mQuakChatKey;
    private String mChatName;
    private String mQuakPosterDisplayName;
    private String mQuakPosterUid;
    private String mQuakPosterPP;
    private String mCurrentUserDisplayName;
    private String mCurrentUserUid;
    private String mCurrentUserPP;
    private String mQuakMessage;
    private String mQuakPhoto;
    private String mInteracterRefKey;
    private String mQuakPosterRefKey;
    private String mLastMessage;
    private long mLastMesageTime;

    public QuakChat(){}

    public QuakChat(String quakChatKey,
                    String chatName,
                    String quakPosterDisplayName,
                    String quakPosterUid,
                    String quakPosterPP,
                    String currentUserDisplayName,
                    String currentUserUid,
                    String currentUserPP,
                    String quakMessage,
                    String quakPhoto,
                    String interacterRefKey,
                    String quakPosterRefKey,
                    String lastMessage,
                    long lastMessageTime){

        mQuakChatKey = quakChatKey;
        mChatName = chatName;
        mQuakPosterDisplayName = quakPosterDisplayName;
        mQuakPosterUid = quakPosterUid;
        mQuakPosterPP = quakPosterPP;
        mCurrentUserDisplayName = currentUserDisplayName;
        mCurrentUserUid = currentUserUid;
        mCurrentUserPP = currentUserPP;
        mQuakMessage = quakMessage;
        mQuakPhoto = quakPhoto;
        mInteracterRefKey = interacterRefKey;
        mQuakPosterRefKey = quakPosterRefKey;
        mLastMessage = lastMessage;
        mLastMesageTime = lastMessageTime;
    }

    public String getChatName(){
        return mChatName;
    }

    public String getQuakPosterDisplayName(){
        return mQuakPosterDisplayName;
    }

    public String getQuakPosterUid(){
        return mQuakPosterUid;
    }

    public String getQuakPosterPP(){
        return mQuakPosterPP;
    }

    public String getCurrentUserDisplayName(){
        return mCurrentUserDisplayName;
    }

    public String getCurrentUserUid(){
        return mCurrentUserUid;
    }

    public String getCurrentUserPP(){
        return mCurrentUserPP;
    }

    public String getQuakChatKey(){
        return mQuakChatKey;
    }

    public String getQuakMessage(){
        return mQuakMessage;
    }

    public String getQuakPhoto(){
        return mQuakPhoto;
    }

    public String getInteracterRefKey(){
        return mInteracterRefKey;
    }

    public String getQuakPosterRefKey(){
        return mQuakPosterRefKey;
    }

    public String getLastMessage(){
        return mLastMessage;
    }

    public long getLastMessageTime(){
        return mLastMesageTime;
    }
}
