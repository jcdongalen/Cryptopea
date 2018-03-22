package com.github.jc.cryptopea.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by Owner on 3/19/2018.
 */

public class Loading {

    public static Dialog getLoadingDialog(Context mContext){
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout layout = new FrameLayout(mContext);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        layout.setLayoutParams(params);

        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setBackgroundColor(Color.TRANSPARENT);
        layout.addView(progressBar);

        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
