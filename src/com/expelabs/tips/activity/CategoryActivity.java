package com.expelabs.tips.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.expelabs.tips.R;
import com.expelabs.tips.adapter.TipsPagerAdapter;
import com.expelabs.tips.dto.Tip;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 14.05.13
 * Time: 0:44
 * To change this template use File | Settings | File Templates.
 */
public class CategoryActivity extends SherlockFragmentActivity {

    private ViewPager tipPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        initActionBar();
        initControls();
    }

    private void initControls() {
        tipPager = (ViewPager) findViewById(R.id.tip_pager);
        TipsPagerAdapter tipsPagerAdapter = new TipsPagerAdapter(getSupportFragmentManager(), loadTips());
        tipPager.setAdapter(tipsPagerAdapter);
    }

    private List<Tip> loadTips() {
        return null;
    }

    private void initActionBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
