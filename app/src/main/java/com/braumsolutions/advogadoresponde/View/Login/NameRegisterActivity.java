package com.braumsolutions.advogadoresponde.View.Login;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.View.Others.PrivacyPoliceActivity;

import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceRegular;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;

public class NameRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHi, tvName, tvPoint, tvMsg, tvMsg2, tvPrivacyPolice, tvAnd, tvTermsServices;
    private TextInputLayout tilName, tilLastName;
    private TextInputEditText etName, etLastName;
    private Button btnNext;
    private String name = "", lastName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_register);

        castWidgets();
        setTypeface();
        setAnimation();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != "") {
                    tilName.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = etName.getText().toString().trim();
                lastName = etLastName.getText().toString().trim();
                if (count == 0) {
                    tvName.setText(getString(R.string.what_your_name));
                    tvPoint.setText(getString(R.string.interrogation));
                    tvName.setTextColor(getResources().getColor(R.color.colorText));
                } else {
                    tvPoint.setText(getString(R.string.exclamation));
                    tvName.setText(String.format("%s %s", name, lastName));
                    tvName.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != "") {
                    tilLastName.setError(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = etName.getText().toString().trim();
                lastName = etLastName.getText().toString().trim();
                if (Objects.equals(name, "") && Objects.equals(lastName, "")) {
                    tvName.setText(getString(R.string.what_your_name));
                    tvPoint.setText(getString(R.string.interrogation));
                    tvName.setTextColor(getResources().getColor(R.color.colorText));
                } else {
                    tvPoint.setText(getString(R.string.exclamation));
                    tvName.setText(String.format("%s %s", name, lastName));
                    tvName.setTextColor(getResources().getColor(R.color.colorAccent));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAnimation() {
        tvHi.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvName.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvPoint.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvMsg.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tilName.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        tilLastName.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        etName.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        etLastName.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        btnNext.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        tvMsg2.setAnimation(AnimationFadeIn2500(getApplicationContext()));
        tvTermsServices.setAnimation(AnimationFadeIn2500(getApplicationContext()));
        tvAnd.setAnimation(AnimationFadeIn2500(getApplicationContext()));
        tvPrivacyPolice.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void setTypeface() {
        tvHi.setTypeface(TypefaceBold(getApplicationContext()));
        tvName.setTypeface(TypefaceBold(getApplicationContext()));
        tvPoint.setTypeface(TypefaceBold(getApplicationContext()));
        tvMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tilName.setTypeface(TypefaceRegular(getApplicationContext()));
        tilLastName.setTypeface(TypefaceRegular(getApplicationContext()));
        etName.setTypeface(TypefaceRegular(getApplicationContext()));
        etLastName.setTypeface(TypefaceRegular(getApplicationContext()));
        btnNext.setTypeface(TypefaceLight(getApplicationContext()));
        tvMsg2.setTypeface(TypefaceLight(getApplicationContext()));
        tvTermsServices.setTypeface(TypefaceLight(getApplicationContext()));
        tvAnd.setTypeface(TypefaceLight(getApplicationContext()));
        tvPrivacyPolice.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        tvMsg2 = findViewById(R.id.tvMs2);
        tvPrivacyPolice = findViewById(R.id.tvPrivacyPolice);
        tvAnd = findViewById(R.id.tvAnd);
        tvTermsServices = findViewById(R.id.tvTermsServices);
        tvHi = findViewById(R.id.tvHi);
        tvName = findViewById(R.id.tvName);
        tvPoint = findViewById(R.id.tvPoint);
        tvMsg = findViewById(R.id.tvMsg);
        tilName = findViewById(R.id.tilName);
        tilLastName = findViewById(R.id.tilLastName);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        btnNext = findViewById(R.id.btnComplete);
        findViewById(R.id.btnComplete).setOnClickListener(this);
        findViewById(R.id.tvTermsServices).setOnClickListener(this);
        findViewById(R.id.tvPrivacyPolice).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnComplete:
                if (Objects.equals(name, "")) {
                    tilName.setError(getString(R.string.fill_name));
                    etName.requestFocus();
                } else if (Objects.equals(lastName, "")) {
                    tilLastName.setError(getString(R.string.fill_last_name));
                    etLastName.requestFocus();
                } else {
                    Intent intentNameLastName = new Intent(getApplicationContext(), UserImageActivity.class);
                    intentNameLastName.putExtra(NAME, etName.getText().toString().trim());
                    intentNameLastName.putExtra(LAST_NAME, etLastName.getText().toString().trim());
                    startActivity(intentNameLastName);
                }
                break;
            case R.id.tvTermsServices:

                break;
            case R.id.tvPrivacyPolice:
                Intent intPrivacy = new Intent(getApplicationContext(), PrivacyPoliceActivity.class);
                startActivity(intPrivacy);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
