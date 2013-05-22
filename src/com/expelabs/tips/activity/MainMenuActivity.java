package com.expelabs.tips.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.expelabs.tips.R;
import com.expelabs.tips.dto.Category;

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
                goToCathegory(Category.COOKING);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCathegory(Category.HOME);
            }
        });
        lifestyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCathegory(Category.LIFESTYLE);
            }
        });
        workBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCathegory(Category.WORK);
            }
        });
        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
