package com.github.jc.cryptopea.Interfaces;

import android.view.View;

/**
 * Created by Owner on 3/22/2018.
 */

public interface OnRecyclerViewItemClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
