package com.expelabs.tips.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.expelabs.tips.R;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.util.OtherUtils;
import com.flurry.android.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 03.08.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class BannerActivity extends Activity implements FlurryAdListener {

    FrameLayout mBanner;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBanner = new FrameLayout(this);
        setContentView(mBanner);
        FlurryAds.setAdListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, DailyTipsApp.FLURRY_KEY);
        FlurryAds.fetchAd(this, "TIPS", mBanner, FlurryAdSize.FULLSCREEN);
        Map<String,String>cookies = new HashMap<String, String>();
        cookies.put("user_id", OtherUtils.getImei()+"");
        cookies.put("reward", getIntent().getStringExtra("reward")+"");
        FlurryAds.setUserCookies(cookies);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAds.removeAd(this, "TIPS", mBanner);
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean shouldDisplayAd(String s, FlurryAdType flurryAdType) {
        return true;
    }

    @Override
    public void onAdClosed(String s) {
        finish();
    }

    @Override
    public void onApplicationExit(String s) {
        finish();
    }

    @Override
    public void onRenderFailed(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void spaceDidReceiveAd(String adSpace) {
        FlurryAds.displayAd(this, adSpace, mBanner);
    }

    @Override
    public void spaceDidFailToReceiveAd(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdClicked(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onAdOpened(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onVideoCompleted(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
