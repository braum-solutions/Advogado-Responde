package com.braumsolutions.advogadoresponde.View;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class ReportCaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvDescription;
    private TextInputLayout tilDescription;
    private TextInputEditText etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_case);

        castWidgets();
        setTypeface();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setTypeface() {
        tvDescription.setTypeface(TypefaceLight(getApplicationContext()));
        tilDescription.setTypeface(TypefaceLight(getApplicationContext()));
        etDescription.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        tvDescription = findViewById(R.id.tvDescription);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
