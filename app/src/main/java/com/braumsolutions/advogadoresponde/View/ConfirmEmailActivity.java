package com.braumsolutions.advogadoresponde.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class ConfirmEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvMsg, tvTitle;
    private Button btnResend;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle("Confirmação de Email");

        sendEmailVerification();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnResend:
                disableFields();
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUser = mAuth.getCurrentUser();

                        if (!mUser.isEmailVerified()) {
                            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        enableFields();
                                        SnackSuccess("E-mail enviado com sucesso!");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    enableFields();
                                    SnackError(e.getMessage());
                                }
                            });
                        } else {
                            sendToMainActivity();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        disableFields();
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (mUser.isEmailVerified()) {
                    sendToMainActivity();
                } else {
                    enableFields();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableFields();
                SnackError(e.getMessage());
            }
        });
        super.onResume();
    }

    private void sendEmailVerification() {
        disableFields();
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mUser = mAuth.getCurrentUser();
                mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            enableFields();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableFields();
                SnackError(e.getMessage());
            }
        });
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void enableFields() {
        btnResend.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    private void disableFields() {
        btnResend.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    private void setTypeface() {
        tvTitle.setTypeface(TypefaceBold(getApplicationContext()));
        tvMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnResend.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        btnResend = findViewById(R.id.btnResend);
        tvMsg = findViewById(R.id.tvMsg);
        tvTitle = findViewById(R.id.tvTitle);
        loading = findViewById(R.id.loading);
        findViewById(R.id.btnResend).setOnClickListener(this);
    }

    public void SnackError(String msg) {
        Snackbar.with(ConfirmEmailActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.SHORT)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(ConfirmEmailActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
