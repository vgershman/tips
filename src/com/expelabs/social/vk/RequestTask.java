package com.expelabs.social.vk;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class RequestTask extends AsyncTask<Object,Void,Void> {

    private String url;
    private RequestCallback callback;

    public RequestTask(String url, RequestCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        StringBuilder builder=new StringBuilder();
        JSONArray jsonArray=null;
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONObject jsonObject1=new JSONObject(builder.toString());
                jsonArray = jsonObject1.getJSONArray("response");
            }else {return null;}


        }catch (JSONException e){
            e.printStackTrace();return null;
        }
        catch (IOException e) {
            e.printStackTrace();return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.onSuccess();
    }
}
