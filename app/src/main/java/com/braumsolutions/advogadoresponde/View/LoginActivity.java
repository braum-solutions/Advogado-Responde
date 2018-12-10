package com.braumsolutions.advogadoresponde.View;

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
    private Button btnLawyer, btnClient, btnHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        castWidgets();
        setTypeface();

    }

    private void setTypeface() {
        btnLawyer.setTypeface(TypefaceLight(getApplicationContext()));
        btnClient.setTypeface(TypefaceLight(getApplicationContext()));
        btnHaveAccount.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        viewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.dots);
        btnLawyer = findViewById(R.id.btnLawyer);
        btnClient = findViewById(R.id.btnClient);
        btnHaveAccount = findViewById(R.id.btnHaveAccount);
        findViewById(R.id.btnLawyer).setOnClickListener(this);
        findViewById(R.id.btnClient).setOnClickListener(this);
        findViewById(R.id.btnHaveAccount).setOnClickListener(this);

        btnClient.setShadowLayer(5, 0, 0, Color.BLACK);
        btnLawyer.setShadowLayer(5, 0, 0, Color.BLACK);
        btnHaveAccount.setShadowLayer(5, 0, 0, Color.BLACK);

        SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClient:
                break;
            case R.id.btnLawyer:
                break;
            case R.id.btnHaveAccount:
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

}
