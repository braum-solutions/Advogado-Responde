package com.braumsolutions.advogadoresponde.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.braumsolutions.advogadoresponde.R;

public class YourCasesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_cases);

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.your_cases));
        getSupportActionBar().setSubtitle(getString(R.string.your_cases_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
    }

}
