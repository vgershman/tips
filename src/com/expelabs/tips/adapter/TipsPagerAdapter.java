package com.expelabs.tips.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.delegate.NavigationDelegate;
import com.expelabs.tips.dto.Tip;
import com.expelabs.tips.fragment.TipFragment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 15.05.13
 * Time: 0:15
 * To change this template use File | Settings | File Templates.
 */
public class TipsPagerAdapter extends FragmentStatePagerAdapter {

    public static final int FAKE_COUNT = 10000;
    private List<Tip> tipList;
    private NavigationDelegate navigationDelegate;
    private ViewPager viewPager;

    public TipsPagerAdapter(FragmentManager fm, List<Tip> tipList, ViewPager tipsPager) {
        super(fm);
        this.viewPager = tipsPager;
        navigationDelegate = new NavigationDelegate() {
            @Override
            public void onLeft() {
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }

            @Override
            public void onRight() {
                if (viewPager.getCurrentItem() < FAKE_COUNT) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        };
        this.tipList = tipList;
    }

    @Override
    public Fragment getItem(int i) {
        int realPosition = i % tipList.size();
        return TipFragment.newInstance(tipList.get(realPosition), navigationDelegate);
    }

    @Override
    public int getCount() {
        return FAKE_COUNT;
    }
}
