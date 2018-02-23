package com.github.jc.cryptopea.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

import com.github.jc.cryptopea.Interfaces.PriceCompareListener;

/**
 * Created by Owner on 2/20/2018.
 */

public class AmazonAccessibilityService extends AccessibilityService implements PriceCompareListener {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = getServiceInfo();

    }

    @Override
    public void onLowerPriceFound() {

    }

    @Override
    public void onLowerPriceCancel() {

    }

    @Override
    public void showResultOverlay() {

    }
}
