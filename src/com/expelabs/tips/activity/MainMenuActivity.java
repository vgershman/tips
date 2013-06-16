package com.expelabs.tips.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.expelabs.tips.R;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.dto.Category;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 13.05.13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */

public class MainMenuActivity extends Activity {

    ImageView cookingBtn;
    ImageView homeBtn;
    ImageView lifestyleBtn;
    ImageView workBtn;
    ImageView randomBtn;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        initItems();
        initListeners();
    }

    private void initItems() {
        cookingBtn = (ImageView)findViewById(R.id.cooking_btn);
        homeBtn = (ImageView)findViewById(R.id.home_btn);
        lifestyleBtn = (ImageView)findViewById(R.id.lifestyle_btn);
        workBtn = (ImageView)findViewById(R.id.work_btn);
        randomBtn = (ImageView)findViewById(R.id.random_btn);
    }

    private void initListeners() {
        cookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Map<String,String>params = new HashMap<String, String>();
				params.put("category","cooking");
				FlurryAgent.logEvent("Category selected",params);
                goToCathegory(Category.COOKING);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Map<String,String>params = new HashMap<String, String>();
				params.put("category","home");
				FlurryAgent.logEvent("Category selected",params);
                goToCathegory(Category.HOME);
            }
        });
        lifestyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Map<String,String>params = new HashMap<String, String>();
				params.put("category","lifestyle");
				FlurryAgent.logEvent("Category selected",params);
                goToCathegory(Category.LIFESTYLE);
            }
        });
        workBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Map<String,String>params = new HashMap<String, String>();
				params.put("category","work");
				FlurryAgent.logEvent("Category selected",params);
                goToCathegory(Category.WORK);
            }
        });
        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Map<String,String>params = new HashMap<String, String>();
				params.put("category","random");
				FlurryAgent.logEvent("Category selected",params);
                goToCathegory(Category.RANDOM);
            }
        });
    }

    private void goToCathegory(Category category) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
        overridePendingTransition(R.anim.appear_from_right,R.anim.disappear_to_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DailyTipsApp.setContext(this);
    }

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this,DailyTipsApp.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
