package com.github.jc.cryptopea.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yasuo on 2/23/2018.
 */

public class SharedPreferencesFactory {

    private Context mContext;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SharedPreferencesFactory(Context context){
        this.mContext = context;
    }

    public SharedPreferences getPreferenceByName(String PreferenceName){
        return mContext.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
    }
}
