package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.MethodsUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceRegular;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvWelcome, tvSignIn;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnForgotPassword, btnLogin;
    private ProgressBar loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnLogin:
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    tilEmail.setError(getString(R.string.fill_email));
                    etEmail.requestFocus();
                } else if (!MethodsUtils.validEmail(email)) {
                    tilEmail.setError(getString(R.string.add_valid_email));
                    etEmail.requestFocus();
                } else if (password.isEmpty()) {
                    tilPassword.setError(getString(R.string.fill_password));
                    etPassword.requestFocus();
                } else {

                    disableFields();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                new AwesomeSuccessDialog(SignInActivity.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage(R.string.glad_you_back)
                                        .setColoredCircle(R.color.colorGreen)
                                        .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                                        .setCancelable(false)
                                        .setPositiveButtonText(getString(R.string.continu))
                                        .setPositiveButtonbackgroundColor(R.color.colorGreen)
                                        .setPositiveButtonTextColor(R.color.white)
                                        .setPositiveButtonClick(new Closure() {
                                            @Override
                                            public void exec() {
                                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();

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

                break;
            case R.id.btnForgotPassword:
                String mail = etEmail.getText().toString().trim();
                if (mail.isEmpty()) {
                    tilEmail.setError(getString(R.string.fill_email));
                    etEmail.requestFocus();
                } else if (!MethodsUtils.validEmail(mail)) {
                    tilEmail.setError(getString(R.string.add_valid_email));
                    etEmail.requestFocus();
                } else {
                    disableFields();
                    btnForgotPassword.setEnabled(false);

                    mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                enableFields();

                                new AwesomeSuccessDialog(SignInActivity.this)
                                        .setTitle(R.string.app_name)
                                        .setMessage(R.string.revocer_pass_msg)
                                        .setColoredCircle(R.color.colorGreen)
                                        .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                                        .setCancelable(false)
                                        .setPositiveButtonText(getString(R.string.continu))
                                        .setPositiveButtonbackgroundColor(R.color.colorGreen)
                                        .setPositiveButtonTextColor(R.color.white)
                                        .setPositiveButtonClick(new Closure() {
                                            @Override
                                            public void exec() {
                                                btnForgotPassword.setEnabled(true);
                                            }
                                        })
                                        .show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            enableFields();
                            btnForgotPassword.setEnabled(true);
                            SnackError(e.getMessage());
                        }
                    });

                }
                break;
        }
    }

    private void setTypeface() {
        tvWelcome.setTypeface(TypefaceBold(getApplicationContext()));
        tvSignIn.setTypeface(TypefaceRegular(getApplicationContext()));
        tilPassword.setTypeface(TypefaceLight(getApplicationContext()));
        tilEmail.setTypeface(TypefaceLight(getApplicationContext()));
        etPassword.setTypeface(TypefaceLight(getApplicationContext()));
        etEmail.setTypeface(TypefaceLight(getApplicationContext()));
        btnLogin.setTypeface(TypefaceBold(getApplicationContext()));
        btnForgotPassword.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        tvWelcome = findViewById(R.id.tvJoinUs);
        tvSignIn = findViewById(R.id.tvSignUp);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        loading = findViewById(R.id.loading);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnForgotPassword).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    private void enableFields() {
        btnLogin.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
    }

    private void disableFields() {
        btnLogin.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
    }

    public void SnackError(String msg) {
        Snackbar.with(SignInActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.SHORT)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
