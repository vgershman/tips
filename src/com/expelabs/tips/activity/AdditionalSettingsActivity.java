package com.expelabs.tips.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.billing.IabHelper;
import com.billing.IabResult;
import com.billing.Purchase;
import com.expelabs.social.view.OAuthDialog;
import com.expelabs.social.webclient.AuthListener;
import com.expelabs.social.webclient.VkAuthClient;
import com.expelabs.tips.R;
import com.expelabs.tips.app.*;
import com.expelabs.tips.delegate.PurchaseDelegate;
import com.expelabs.tips.dto.Share;
import com.expelabs.tips.util.BillingUtils;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 19.05.13
 * Time: 1:36
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalSettingsActivity extends SherlockActivity {

	private ImageView shareVk;
	private ImageView shareFb;
	private ImageView shareTw;
	private ImageView shareEmail;

	private ImageView buyTotal;
	private ImageView buyCooking;
	private ImageView buyHome;
	private ImageView buyWork;
	private ImageView buyLifestyle;
	private OAuthDialog oAuthDialog;
	private UiLifecycleHelper lifecycleHelper;
	private Session.StatusCallback statusCallback;
	private List<String> permissions = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		initActionBar();
		initBuyComponents();
		initShareComponents();
		bindUIbyPurchasese();
		permissions.add("publish_actions");
		statusCallback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (state.equals(SessionState.OPENED)) {
					session.requestNewPublishPermissions(new Session.NewPermissionsRequest(AdditionalSettingsActivity.this, permissions));
				}
			}
		};
		lifecycleHelper = new UiLifecycleHelper(this, statusCallback);
	}

	private void initShareComponents() {
		shareFb = (ImageView) findViewById(R.id.share_facebook);
		shareEmail = (ImageView) findViewById(R.id.share_email);
		shareTw = (ImageView) findViewById(R.id.share_twitter);
		shareVk = (ImageView) findViewById(R.id.share_vk);
		Locale current = getResources().getConfiguration().locale;
		int default_share = Share.FACEBOOK;
		if (current.getCountry().equals("RU")) {
			default_share = Share.VK;
		}
		int selected = getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).getInt("share", default_share);
		selectButtons(selected);
		findViewById(R.id.share_facebook).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit().putInt("share", Share.FACEBOOK).commit();
				Session.openActiveSession(AdditionalSettingsActivity.this, true, statusCallback);
				selectButtons(Share.FACEBOOK);
			}
		});
		findViewById(R.id.share_vk).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VkAuthClient vkAuthClient = new VkAuthClient(DailyTipsApp.VK_APP_ID, DailyTipsApp.VK_SCOPE, new AuthListener() {
					@Override
					public void onSuccess(String url) {
						oAuthDialog.dismiss();
						String[] params = url.substring(VkAuthClient.VK_REDIRECT_URI.length() + 1).split("&");
						SharedPreferences.Editor editor = getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit();
						editor.putString("VkAccessToken", params[0].split("=")[1]);
						editor.putString("VkExpiresIn", params[1].split("=")[1]);
						editor.putString("VkUserId", params[2].split("=")[1]);
						editor.putLong("VkAccessTime", System.currentTimeMillis());
						editor.commit();
					}

					@Override
					public void onError() {
						Toast.makeText(AdditionalSettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
						oAuthDialog.dismiss();
					}
				});
				oAuthDialog = new OAuthDialog(AdditionalSettingsActivity.this, vkAuthClient);
				oAuthDialog.show();
				getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit().putInt("share", Share.VK).commit();
				selectButtons(Share.VK);
			}
		});
		findViewById(R.id.share_twitter).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DailyTipsApp.isAppInstalled("com.twitter.android")) {
					getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit().putInt("share", Share.TWITTER).commit();
					selectButtons(Share.TWITTER);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalSettingsActivity.this);
					builder.setTitle("Twitter App is missing");
					builder.setMessage("Do you want to install?");
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final Intent market = new Intent(Intent.ACTION_VIEW,
									Uri.parse("http://market.android.com/details?id=com.twitter.android"));
							AdditionalSettingsActivity.this.startActivity(market);
						}
					});
					builder.create().show();
				}
			}
		});
		findViewById(R.id.share_email).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit().putInt("share", Share.EMAIL).commit();
				selectButtons(Share.EMAIL);
			}
		});
	}

	private void initActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getString(R.string.additional_settings_title));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initBuyComponents() {
		buyTotal = (ImageView) findViewById(R.id.ia_total);
		buyTotal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyTipsApp.getBillingUtils().buy(DailyTipsApp.SKU_TOTAL, AdditionalSettingsActivity.this, new PurchaseDelegate() {
					@Override
					public void onSuccess() {
						bindUIbyPurchasese();
					}
				});
			}
		});
		buyCooking = (ImageView) findViewById(R.id.ia_cooking);
		buyCooking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyTipsApp.getBillingUtils().buy(DailyTipsApp.SKU_COOKING, AdditionalSettingsActivity.this, new PurchaseDelegate() {
					@Override
					public void onSuccess() {
						bindUIbyPurchasese();
					}
				});
			}
		});
		buyHome = (ImageView) findViewById(R.id.ia_home);
		buyHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyTipsApp.getBillingUtils().buy(DailyTipsApp.SKU_HOME, AdditionalSettingsActivity.this, new PurchaseDelegate() {
					@Override
					public void onSuccess() {
						bindUIbyPurchasese();
					}
				});
			}
		});
		buyWork = (ImageView) findViewById(R.id.ia_work);
		buyWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyTipsApp.getBillingUtils().buy(DailyTipsApp.SKU_WORK, AdditionalSettingsActivity.this, new PurchaseDelegate() {
					@Override
					public void onSuccess() {
						bindUIbyPurchasese();
					}
				});
			}
		});
		buyLifestyle = (ImageView) findViewById(R.id.ia_life);
		buyLifestyle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyTipsApp.getBillingUtils().buy(DailyTipsApp.SKU_LIFESTYLE, AdditionalSettingsActivity.this, new PurchaseDelegate() {
					@Override
					public void onSuccess() {
						bindUIbyPurchasese();
					}
				});
			}
		});
	}

	private void bindUIbyPurchasese() {
		Map<String, Boolean> purchases = DailyTipsApp.getPurchases();
		if (purchases.get(DailyTipsApp.SKU_TOTAL)) {
			buyTotal.setEnabled(false);
			buyCooking.setEnabled(false);
			buyHome.setEnabled(false);
			buyWork.setEnabled(false);
			buyLifestyle.setEnabled(false);
		}
		if (purchases.get(DailyTipsApp.SKU_HOME)) {
			buyHome.setEnabled(false);
		}
		if (purchases.get(DailyTipsApp.SKU_COOKING)) {
			buyCooking.setEnabled(false);
		}
		if (purchases.get(DailyTipsApp.SKU_WORK)) {
			buyWork.setEnabled(false);
		}
		if (purchases.get(DailyTipsApp.SKU_LIFESTYLE)) {
			buyLifestyle.setEnabled(false);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.appear_from_left, R.anim.disappear_to_right);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DailyTipsApp.setContext(this);
		//lifecycleHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BillingUtils.RC_BUY) {
			DailyTipsApp.getBillingUtils().handleActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
		lifecycleHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		lifecycleHelper.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		lifecycleHelper.onPause();
	}

	private void selectButtons(int selected) {
		shareEmail.setSelected(false);
		shareTw.setSelected(false);
		shareVk.setSelected(false);
		shareFb.setSelected(false);
		switch (selected) {
			case Share.FACEBOOK:
				shareFb.setSelected(true);
				break;
			case Share.VK:
				shareVk.setSelected(true);
				break;
			case Share.TWITTER:
				shareTw.setSelected(true);
				break;
			case Share.EMAIL:
				shareEmail.setSelected(true);
				break;
		}
	}
}
