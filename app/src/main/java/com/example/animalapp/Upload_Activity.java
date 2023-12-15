package com.example.animalapp;

import androidx.appcompat.app.AppCompatActivity;

public class Upload_Activity extends AppCompatActivity {
    private String mName;
    private String mImageUrl;

    public Upload_Activity(){

    }

    public Upload_Activity(String name, String imageUrl){
        mName=name;
        mImageUrl=imageUrl;

    }

    public String getName(){
        return mName;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setName(String name){
        mName=name;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }
}
