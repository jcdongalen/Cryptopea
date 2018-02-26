package com.github.jc.cryptopea.Activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.github.jc.cryptopea.BuildConfig;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.github.jc.cryptopea.Utils.SharedPreferencesFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TestDeviceID = AdRequest.DEVICE_ID_EMULATOR;
    private static final int Earning1_MAXTIME = 1000 * 60 * 2;
    private static final int Earning2_MAXTIME = 1000 * 60 * 5;
    private static final int COUNTDOWN_INTERVAL = 1000;
    private static final int EtheriumRefresh_MAXTIME = 1000 * 60 * 10;

    private int mySatoshis = 0;
    private double ethPrice = 0.0, ethInterstitialReward = 0.0, ethRewardedVideoReward = 0.0;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private Button btnEarnOption1, btnEarnOption2, btnEarnOption3;
    private TextView tvEarnings, tvEtherPrice;

    private MediaPlayer mRinger;

    private SharedPreferencesFactory sharedPreferencesFactory;
    private Constants mConstants;
    private RequestQueue mRequestQueue;

    private CountDownTimer Earning1CountdownTimer = new CountDownTimer(Earning1_MAXTIME, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            disableButton(btnEarnOption1, getFormattedTimer(millisUntilFinished));

            if (btnEarnOption1.getText().toString().contains("00:05")) {
                mInterstitialAd.loadAd(requestAd());
            }
        }

        @Override
        public void onFinish() {
            enableButton(btnEarnOption1, getResources().getString(R.string.earning_two));
        }
    };

    private CountDownTimer Earning2CountdownTimer = new CountDownTimer(Earning2_MAXTIME, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            disableButton(btnEarnOption2, getFormattedTimer(millisUntilFinished));

            if (btnEarnOption2.getText().toString().contains("00:05")) {
                mRewardedVideoAd.loadAd(BuildConfig.REWARDEDVIDEO_AD_ID, requestAd());
            }
        }

        @Override
        public void onFinish() {
            enableButton(btnEarnOption2, getResources().getString(R.string.earning_three));
        }
    };

    private CountDownTimer EtheriumPriceRequestTimer = new CountDownTimer(EtheriumRefresh_MAXTIME, COUNTDOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            EtheriumPriceRequestTimer.start();
            ethPriceRequest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRinger = MediaPlayer.create(this, R.raw.coin_drop);
        sharedPreferencesFactory = new SharedPreferencesFactory(this);
        mConstants = new Constants(this);
        mRequestQueue = Volley.newRequestQueue(this);

        tvEarnings = findViewById(R.id.tvTotalEarnings);
        tvEtherPrice = findViewById(R.id.tvEtherPrice);

        btnEarnOption1 = findViewById(R.id.btnEarnOption1);
        btnEarnOption2 = findViewById(R.id.btnEarnOption2);
        btnEarnOption3 = findViewById(R.id.btnEarnOption3);

        btnEarnOption1.setOnClickListener(this);
        btnEarnOption2.setOnClickListener(this);
        btnEarnOption3.setOnClickListener(this);

        //Request for updated price of Ethereum
        ethPriceRequest();
        EtheriumPriceRequestTimer.start();

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
                int rewardedSatoshi = (15 + mConstants.getRandomInt(5, 0));
                Earning1CountdownTimer.start();
                mySatoshis += rewardedSatoshi;
                mRinger.start();
                Toast.makeText(MainActivity.this, "You have received " + rewardedSatoshi + " satoshi's.", Toast.LENGTH_SHORT).show();
                viewMyCoins();
            }

            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivity.this, "Interstitial Ad is now loaded.", Toast.LENGTH_SHORT).show();
                super.onAdLoaded();
                enableButton(btnEarnOption1, getResources().getString(R.string.earning_two));
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(MainActivity.this, "Interstitial Ad failed to load. Error Code: ", Toast.LENGTH_SHORT).show();
                super.onAdFailedToLoad(i);
            }
        });
        disableButton(btnEarnOption1, "Loading...");

        //RewardedVideo Ad Area
        disableButton(btnEarnOption2, "Loading...");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdLoaded Method");
                enableButton(btnEarnOption2, getResources().getString(R.string.earning_three));
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdOpened Method");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoStarted Method");
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdClosed Method");
                Earning2CountdownTimer.start();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.wtf("RewardedVideoAd", "onRewarded Method");
                int rewardedSatoshi = rewardItem.getAmount() + mConstants.getRandomInt(5, 0);
                Toast.makeText(MainActivity.this, "You received " + rewardedSatoshi + " " + rewardItem.getType() + ".", Toast.LENGTH_SHORT).show();
                mySatoshis += rewardedSatoshi;
                saveMyCoins();
                mRinger.start();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdLeftApplication Method");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdFailedToLoad ERROR: " + i);
                enableButton(btnEarnOption2, "Retry");
            }
        });
        mRewardedVideoAd.loadAd(BuildConfig.REWARDEDVIDEO_AD_ID, requestAd());
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
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case R.id.btnEarnOption2:
                if (btnEarnOption3.getText().toString().equalsIgnoreCase("Retry")) {
                    mRewardedVideoAd.loadAd(BuildConfig.REWARDEDVIDEO_AD_ID, requestAd());
                    disableButton(btnEarnOption2, "Loading...");
                } else {
                    loadRewardedVideoAd();
                }
                break;
            case R.id.btnEarnOption3:
                break;
        }
    }

    private String getFormattedTimer(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    private AdRequest requestAd() {
        return BuildConfig.ENVIRONMENT.equalsIgnoreCase("test") ? new AdRequest.Builder().addTestDevice(TestDeviceID).build() : new AdRequest.Builder().build();
    }

    private void loadRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
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

    private void disableButton(Button btn, String text) {
        btn.setClickable(false);
        btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btn.setText(text);
    }

    private void enableButton(Button btn, String text) {
        btn.setClickable(true);
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn.setText(text);
    }

    private void ethPriceRequest() {
        final JsonObjectRequest ethPriceRequest = new JsonObjectRequest(Request.Method.GET,
                getResources().getString(R.string.coinbase_getcurrencies_api_url),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject rates = data.getJSONObject("rates");
                            String EthPriceinPHP = rates.getString("PHP");
                            ethPrice = Double.parseDouble(EthPriceinPHP);
                            String finalEthPrice = "Ethereum: Php " + mConstants.currencyFormatter(ethPrice);
                            tvEtherPrice.setText(finalEthPrice);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueue.add(ethPriceRequest);
    }
}
