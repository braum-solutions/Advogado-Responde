package com.braumsolutions.advogadoresponde.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.Model.SliderAdapter;
import com.braumsolutions.advogadoresponde.R;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mDotLayout;
    private ViewPager viewPager;
    private Button btnCreateAcc, btnHaveAccount, btnPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        castWidgets();
        setTypeface();

    }

    private void setTypeface() {
        btnCreateAcc.setTypeface(TypefaceLight(getApplicationContext()));
        btnPrivacyPolicy.setTypeface(TypefaceLight(getApplicationContext()));
        btnHaveAccount.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        viewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.dots);
        btnCreateAcc = findViewById(R.id.btnCreateAcc);
        btnHaveAccount = findViewById(R.id.btnHaveAccount);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolice);
        findViewById(R.id.btnCreateAcc).setOnClickListener(this);
        findViewById(R.id.btnHaveAccount).setOnClickListener(this);
        findViewById(R.id.btnPrivacyPolice).setOnClickListener(this);

        btnCreateAcc.setShadowLayer(5, 0, 0, Color.BLACK);
        btnHaveAccount.setShadowLayer(5, 0, 0, Color.BLACK);
        btnPrivacyPolicy.setShadowLayer(5, 0, 0, Color.BLACK);

        SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAcc:
                Intent intCreate = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intCreate);
                break;
            case R.id.btnHaveAccount:
                Intent intHave = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intHave);
                break;
            case R.id.btnPrivacyPolice:
                Intent intPrivacy = new Intent(getApplicationContext(), PrivacyPoliceActivity.class);
                startActivity(intPrivacy);
                break;
        }
    }

    public void addDotsIndicator(int position) {
        TextView[] mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText("•");
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorAccent));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

}
