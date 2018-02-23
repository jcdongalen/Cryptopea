package com.github.jc.cryptopea.Utils;

import android.content.Context;
import android.provider.Telephony;

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

}
