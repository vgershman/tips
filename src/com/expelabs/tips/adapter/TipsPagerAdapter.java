package com.expelabs.tips.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

    private List<Tip> tipList;

    public TipsPagerAdapter(FragmentManager fm, List<Tip> tipList) {
        super(fm);
        this.tipList = tipList;
    }

    @Override
    public Fragment getItem(int i) {
        return TipFragment.newInstance(tipList.get(i));
    }

    @Override
    public int getCount() {
        return tipList.size();
    }
}
