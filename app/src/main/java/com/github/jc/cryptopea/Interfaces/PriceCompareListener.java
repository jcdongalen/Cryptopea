package com.github.jc.cryptopea.Interfaces;

/**
 * Created by Owner on 2/20/2018.
 */

public interface PriceCompareListener {

    void onLowerPriceFound();

    void onLowerPriceCancel();

    void showResultOverlay();
}
