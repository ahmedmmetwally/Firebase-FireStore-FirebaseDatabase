package com.example.firebaseuploadexample2;

import com.google.firebase.database.Exclude;

public class UpLoad {
    private  String mName;
    private String mImageUrl;
    private String mKey;
    public UpLoad(){

    }

    public UpLoad(String mName, String mImageUrl) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
    @Exclude
    public String getmKey(){
        return this.mKey;
    }
    @Exclude
    public void setmKey(String key){
        this.mKey=key;
    }
}
