package com.braumsolutions.advogadoresponde.View.Cases;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.Model.ReportsModel;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.View.NewCase.OccupationAreaCaseActivity;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Locale;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.getDateTime;
import static com.braumsolutions.advogadoresponde.Utils.ToastUtils.ToastSuccess;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_BR;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_EN;
import static com.braumsolutions.advogadoresponde.Utils.Utils.REASON_BR;
import static com.braumsolutions.advogadoresponde.Utils.Utils.REASON_EN;
import static com.braumsolutions.advogadoresponde.Utils.Utils.REPORTS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ReportCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvDescription;
    private TextInputLayout tilDescription;
    private TextInputEditText etDescription;
    private MaterialSpinner spReason;
    private String key;
    private Button btnSend;
    private KProgressHUD dialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_case);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getIntentBundle();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (Objects.equals(Locale.getDefault().getDisplayLanguage(), "English")) {
            spReason.setItems(REASON_EN);
        } else {
            spReason.setItems(REASON_BR);
        }

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilDescription.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (spReason.getSelectedIndex() == 0) {
                    SnackWarning(getString(R.string.select_reason));
                } else if (etDescription.getText().toString().trim().equals("")) {
                    tilDescription.setError(getString(R.string.fill_description));
                } else {
                    createDialog(getString(R.string.please_wait), getString(R.string.sendind));

                    DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(REPORTS).push();
                    ReportsModel report = new ReportsModel();
                    report.setReason(String.valueOf(spReason.getSelectedIndex()));
                    report.setDescription(etDescription.getText().toString().trim());
                    report.setKey(key);
                    report.setUser(mAuth.getCurrentUser().getUid());
                    report.setDate(getDateTime());
                    database.setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                ToastSuccess(getApplicationContext(), getString(R.string.send_success));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SnackError(e.getMessage());
                        }
                    });

                }
                break;
        }
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString(KEY);
        }
    }
    
    private void setTypeface() {
        tvDescription.setTypeface(TypefaceLight(getApplicationContext()));
        tilDescription.setTypeface(TypefaceLight(getApplicationContext()));
        etDescription.setTypeface(TypefaceLight(getApplicationContext()));
        btnSend.setTypeface(TypefaceLight(getApplicationContext()));
        spReason.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        tvDescription = findViewById(R.id.tvDescription);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
        spReason = findViewById(R.id.spReason);
        btnSend = findViewById(R.id.btnSend);
        findViewById(R.id.btnSend).setOnClickListener(this);
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(ReportCaseActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    public void SnackWarning(String msg) {
        Snackbar.with(ReportCaseActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackError(String msg) {
        Snackbar.with(ReportCaseActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
