package com.github.jc.cryptopea.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.jc.cryptopea.Utils.SharedPreferencesFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

/**
 * Created by Owner on 2/27/2018.
 */

public class ProfileDetails {

    private static ProfileDetails INSTANCE;
    private Context mContext;
    private String user_id = "",
            email = "",
            photo_url = "",
            phone_number = "",
            display_name = "",
            EtherToken = "";
    private Float RewardedEther = 0.000000000000f;

    private ProfileDetails(Context context) {
        this.mContext = context;
    }

    public static ProfileDetails getInstance() {
        return INSTANCE;
    }

    public static void updateInstance(ProfileDetails profileDetails){
        INSTANCE = profileDetails;
    }

    public static synchronized void initialize(Context context) {

        INSTANCE = new ProfileDetails(context);

//        SharedPreferences sharedPreferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE);
//        String cachedProfileDetails = sharedPreferences.getString("ProfileDetails", "");
//
//        if (INSTANCE == null) {
//            if (cachedProfileDetails.equalsIgnoreCase("")) {
//                INSTANCE = new ProfileDetails(context);
//            } else {
//                INSTANCE = new Gson().fromJson(cachedProfileDetails, ProfileDetails.class);
//            }
//        }
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEtherToken() {
        return EtherToken;
    }

    public void setEtherToken(String etherToken) {
        EtherToken = etherToken;
    }

    public Float getRewardedEther() {
        return RewardedEther;
    }

    public void setRewardedEther(Float rewardedEther) {
        RewardedEther = rewardedEther;
    }
}
