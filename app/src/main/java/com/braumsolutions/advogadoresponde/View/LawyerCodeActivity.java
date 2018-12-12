package com.braumsolutions.advogadoresponde.View;

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
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_CODE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY;

public class LawyerCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialSpinner spUF;
    private TextView tvEnterOab, tvEnterOabMsg;
    private TextInputLayout tilOab;
    private TextInputEditText etOab;
    private Button btnContinue;
    private String name, lastName, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_code);

        castWidgets();
        setTypeface();
        getIntentBundle();

        etOab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilOab.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setTypeface() {
        tvEnterOab.setTypeface(TypefaceBold(getApplicationContext()));
        tvEnterOabMsg.setTypeface(TypefaceLight(getApplicationContext()));
        etOab.setTypeface(TypefaceBold(getApplicationContext()));
        tilOab.setTypeface(TypefaceLight(getApplicationContext()));
        btnContinue.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnContinue).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        tvEnterOab = findViewById(R.id.tvEnterOab);
        btnContinue = findViewById(R.id.btnContinue);
        tvEnterOabMsg = findViewById(R.id.tvtvEnterOabMsg);
        tilOab = findViewById(R.id.tilOab);
        etOab = findViewById(R.id.etOab);
        spUF = findViewById(R.id.spUF);
        spUF.setItems(UF_ARRAY);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(NAME);
            lastName = bundle.getString(LAST_NAME);
            type = bundle.getString(TYPE_REGISTER);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnContinue:
                String uf = String.valueOf(spUF.getSelectedIndex());
                String oab = etOab.getText().toString().trim();

                if (uf.equals("0")) {
                    SnackWarning(getString(R.string.select_uf_oab));
                } else if (Objects.equals(oab, "")) {
                    tilOab.setError(getString(R.string.enter_oab));
                } else {
                    Intent intentLawyer = new Intent(getApplicationContext(), SignUpActivity.class);
                    intentLawyer.putExtra(NAME, name);
                    intentLawyer.putExtra(LAST_NAME, lastName);
                    intentLawyer.putExtra(TYPE_REGISTER, type);
                    intentLawyer.putExtra(OAB_UF, uf);
                    intentLawyer.putExtra(OAB_CODE, oab);
                    startActivity(intentLawyer);
                }

                break;
        }
    }

    public void SnackWarning(String msg) {
        Snackbar.with(LawyerCodeActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
