package com.expelabs.social.vk;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class RequestTask extends AsyncTask<Object, Void, Void> {

	private boolean error;
	private boolean isPost;
	private String url;
	private RequestCallback callback;
	private JSONObject jsonResponse = null;
	private File file;

	public RequestTask(boolean isPost, String url, File file, RequestCallback callback) {
		this.url = url;
		this.isPost = isPost;
		this.callback = callback;
		this.file = file;
	}

	@Override
	protected Void doInBackground(Object... params1) {
		HttpClient client = new DefaultHttpClient();
		HttpUriRequest httpRequest = isPost ? new HttpPost(url) : new HttpGet(url);
		if (isPost) {
			if (file != null) {
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				reqEntity.addPart("photo", new FileBody(file));
				((HttpPost) httpRequest).setEntity(reqEntity);
			}
		}
		StringBuilder builder = new StringBuilder();
		try {
			HttpResponse response = client.execute(httpRequest);
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
				jsonResponse = new JSONObject(builder.toString());
			} else {
				error = true;
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			error = true;
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			error = true;
			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		if (error) {
			callback.onFailure();
		} else {
			callback.onSuccess(jsonResponse);
		}
	}
}
