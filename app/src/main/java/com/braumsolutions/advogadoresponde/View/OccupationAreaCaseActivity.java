package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.jaredrummler.materialspinner.MaterialSpinner;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;

public class OccupationAreaCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvArea, tvAreaMsg, tvMsg;
    private MaterialSpinner spOcuppationArea;
    private Button btnNext;
    private String[] caseArrayBr = {"Trabalhista", "Previdenciário", "Outro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupation_area_case);

        castWidgets();
        setTypeface();
        setAnimation();

        spOcuppationArea.setItems(caseArrayBr);

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
        tvArea = findViewById(R.id.tvArea);
        tvAreaMsg = findViewById(R.id.tvAreaMsg);
        tvMsg = findViewById(R.id.tvMsg);
        spOcuppationArea = findViewById(R.id.spOccupationArea);
        btnNext = findViewById(R.id.btnNext);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnNext).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnNext:
                    if (spOcuppationArea.getSelectedIndex() == 0) {
                        SnackWarning(getString(R.string.select_occupation_area));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), null);
                        intent.putExtra(OCCUPATION_AREA, String.valueOf(spOcuppationArea.getSelectedIndex()));
                        startActivity(intent);
                    }
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
