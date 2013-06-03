package com.expelabs.tips.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.expelabs.social.view.OAuthDialog;
import com.expelabs.social.vk.RequestCallback;
import com.expelabs.social.vk.VkAccess;
import com.expelabs.social.webclient.AuthListener;
import com.expelabs.social.webclient.VkAuthClient;
import com.expelabs.tips.R;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.delegate.NavigationDelegate;
import com.expelabs.tips.dto.Share;
import com.expelabs.tips.dto.Tip;
import com.expelabs.tips.util.ImageUtils;
import com.facebook.*;
import com.facebook.android.Facebook;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 15.05.13
 * Time: 0:20
 * To change this template use File | Settings | File Templates.
 */
public class TipFragment extends Fragment {

	private Tip tip;
	private ImageView socialButton;
	private static NavigationDelegate navigationDelegate;
	private OAuthDialog oAuthDialog;

	public static TipFragment newInstance(Tip tip, NavigationDelegate navigationDelegateParam) {
		navigationDelegate = navigationDelegateParam;
		TipFragment pageFragment = new TipFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable("tip", tip);
		pageFragment.setArguments(arguments);
		return pageFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tip = (Tip) getArguments().get("tip");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tip_item, null, false);
		((TextView) view.findViewById(R.id.tip_text)).setText(tip.getText());
		((TextView) view.findViewById(R.id.tip_text_ital)).setText(tip.getTextItalic());
		view.findViewById(R.id.left_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				navigationDelegate.onLeft();
			}
		});
		view.findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				navigationDelegate.onRight();
			}
		});
		socialButton = (ImageView) view.findViewById(R.id.social_button);
		Locale current = getResources().getConfiguration().locale;
		int default_share = Share.FACEBOOK;
		if (current.getCountry().equals("RU")) {
			default_share = Share.VK;
		}
		final int share_id = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt("share", default_share);
		switch (share_id) {
			case Share.VK:
				socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_vk));
				break;
			case Share.FACEBOOK:
				socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_fb));
				break;
			case Share.TWITTER:
				socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_twit));
				break;
			case Share.EMAIL:
				socialButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_email));
				break;
		}
		socialButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (share_id) {
					case Share.VK:
						shareVK();
						break;
					case Share.FACEBOOK:
						shareFB();
						break;
					case Share.TWITTER:
						shareTwitter();
						break;
					case Share.EMAIL:
						shareEmail();
						break;
				}
			}
		});
		try {
			InputStream is = getActivity().getAssets().open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg");
			((ImageView) view.findViewById(R.id.tip_image)).setImageBitmap(BitmapFactory.decodeStream(is));
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return view;
	}

	private void shareEmail() {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.tip_tag));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, tip.getText() + "\n" + tip.getTextItalic());
		try {
			emailIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(ImageUtils.writeImageToFile(BitmapFactory.decodeStream(getActivity().getAssets()
							.open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg")), "image"))));
		} catch (IOException e) {
			return;
		}
		final PackageManager pm = getActivity().getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
		ResolveInfo best = null;
		for (final ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") ||
					info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
		if (best != null)
			emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		startActivity(emailIntent);
	}

	private void shareTwitter() {

		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
				targetedShare.setType("text/plain");
				if (info.activityInfo.packageName.toLowerCase().contains("twi") ||
						info.activityInfo.name.toLowerCase().contains("twi")) {
					String text = getString(R.string.tip_tag).replace("#","@") + " "+ tip.getText();
					if(text.length() > 118){
						text = text.substring(0,118);
					}
					targetedShare.putExtra(Intent.EXTRA_TEXT, text);
					try {
					targetedShare.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(ImageUtils.writeImageToFile(BitmapFactory.decodeStream(getActivity().getAssets()
									.open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg")), "image"))));
				} catch (IOException e) {
					return;
				}

					targetedShare.setPackage(info.activityInfo.packageName);
					targetedShareIntents.add(targetedShare);
				}
			}
		}
		if (targetedShareIntents.size() == 0) {
			return;
		}
		Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), getString(R.string.share_select_ac));
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		startActivity(chooserIntent);
	}

	private void shareFB() {
		Session session = Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
			}
		});
		if (session != null) {
			Bundle postParams = new Bundle();
			postParams.putString("name", getString(R.string.tip_tag) + " " + tip.getText());
			postParams.putString("caption", tip.getTextItalic());
			postParams.putString("link", getString(R.string.market));
			postParams.putString("picture", DailyTipsApp.HOSTING_BASE_URL + "/" + tip.getCategoryName().toLowerCase() + "/" + tip.getId() + ".jpg");
			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					try {
						JSONObject graphResponse = response
								.getGraphObject()
								.getInnerJSONObject();
						String postId = null;
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							Log.i("ES",
									"JSON error " + e.getMessage());
						}
						FacebookRequestError error = response.getError();
						if (error != null) {
							Toast.makeText(getActivity(), getString(R.string.share_error), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getActivity(), getString(R.string.share_success), Toast.LENGTH_LONG).show();
						}
					} catch (NullPointerException ex) {
						Toast.makeText(getActivity(), getString(R.string.share_error), Toast.LENGTH_LONG).show();
					}
				}
			};
			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	private void shareVK() {
		String uid = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, Context.MODE_PRIVATE).getString("VkUserId", "");
		String accessToken = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, Context.MODE_PRIVATE).getString("VkAccessToken", "");
		if (!uid.equals("") && !accessToken.equals("")) {
			File f = null;
			try {
				f = new File(ImageUtils.writeImageToFile(BitmapFactory.decodeStream(getActivity().getAssets()
						.open("tipsImages/" + tip.getCategoryName() + "/" + tip.getId() + ".jpg")), "image"));
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			//File file = new File(URI.create(DailyTipsApp.HOSTING_BASE_URL + tip.getCategoryName().toLowerCase() + "/" + tip.getId() + ".jpg"));
			VkAccess.post(getString(R.string.tip_tag) + '\n' + tip.getText() + '\n' + tip.getTextItalic(),
					getString(R.string.market), f, uid, accessToken, new RequestCallback() {

				@Override
				public void onSuccess(JSONObject response) {
					Toast.makeText(getActivity(), getString(R.string.share_success), Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure() {
					Toast.makeText(getActivity(), getString(R.string.share_error), Toast.LENGTH_LONG).show();
				}
			});
		} else {
			VkAuthClient vkAuthClient = new VkAuthClient(DailyTipsApp.VK_APP_ID, DailyTipsApp.VK_SCOPE, new AuthListener() {
				@Override
				public void onSuccess(String url) {
					oAuthDialog.dismiss();
					String[] params = url.substring(VkAuthClient.VK_REDIRECT_URI.length() + 1).split("&");
					SharedPreferences.Editor editor = getActivity().getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
					editor.putString("VkAccessToken", params[0].split("=")[1]);
					editor.putString("VkExpiresIn", params[1].split("=")[1]);
					editor.putString("VkUserId", params[2].split("=")[1]);
					editor.putLong("VkAccessTime", System.currentTimeMillis());
					editor.commit();
					shareVK();
				}

				@Override
				public void onError() {
					Toast.makeText(getActivity(), getString(R.string.share_error), Toast.LENGTH_LONG).show();
					oAuthDialog.dismiss();
				}
			});
			oAuthDialog = new OAuthDialog(getActivity(), vkAuthClient);
			oAuthDialog.show();
		}
	}

	public Tip getTip() {
		return tip;
	}
}
