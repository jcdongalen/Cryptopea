package com.github.jc.cryptopea.Fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class Reward extends Fragment implements View.OnClickListener {

    private View view;

    private static final String TestDeviceID = AdRequest.DEVICE_ID_EMULATOR;
    private static final int Earning1_MAXTIME = 1000 * 60 * 2;
    private static final int Earning2_MAXTIME = 1000 * 60 * 5;
    private static final int COUNTDOWN_INTERVAL = 1000;
    private static final int EtheriumRefresh_MAXTIME = 1000 * 60 * 10;

    private float myEther = 0.000000000000f;
    private float ethPrice = 0.000000000000f;
    private float ethInterstitialReward = 0.000000000000f;
    private float ethRewardedVideoReward = 0.000000000000f;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean isAbleToShowAd = true;

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
            enableButton(btnEarnOption1, String.format(getResources().getString(R.string.earning_ether), ethInterstitialReward));
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
            enableButton(btnEarnOption2, String.format(getResources().getString(R.string.earning_ether), ethRewardedVideoReward));
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

    public Reward() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reward, container, false);

        mRinger = MediaPlayer.create(getActivity(), R.raw.coin_drop);
        sharedPreferencesFactory = new SharedPreferencesFactory(getActivity());
        mConstants = new Constants(getActivity());
        mRequestQueue = Volley.newRequestQueue(getActivity());

        tvEarnings = view.findViewById(R.id.tvTotalEarnings);
        tvEtherPrice = view.findViewById(R.id.tvEtherPrice);

        btnEarnOption1 = view.findViewById(R.id.btnEarnOption1);
        btnEarnOption2 = view.findViewById(R.id.btnEarnOption2);
        btnEarnOption3 = view.findViewById(R.id.btnEarnOption3);

        btnEarnOption1.setOnClickListener(this);
        btnEarnOption2.setOnClickListener(this);
        btnEarnOption3.setOnClickListener(this);

        //Request for updated price of Ethereum
        ethPriceRequest();
        EtheriumPriceRequestTimer.start();

        //Banner Ad Area
        AdView mBannerAd = view.findViewById(R.id.adView);
        mBannerAd.loadAd(requestAd());
        viewMyCoins();

        //Interstitial Ad Area
        mInterstitialAd = new InterstitialAd(getActivity().getApplicationContext());
        mInterstitialAd.setAdUnitId(BuildConfig.INTERSTITIAL_AD_ID);
        mInterstitialAd.loadAd(requestAd());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                String message = "You have received " + String.format(Locale.getDefault(), "%.12f", ethInterstitialReward) + " ether.";
                mConstants.showShortToast(message);
                Earning1CountdownTimer.start();
                myEther += ethInterstitialReward;
                mRinger.start();
                viewMyCoins();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (isAbleToShowAd)
                    enableButton(btnEarnOption1, String.format(getResources().getString(R.string.earning_ether), ethInterstitialReward));
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if(isAbleToShowAd) {
                    String message = "Interstitial Ad failed to load. Error Code: " + i;
                    mConstants.showShortToast(message);
                }
            }
        });
        disableButton(btnEarnOption1, "Loading...");

        //RewardedVideo Ad Area
        disableButton(btnEarnOption2, "Loading...");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.wtf("RewardedVideoAd", "onRewardedVideoAdLoaded Method");
                if (isAbleToShowAd)
                    enableButton(btnEarnOption2, String.format(getResources().getString(R.string.earning_ether), ethRewardedVideoReward));
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
                String message = "You received " + String.format(Locale.getDefault(), "%.12f", ethRewardedVideoReward) + " " + rewardItem.getType() + ".";
                mConstants.showShortToast(message);
                myEther += ethRewardedVideoReward;
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
                if (isAbleToShowAd)
                    enableButton(btnEarnOption2, "Retry");
            }
        });
        mRewardedVideoAd.loadAd(BuildConfig.REWARDEDVIDEO_AD_ID, requestAd());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        this.isAbleToShowAd = true;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        this.isAbleToShowAd = false;
        super.onDetach();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(getActivity());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(getActivity());
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
            myEther = sharedPreferences.getFloat("myEther", 0.000000000000f);
        }

        tvEarnings.setText("Total Earnings:\n" + String.format("%.12f ", myEther) + " Ethers.");
    }

    private void saveMyCoins() {
        SharedPreferences sharedPreferences = sharedPreferencesFactory.getPreferenceByName("Account");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("myEther", myEther);
        editor.apply();
    }

    private void disableButton(Button btn, String text) {
        btn.setClickable(false);
        btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btn.setTextColor(getResources().getColor(R.color.global_text_color));
        btn.setShadowLayer(2, -1, -1, R.color.global_text_shadow_white);
        btn.setText(text);
    }

    private void enableButton(Button btn, String text) {
        btn.setClickable(true);
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btn.setTextColor(getResources().getColor(android.R.color.white));
        btn.setShadowLayer(2, -1, -1, R.color.global_text_shadow);
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
                            ethPrice = Float.parseFloat(EthPriceinPHP);
                            String finalEthPrice = "Ethereum: Php " + mConstants.currencyFormatter(ethPrice);
                            tvEtherPrice.setText(finalEthPrice);

                            ethInterstitialReward = 0.01f / ethPrice;
                            ethRewardedVideoReward = 0.02f / ethPrice;

                            if (btnEarnOption1.isClickable()) {
                                btnEarnOption1.setText(String.format(Locale.getDefault(), "+ %.12f", ethInterstitialReward));
                            }

                            if (btnEarnOption2.isClickable()) {
                                btnEarnOption2.setText(String.format(Locale.getDefault(), "+ %.12f", ethRewardedVideoReward));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mConstants.showLongToast(error.getMessage());
                    }
                });
        mRequestQueue.add(ethPriceRequest);
    }
}
