package com.expelabs.tips.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import au.com.bytecode.opencsv.CSVReader;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.expelabs.tips.R;
import com.expelabs.tips.adapter.TipsPagerAdapter;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.delegate.NavigationDelegate;
import com.expelabs.tips.dto.Category;
import com.expelabs.tips.dto.Tip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        currentCategory = (Category) getIntent().getSerializableExtra("category");
        initActionBar();
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    private void initControls() {
        tipPager = (ViewPager) findViewById(R.id.tip_pager);
        List<Tip> tipList = prepareTips(currentCategory);
        TipsPagerAdapter tipsPagerAdapter = new TipsPagerAdapter(getSupportFragmentManager(), tipList, tipPager);
        tipPager.setAdapter(tipsPagerAdapter);

    }

    private List<Tip> prepareTips(Category category) {
        List<Tip> results = new ArrayList<Tip>();
        switch (category) {
            case COOKING:
                results = loadTips(category,R.raw.cooking);
                break;
            case HOME:
                results = loadTips(category,R.raw.home);
                break;
            case RANDOM:
                results.addAll(loadTips(Category.COOKING,R.raw.cooking));
                results.addAll(loadTips(Category.HOME, R.raw.home));
                Collections.shuffle(results,new Random());
                break;
        }
        return results;

    }

    public List<Tip>loadTips(Category category,int tipsFileResourceId){
        List<Tip>results = new ArrayList<Tip>();
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getResources().openRawResource(tipsFileResourceId))));
        String[] nextLine;
        int i = 0;
        try {
            while ((nextLine = reader.readNext()) != null) {
                i++;
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
        int currentItem = getSharedPreferences(DailyTipsApp.PREFERENCES_NAME, MODE_PRIVATE).getInt(CURRENT_ITEM + currentCategory.name(), TipsPagerAdapter.FAKE_COUNT/2);
        tipPager.setCurrentItem(currentItem, false);
    }

    private void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        int titleId = 0;
        switch (currentCategory){
            case COOKING: titleId = R.string.category_cooking;break;
            case HOME: titleId = R.string.category_home;break;
            case WORK: titleId = R.string.category_work;break;
            case LIFESTYLE: titleId = R.string.category_lifestyle;break;
            case RANDOM: titleId = R.string.category_random;break;
        }
        getActionBar().setTitle(titleId);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(CategoryActivity.this,AdditionalSettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
