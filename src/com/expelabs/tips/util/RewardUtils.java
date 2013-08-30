package com.expelabs.tips.util;

import android.content.Context;
import android.os.AsyncTask;
import com.expelabs.tips.app.DailyTipsApp;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 11.08.13
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
public class RewardUtils {

    private static final String BASE_URL = "https://vgershman.cloudant.com/daily-tips-rewards/";

    public static void getRewards(Context context, final Callback callback) {
        final String udid = OtherUtils.getImei();
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(BASE_URL + udid));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader
                            (new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    String NL = System.getProperty("line.separator");
                    while ((line = in.readLine()) != null) {
                        sb.append(line + NL);
                    }
                    in.close();
                    String page = sb.toString();
                    JSONObject jsonObject = new JSONObject(page);
                    return jsonObject;
                } catch (Exception ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                boolean cooking = false;
                boolean home = false;
                boolean life = false;
                boolean work = false;
                if (!jsonObject.optString("cooking").equals("")) {
                    cooking = true;
                }
                if (!jsonObject.optString("home").equals("")) {
                    home = true;
                }
                if (!jsonObject.optString("life").equals("")) {
                    life = true;
                }
                if (!jsonObject.optString("work").equals("")) {
                    work = true;
                }
                callback.onSuccess(cooking,home,life,work);
            }
        }.execute();
    }

    public interface Callback {
        void onSuccess(boolean cooking, boolean home, boolean life, boolean work);
    }
}
