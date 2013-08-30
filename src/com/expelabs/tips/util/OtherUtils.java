package com.expelabs.tips.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.expelabs.tips.app.DailyTipsApp;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 21.08.13
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class OtherUtils {
    public static String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) DailyTipsApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}
