package com.expelabs.tips.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import au.com.bytecode.opencsv.CSVReader;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.expelabs.tips.R;
import com.expelabs.tips.adapter.TipsPagerAdapter;
import com.expelabs.tips.dto.Category;
import com.expelabs.tips.dto.Tip;
import com.expelabs.tips.util.ImageUtils;
import com.expelabs.tips.app.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 14.05.13
 * Time: 0:44
 * To change this template use File | Settings | File Templates.
 */
public class CategoryActivity extends SherlockFragmentActivity {

    private Category currentCategory;
    private static final String CURRENT_ITEM = "cur_item";
    private ViewPager tipPager;
    private TipsPagerAdapter tipsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        currentCategory = (Category) getIntent().getSerializableExtra("category");
        initActionBar();
        initControls();
        DailyTipsApp.clearScrollCounter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    private void initControls() {
        tipPager = (ViewPager) findViewById(R.id.tip_pager);
        tipPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                DailyTipsApp.incrementScrollCounter();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private List<Tip> prepareTips(Category category) {
        List<Tip> results = new ArrayList<Tip>();
        switch (category) {
            case COOKING:
                results = loadTips(category, R.raw.cooking);
                break;
            case HOME:
                results = loadTips(category, R.raw.home);
                break;
            case LIFESTYLE:
                results = loadTips(category, R.raw.lifestyle);
                break;
            case WORK:
                results = loadTips(category, R.raw.work);
                break;
            case RANDOM:
                results.addAll(loadTips(Category.COOKING, R.raw.cooking));
                results.addAll(loadTips(Category.HOME, R.raw.home));
                results.addAll(loadTips(Category.LIFESTYLE, R.raw.lifestyle));
                results.addAll(loadTips(Category.WORK, R.raw.work));
                Collections.shuffle(results, new Random());
                break;
        }
        return results;
    }

    public List<Tip> loadTips(Category category, int tipsFileResourceId) {
        Map<String, Boolean> purchases = DailyTipsApp.getPurchases();
        int limit = 25;
        if (purchases.get(DailyTipsApp.SKU_TOTAL) || purchases.get(getPackageName() + "." + category.name().toLowerCase())) {
            limit = 125;
        }
        List<Tip> results = new ArrayList<Tip>();
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getResources().openRawResource(tipsFileResourceId))));
        String[] nextLine;
        int i = 0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                i++;
                if (i > limit) {
                    break;
                }
                Tip newTip = new Tip();
                newTip.setId(i);
                newTip.setCategoryName(category.name().toLowerCase());
                newTip.setText(nextLine[0]);
                newTip.setTextItalic(nextLine[1]);
                results.add(newTip);
            }
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
        return results;
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).edit().putInt(CURRENT_ITEM + currentCategory.name(), tipPager.getCurrentItem()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DailyTipsApp.setContext(this);
        List<Tip> tipList = prepareTips(currentCategory);
        tipsPagerAdapter = new TipsPagerAdapter(getSupportFragmentManager(), tipList, tipPager);
        tipPager.setAdapter(tipsPagerAdapter);
        int currentItem = getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).getInt(CURRENT_ITEM + currentCategory.name(), TipsPagerAdapter.FAKE_COUNT / 2);
        tipPager.setCurrentItem(currentItem, false);
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int titleId = 0;
        switch (currentCategory) {
            case COOKING:
                titleId = R.string.category_cooking;
                break;
            case HOME:
                titleId = R.string.category_home;
                break;
            case WORK:
                titleId = R.string.category_work;
                break;
            case LIFESTYLE:
                titleId = R.string.category_lifestyle;
                break;
            case RANDOM:
                titleId = R.string.category_random;
                break;
        }
        getSupportActionBar().setTitle(titleId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(CategoryActivity.this, AdditionalSettingsActivity.class));
                overridePendingTransition(R.anim.appear_from_right, R.anim.disappear_to_left);
                return true;
            case R.id.menu_item_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Tip tip2Share = tipsPagerAdapter.getCurrent().getTip();
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("plain/text");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                targetedShare.setType("image/jpeg"); // put here your mime type
                targetedShare.putExtra(Intent.EXTRA_TEXT, tip2Share.getText() + "\n" + tip2Share.getTextItalic());
                targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(DailyTipsApp.HOSTING_BASE_URL + "/" + tip2Share.getCategoryName().toLowerCase() + "/" + tip2Share.getId() + ".jpg"));
                targetedShare.setPackage(info.activityInfo.packageName);
                targetedShareIntents.add(targetedShare);
            }
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), getString(R.string.share_select_ac));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.appear_from_left, R.anim.disappear_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
