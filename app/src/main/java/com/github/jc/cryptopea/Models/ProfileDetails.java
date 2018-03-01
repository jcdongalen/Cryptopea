package com.github.jc.cryptopea.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.jc.cryptopea.Utils.SharedPreferencesFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

/**
 * Created by Owner on 2/27/2018.
 */

public class ProfileDetails {

    private static ProfileDetails INSTANCE;
    private Context mContext;
    private Gson gson;
    private SharedPreferencesFactory sharedPreferencesFactory;
    private String user_name = "",
            password = "",
            email_address = "",
            mobile_number = "",
            profile_picture_url = "",
            EtherToken = "";
    private Float RewardedEther = 0.000000000000f;
    private FirebaseUser mUser;

    private ProfileDetails(Context context) {
        this.mContext = context;
        sharedPreferencesFactory = new SharedPreferencesFactory(context);
        gson = new Gson();
    }

    public static synchronized ProfileDetails initialize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        String cachedProfileDetails = sharedPreferences.getString("ProfileDetails", "");

        if(INSTANCE == null){
            if(cachedProfileDetails.equalsIgnoreCase("")){
                INSTANCE = new ProfileDetails(context);
            }
            else{
                INSTANCE = new Gson().fromJson(cachedProfileDetails, ProfileDetails.class);
            }
        }

        return INSTANCE;
    }

    public void updateProfileDetails(String user_name, String password, String email_address, String mobile_number, String profile_picture_url, String EtherToken){
        this.user_name = user_name;
        this.password = password;
        this.email_address = email_address;
        this.mobile_number = mobile_number;
        this.profile_picture_url = profile_picture_url;
        this.EtherToken = EtherToken;
    }

    public void commitChanges(){
        SharedPreferences sharedPreferences = sharedPreferencesFactory.getPreferenceByName("Profile");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ProfileDetails_json = gson.toJson(INSTANCE);
        editor.putString("ProfileDetails", ProfileDetails_json);
        editor.apply();
    }

    public FirebaseUser getmUser() {
        return mUser;
    }

    public void setmUser(FirebaseUser mUser) {
        this.mUser = mUser;
    }

    public Float getRewardedEther() {
        return RewardedEther;
    }

    public void setRewardedEther(Float rewardedEther) {
        RewardedEther = rewardedEther;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public String getEtherToken() {
        return EtherToken;
    }
}
