package com.expelabs.tips.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import au.com.bytecode.opencsv.CSVReader;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
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
import java.util.List;

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
    private NavigationDelegate navigationDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        currentCategory = (Category)getIntent().getSerializableExtra("category");
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
        final List<Tip>tipList = loadTips(currentCategory);
        navigationDelegate = new NavigationDelegate() {
            @Override
            public void onLeft() {
                if(tipPager.getCurrentItem() > 0){
                    tipPager.setCurrentItem(tipPager.getCurrentItem() - 1, true);
                }else{
                    tipPager.setCurrentItem(tipList.size() - 1, true);
                }
            }

            @Override
            public void onRight() {
                if (tipPager.getCurrentItem() < tipList.size() - 1) {
                    tipPager.setCurrentItem(tipPager.getCurrentItem() + 1, true);
                }else{
                    tipPager.setCurrentItem(0,true);
                }
            }
        };
        final TipsPagerAdapter tipsPagerAdapter = new TipsPagerAdapter(getSupportFragmentManager(), tipList, navigationDelegate);
        tipPager.setAdapter(tipsPagerAdapter);

    }

    private List<Tip> loadTips(Category category) {
        List<Tip> results = new ArrayList<Tip>();
        int tipsFile = 0;
        switch (category) {
            case COOKING:
                tipsFile = R.raw.cooking;
                break;
            case HOME:
                tipsFile = R.raw.home;
                break;
            case RANDOM:
                tipsFile = R.raw.home;
        }
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getResources().openRawResource(tipsFile))));
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
        getSharedPreferences(DailyTipsApp.PREFERENCES_NAME,MODE_PRIVATE).edit().putInt(CURRENT_ITEM + currentCategory.name(),tipPager.getCurrentItem()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentItem  = getSharedPreferences(DailyTipsApp.PREFERENCES_NAME,MODE_PRIVATE).getInt(CURRENT_ITEM + currentCategory.name(),0);
        tipPager.setCurrentItem(currentItem,false);
    }

    private void initActionBar() {
        //getSupportActionBar().setHomeButtonEnabled(true);
    }
}
