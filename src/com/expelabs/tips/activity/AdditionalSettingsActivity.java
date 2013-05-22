package com.expelabs.tips.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.expelabs.tips.R;
import com.expelabs.tips.app.*;
import com.expelabs.tips.delegate.PurchaseDelegate;
import com.expelabs.tips.util.BillingUtils;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 19.05.13
 * Time: 1:36
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalSettingsActivity extends SherlockActivity {

    private ImageView buyTotal;
    private ImageView buyCooking;
    private ImageView buyHome;
    private ImageView buyWork;
    private ImageView buyLifestyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initActionBar();
        initComponents();
        bindUIbyPurchasese();
    }

    private void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(getString(R.string.additional_settings_title));
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

    private void initComponents() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BillingUtils.RC_BUY) {
            DailyTipsApp.getBillingUtils().handleActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
