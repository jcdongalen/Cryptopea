package com.github.jc.cryptopea.Utils;

import android.content.Context;
import android.widget.Toast;

import com.github.jc.cryptopea.BuildConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by Owner on 2/23/2018.
 */

public class AdvertisementFactory implements RewardedVideoAdListener {

    private static final String TestDeviceID = "1965D2779D0B5F70D69E8091E3F4E9F2";
    private static final String RewardedVideo_AD_ID = "ca-app-pub-3703727949107398/2821567059";

    private Context mContext;

    public AdvertisementFactory(Context context) {
        this.mContext = context;
    }

    public AdRequest requestAd() {
        return BuildConfig.ENVIRONMENT.equalsIgnoreCase("test") ? new AdRequest.Builder().addTestDevice(TestDeviceID).build() : new AdRequest.Builder().build();
    }

    public void shoBannerAd(AdView mBannerAd) {
        AdRequest bannerAdRequest = requestAd();
        mBannerAd.loadAd(bannerAdRequest);
    }

    public InterstitialAd initializeInterstitialAd(String InterstitialAdID){
        InterstitialAd mInterstitial = new InterstitialAd(mContext);
        mInterstitial.setAdUnitId(InterstitialAdID);
        return mInterstitial;
    }

    public void requestInterstitialAd(InterstitialAd mInterstitialAd){
        AdRequest adRequest = requestAd();
        mInterstitialAd.loadAd(adRequest);
    }

    public void requestRewardedVideoAd(RewardedVideoAd mRewardedVideoAd){
//        AdRequest adRequest = requestAd();
//        mRewardedVideoAd.loadAd(adRequest);
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(mContext, "onRewarded! currency: " + rewardItem.getType() + "  amount: " + rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}
