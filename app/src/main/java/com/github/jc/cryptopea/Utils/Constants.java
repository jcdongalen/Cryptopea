package com.github.jc.cryptopea.Utils;

import android.content.Context;
import android.provider.Telephony;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

/**
 * Created by Owner on 2/20/2018.
 */

public class Constants {

    private Context mContext;

    public Constants(Context mContext){
        this.mContext = mContext;
    }

    public String getSMSPackageName(){
        return Telephony.Sms.getDefaultSmsPackage(mContext);
    }

    public int getRandomInt(int max, int min) throws IllegalArgumentException{
        return new Random().nextInt((max - min) + 1) + min;
    }

    public String currencyFormatter(float val){
        return String.format(Locale.getDefault(), "%,.2f", val);
    }

    public void showShortToast(String Message){
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String Message){
        Toast.makeText(mContext, Message, Toast.LENGTH_LONG).show();
    }
}
