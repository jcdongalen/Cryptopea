package com.github.jc.cryptopea.Activities;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jc.cryptopea.BuildConfig;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.AdvertisementFactory;
import com.github.jc.cryptopea.Utils.SharedPreferencesFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    private static final String TestDeviceID = AdRequest.DEVICE_ID_EMULATOR;

    private int mySatoshis = 0;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private Button btnEarnOption1, btnEarnOption2, btnEarnOption3;
    private TextView tvEarnings;

    private SharedPreferencesFactory sharedPreferencesFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesFactory = new SharedPreferencesFactory(this);

        tvEarnings = findViewById(R.id.tvTotalEarnings);

        btnEarnOption1 = findViewById(R.id.btnEarnOption1);
        btnEarnOption2 = findViewById(R.id.btnEarnOption2);
        btnEarnOption3 = findViewById(R.id.btnEarnOption3);

        btnEarnOption1.setOnClickListener(this);
        btnEarnOption2.setOnClickListener(this);
        btnEarnOption3.setOnClickListener(this);

        //Banner Ad Area
        AdView mBannerAd = findViewById(R.id.adView);
        mBannerAd.loadAd(requestAd());
        viewMyCoins();

        //Interstitial Ad Area
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3703727949107398/6098095956");
        mInterstitialAd.loadAd(requestAd());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mySatoshis += 15;
                viewMyCoins();
                mInterstitialAd.loadAd(requestAd());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                btnEarnOption2.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                btnEarnOption2.setEnabled(true);
                btnEarnOption2.setText("Retry");
            }
        });

        //RewardedVideo Ad Area
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEarnOption1:
                mySatoshis += 10;
                saveMyCoins();
                viewMyCoins();
                break;
            case R.id.btnEarnOption2:
                if (btnEarnOption2.getText().toString().equalsIgnoreCase("Retry")) {
                    mInterstitialAd.loadAd(requestAd());
                    btnEarnOption2.setEnabled(false);
                } else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        btnEarnOption2.setText("Retry");
                    }
                }
                break;
            case R.id.btnEarnOption3:
                loadRewardedVideoAd();
                break;
        }
    }

    private AdRequest requestAd() {
        return BuildConfig.ENVIRONMENT.equalsIgnoreCase("test") ? new AdRequest.Builder().addTestDevice(TestDeviceID).build() : new AdRequest.Builder().build();
    }

    private void loadRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            mRewardedVideoAd.loadAd("ca-app-pub-3703727949107398/2821567059", requestAd());
        }
    }

    private void viewMyCoins() {
        if (sharedPreferencesFactory.getPreferenceByName("Account") != null) {
            SharedPreferences sharedPreferences = sharedPreferencesFactory.getPreferenceByName("Account");
            mySatoshis = sharedPreferences.getInt("mySatoshis", 0);
        }

        tvEarnings.setText("Total Earnings:\n" + mySatoshis + " Satohis");
    }

    private void saveMyCoins() {
        if (sharedPreferencesFactory.getPreferenceByName("Account") != null) {
            SharedPreferences sharedPreferences = sharedPreferencesFactory.getPreferenceByName("Account");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("mySatoshis", mySatoshis);
            editor.commit();
        }
    }

    //TODO: Handle RewardedAdListener
    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        btnEarnOption3.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        viewMyCoins();
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(this, "You received " + rewardItem.getAmount() + " " + rewardItem.getType() + ".", Toast.LENGTH_SHORT).show();
        mySatoshis += rewardItem.getAmount();
        saveMyCoins();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
        btnEarnOption3.setText("Retry");
        btnEarnOption3.setEnabled(true);
    }
}
