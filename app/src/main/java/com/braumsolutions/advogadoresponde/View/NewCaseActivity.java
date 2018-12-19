package com.braumsolutions.advogadoresponde.View;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.braumsolutions.advogadoresponde.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class NewCaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialSpinner spOccupationArea;
    private TextInputEditText etDescription;
    private TextInputLayout tilDescription;
    private String[] caseArrayBr = {"Trabalhista", "Previdenciário", "Outro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_case);

        castWidgets();
        setTypeface();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.new_case));
        getSupportActionBar().setSubtitle(R.string.tell_about_case);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        spOccupationArea.setItems(caseArrayBr);

    }

    private void setTypeface() {
        etDescription.setTypeface(TypefaceLight(getApplicationContext()));
        tilDescription.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        spOccupationArea = findViewById(R.id.spOccupationArea);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
    }
}
