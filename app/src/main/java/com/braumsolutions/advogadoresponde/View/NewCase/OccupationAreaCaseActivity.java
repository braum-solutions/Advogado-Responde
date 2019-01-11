package com.braumsolutions.advogadoresponde.View.NewCase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Locale;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EDIT;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_BR;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_EN;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PICTURE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class OccupationAreaCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvArea, tvAreaMsg, tvMsg;
    private MaterialSpinner spOcuppationArea;
    private Button btnNext;
    private String key, area, image, pdf, description, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupation_area_case);

        castWidgets();
        setTypeface();
        setAnimation();
        getIntentBundle();

        if (Objects.equals(Locale.getDefault().getDisplayLanguage(), "English")) {
            spOcuppationArea.setItems(OCCUPATION_AREA_ARRAY_EN);
        } else {
            spOcuppationArea.setItems(OCCUPATION_AREA_ARRAY_BR);
        }

        if (Objects.equals(edit, "true")) {
            spOcuppationArea.setSelectedIndex(Integer.parseInt(area));
        }

    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(KEY) != null) {
                key = bundle.getString(KEY);
            }
            if (bundle.getString(OCCUPATION_AREA) != null) {
                area = bundle.getString(OCCUPATION_AREA);
            }
            if (bundle.getString(PICTURE) != null) {
                image = bundle.getString(PICTURE);
            }
            if (bundle.getString(PDF) != null) {
                pdf = bundle.getString(PDF);
            }
            if (bundle.getString(DESCRIPTION) != null) {
                description = bundle.getString(DESCRIPTION);
            }
            if (bundle.getString(EDIT) != null){
                edit = bundle.getString(EDIT);
            }
        }
    }

    private void setAnimation() {
        tvArea.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvAreaMsg.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        spOcuppationArea.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnNext.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        tvMsg.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void setTypeface() {
        tvArea.setTypeface(TypefaceBold(getApplicationContext()));
        tvAreaMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnNext.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        tvArea = findViewById(R.id.tvComment);
        tvAreaMsg = findViewById(R.id.tvDecriptionMsg);
        tvMsg = findViewById(R.id.tvMsg);
        spOcuppationArea = findViewById(R.id.spOccupationArea);
        btnNext = findViewById(R.id.btnComplete);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnComplete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnComplete:
                //if (spOcuppationArea.getSelectedIndex() == 0) {
                //    SnackWarning(getString(R.string.select_occupation_area));
                //} else {
                Intent intent = new Intent(getApplicationContext(), FileCaseActivity.class);
                intent.putExtra(OCCUPATION_AREA, String.valueOf(spOcuppationArea.getSelectedIndex()));
                intent.putExtra(KEY, key);
                intent.putExtra(PICTURE, image);
                intent.putExtra(PDF, pdf);
                intent.putExtra(DESCRIPTION, description);
                intent.putExtra(EDIT, edit);
                startActivity(intent);
                //}
                break;
        }
    }

    public void SnackWarning(String msg) {
        Snackbar.with(OccupationAreaCaseActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
