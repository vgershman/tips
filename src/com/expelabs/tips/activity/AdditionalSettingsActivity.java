package com.expelabs.tips.activity;

import android.app.Activity;
import android.os.Bundle;
import com.expelabs.tips.R;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 19.05.13
 * Time: 1:36
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initActionBar();
    }

    private void initActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(getString(R.string.additional_settings_title));
    }
}
