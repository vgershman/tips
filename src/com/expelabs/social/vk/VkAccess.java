package com.expelabs.social.vk;

import android.widget.Toast;
import com.facebook.Response;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 24.05.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class VkAccess {

	private final static String VK_METHOD_BASE = "https://api.vk.com/method/";

	public static void post(final String content, final String link,final File photo, final String uid, final String token, final RequestCallback requestCallback) {

		getUploadSever(uid, token, new RequestCallback() {
			@Override
			public void onSuccess(JSONObject response) {
				if (response != null) {
					JSONObject responseObj = response.optJSONObject("response");
					String uploadUrl = null;
					if(responseObj!=null){
						uploadUrl = responseObj.optString("upload_url");
					}
					if (uploadUrl != null) {
						uploadPhoto(uploadUrl, photo, new RequestCallback() {
							@Override
							public void onSuccess(JSONObject response) {
								if (response != null) {
									String server = response.optString("server");
									String photo = response.optString("photo");
									String hash = response.optString("hash");
									if (server != null && photo != null && hash != null) {
										saveWallPhoto(server, photo, hash, uid, token, new RequestCallback() {
											@Override
											public void onSuccess(JSONObject response) {
												if(response!=null){
													String id = response.optString("id");
													if(id!=null && !id.isEmpty()){
														String attachments = id + "," + link;
														final String requestUrl = VK_METHOD_BASE + "wall.post?uid=<uid>&access_token=<ac>&message=<message>&attachments=<at>"
																.replace("<uid>", uid).replace("<ac>", token).replace("<message>", URLEncoder.encode(content)).replace("<at>",attachments);
														new RequestTask(false,requestUrl,null,requestCallback).execute();
													}else {
														String attachments = link;
														final String requestUrl = VK_METHOD_BASE + "wall.post?uid=<uid>&access_token=<ac>&message=<message>&attachments=<at>"
																.replace("<uid>", uid).replace("<ac>", token).replace("<message>", URLEncoder.encode(content)).replace("<at>",attachments);
														new RequestTask(false,requestUrl,null,requestCallback).execute();
													}
												}else {
													requestCallback.onFailure();
												}
											}

											@Override
											public void onFailure() {
												requestCallback.onFailure();
											}
										});
									} else {
										requestCallback.onFailure();
									}
								} else {
									requestCallback.onFailure();
								}
							}

							@Override
							public void onFailure() {
								requestCallback.onFailure();
							}
						});
					} else {
						requestCallback.onFailure();
					}
				} else {
					requestCallback.onFailure();
				}
			}

			@Override
			public void onFailure() {
				requestCallback.onFailure();
			}
		});
	}

	public static void getUploadSever(String uid, String token, final RequestCallback requestCallback) {
		String requestUploadServer = VK_METHOD_BASE + "photos.getWallUploadServer?uid=<uid>&access_token=<ac>"
				.replace("<uid>", uid).replace("<ac>", token);
		new RequestTask(false, requestUploadServer, null, requestCallback).execute();
	}

	public static void uploadPhoto(String uploadUrl, File file, RequestCallback requestCallback) {
		new RequestTask(true, uploadUrl, file, requestCallback).execute();
	}

	public static void saveWallPhoto(String server, String photo, String hash, String uid, String token, RequestCallback callback) {
		String requestUrl = VK_METHOD_BASE + "photos.saveWallPhoto?uid=<uid>&access_token=<ac>&server=<server>&photo=<photo>&hash=<hash>"
				.replace("<uid>", uid).replace("<ac>", token).replace("<server>", server).replace("<photo>", photo).replace("<hash>", hash);
		new RequestTask(false, requestUrl, null, callback).execute();
	}
}
