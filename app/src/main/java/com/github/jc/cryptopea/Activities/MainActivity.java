package com.github.jc.cryptopea.Activities;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jc.cryptopea.BuildConfig;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.SharedPreferencesFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    private static final String TestDeviceID = AdRequest.DEVICE_ID_EMULATOR;

    private int mySatoshis = 0;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private Button btnEarnOption1, btnEarnOption2, btnEarnOption3;
    private TextView tvEarnings;

    private SharedPreferencesFactory sharedPreferencesFactory;

    private CountDownTimer Earning1CountdownTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnEarnOption1.setClickable(false);
            btnEarnOption1.setText(String.valueOf((millisUntilFinished / 1000) + 1));
            btnEarnOption1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public void onFinish() {
            btnEarnOption1.setClickable(true);
            btnEarnOption1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnEarnOption1.setText("Earn 10 Satoshi's");
        }
    };

    private CountDownTimer Earning2CountdownTimer = new CountDownTimer(15000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnEarnOption2.setClickable(false);
            btnEarnOption2.setText(String.valueOf((millisUntilFinished / 1000)));
            btnEarnOption2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public void onFinish() {
            btnEarnOption2.setClickable(true);
            btnEarnOption2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnEarnOption2.setText("Earn 15 Satoshi's");
        }
    };

    private CountDownTimer Earning3CountdownTimer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnEarnOption3.setClickable(false);
            btnEarnOption3.setText(String.valueOf((millisUntilFinished / 1000)));
            btnEarnOption3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        @Override
        public void onFinish() {
            btnEarnOption3.setClickable(true);
            btnEarnOption3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnEarnOption3.setText("Earn 15 Satoshi's");
        }
    };

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
        mInterstitialAd.setAdUnitId(BuildConfig.INTERSTITIAL_AD_ID);
        mInterstitialAd.loadAd(requestAd());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Earning2CountdownTimer.start();
                mySatoshis += 15;
                viewMyCoins();
                mInterstitialAd.loadAd(requestAd());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
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
                Earning1CountdownTimer.start();
                mySatoshis += 10;
                saveMyCoins();
                viewMyCoins();
                break;
            case R.id.btnEarnOption2:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
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
            mRewardedVideoAd.loadAd(BuildConfig.REWARDEDVIDEO_AD_ID, requestAd());
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
        SharedPreferences sharedPreferences = sharedPreferencesFactory.getPreferenceByName("Account");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mySatoshis", mySatoshis);
        editor.apply();
    }

    //TODO: Handle RewardedAdListener
    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
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
        Earning3CountdownTimer.start();
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
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad: " + i, Toast.LENGTH_SHORT).show();
    }
}
