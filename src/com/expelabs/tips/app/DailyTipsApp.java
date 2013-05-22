package com.expelabs.tips.app;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import com.expelabs.tips.R;
import com.expelabs.tips.util.BillingUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 14.05.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */

public class DailyTipsApp extends Application {

    public static final String PREFERENCES_NAME = "tips_preferences";
    public static final String SKU_TOTAL = "com.expelabs.tips.total";
    public static final String SKU_COOKING = "com.expelabs.tips.cooking";
    public static final String SKU_HOME = "com.expelabs.tips.home";
    public static final String SKU_WORK = "com.expelabs.tips.work";
    public static final String SKU_LIFESTYLE = "com.expelabs.tips.lifestyle";
    private static int scrollngCounter;

    public static final String GOOGLE_PLAY_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwaper77Q1Aj7/bLzkKLimOGL7QZxZNYGZhqgI2gRhqxKsz1MthpUELHCmD6NMpQhvTqGMS5kAMiXOvlOeCYw6E8uGg2ABOXfVy+8Ul3yr0YcQdi6CW0mrDbbPwUk/HtHPz8fw4H/42CMncw5qGckhKsNx027QQGPh4tgP3gyNDkhVL7jUbSWDX6xn/8KpRhdprRjcQxfZ0yDQZpOixYLTYOtT9F/opEng9ORbfTmN3BMtnZIpJqB/dSbcgRUTNVWNQ8ioIZHZTziHeLAL4504KEpx9i6wPdi8TIAH8Qt9Xt/21XoTmU/Jn+X0+T/vha3955ADmvGhasyKNfwI8UfRQIDAQAB";

    private static BillingUtils billingUtils;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        billingUtils = new BillingUtils(getApplicationContext());
        appContext = getApplicationContext();
    }

    public static BillingUtils getBillingUtils() {
        return billingUtils;
    }

    public static Map<String,Boolean> getPurchases(){
        HashMap<String,Boolean>result = new HashMap<String, Boolean>();
        result.put(SKU_TOTAL, appContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getBoolean(SKU_TOTAL, false));
        result.put(SKU_HOME,appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean(SKU_HOME, false));
        result.put(SKU_COOKING,appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean(SKU_COOKING, false));
        result.put(SKU_WORK,appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean(SKU_WORK, false));
        result.put(SKU_LIFESTYLE,appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean(SKU_LIFESTYLE, false));
        return result;
    }

    public static void setContext(Context appContext) {
        DailyTipsApp.appContext = appContext;
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
        billingUtils.dispose();
    }

    public static void incrementScrollCounter(){
        scrollngCounter++;
        if(scrollngCounter % 10 == 0){
            if(!appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean("bought", false)){
                AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
                builder.setTitle(appContext.getString(R.string.more_tips));
                builder.setMessage("Test");
                builder.create().show();
            }
        }
        if(scrollngCounter % 15 ==0){
            if(!appContext.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean("rate", false)){
                AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
                builder.setTitle("Rate this app");
                builder.setMessage("Test");
                builder.create().show();
            }
        }
    }

}

