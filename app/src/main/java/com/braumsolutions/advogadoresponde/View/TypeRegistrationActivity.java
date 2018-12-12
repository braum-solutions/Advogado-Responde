package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;

public class TypeRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName, tvNameMsg, tvTypeMsg;
    private Button btnLawyer, btnClient;
    private String name, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_registration);

        castWidgets();
        setTypeface();
        getIntentBundle();

    }

    private void castWidgets() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnLawyer).setOnClickListener(this);
        findViewById(R.id.btnClient).setOnClickListener(this);
        tvName = findViewById(R.id.tvName);
        tvNameMsg = findViewById(R.id.tvNameMsg);
        tvTypeMsg = findViewById(R.id.tvTypeMsg);
        btnClient = findViewById(R.id.btnClient);
        btnLawyer = findViewById(R.id.btnLawyer);
    }

    private void setTypeface() {
        tvName.setTypeface(TypefaceBold(getApplicationContext()));
        tvNameMsg.setTypeface(TypefaceBold(getApplicationContext()));
        tvTypeMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnLawyer.setTypeface(TypefaceLight(getApplicationContext()));
        btnClient.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(NAME);
            lastName = bundle.getString(LAST_NAME);
            tvName.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnLawyer:
                Intent intentLawyer = new Intent(getApplicationContext(), LawyerCodeActivity.class);
                intentLawyer.putExtra(NAME, name);
                intentLawyer.putExtra(LAST_NAME, lastName);
                intentLawyer.putExtra(TYPE_REGISTER, "0");
                startActivity(intentLawyer);
                break;
            case R.id.btnClient:
                Intent intentClient = new Intent(getApplicationContext(), SignUpActivity.class);
                intentClient.putExtra(NAME, name);
                intentClient.putExtra(LAST_NAME, lastName);
                intentClient.putExtra(TYPE_REGISTER, "1");
                startActivity(intentClient);
                break;
        }
    }

}
