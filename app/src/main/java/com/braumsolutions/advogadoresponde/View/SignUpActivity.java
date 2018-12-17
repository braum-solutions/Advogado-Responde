package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.net.Uri;
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
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.Utils.Utils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.validEmail;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceRegular;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CREDITS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EMAIL;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_CODE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USERS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.VERIFIED;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHi, tvSignUp, tvName;
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    private Button btnSignUp;
    private ProgressBar loading;
    private FirebaseAuth mAuth;
    private String name, lastName, type, oabCode, oabUf, image, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getIntentBundle();
        setAnimation();

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
                    tilConfirmPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilPassword.setError(null);
                    tilConfirmPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAnimation() {
        tvHi.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvName.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvSignUp.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        tilEmail.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        tilPassword.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        tilConfirmPassword.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnSignUp.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(NAME);
            lastName = bundle.getString(LAST_NAME);
            type = bundle.getString(TYPE_REGISTER);
            oabCode = bundle.getString(OAB_CODE);
            oabUf = bundle.getString(OAB_UF);
            image = bundle.getString(IMAGE);
            tvName.setText(name);
        }
    }

    private void setTypeface() {
        tvSignUp.setTypeface(TypefaceRegular(getApplicationContext()));
        tvHi.setTypeface(TypefaceBold(getApplicationContext()));
        tvName.setTypeface(TypefaceBold(getApplicationContext()));
        etEmail.setTypeface(TypefaceLight(getApplicationContext()));
        etPassword.setTypeface(TypefaceLight(getApplicationContext()));
        etConfirmPassword.setTypeface(TypefaceLight(getApplicationContext()));
        tilConfirmPassword.setTypeface(TypefaceLight(getApplicationContext()));
        tilEmail.setTypeface(TypefaceLight(getApplicationContext()));
        tilPassword.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        tvHi = findViewById(R.id.tvHi);
        tvName = findViewById(R.id.tvName);
        tvSignUp = findViewById(R.id.tvSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        loading = findViewById(R.id.loading);
    }

    private void enableFields() {
        btnSignUp.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
        etConfirmPassword.setEnabled(true);
    }

    private void disableFields() {
        btnSignUp.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        etConfirmPassword.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSignUp:
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    tilEmail.setError(getString(R.string.fill_email));
                    etEmail.requestFocus();
                } else if (!validEmail(email)) {
                    tilEmail.setError(getString(R.string.add_valid_email));
                    etEmail.requestFocus();
                } else if (password.isEmpty()) {
                    tilPassword.setError(getString(R.string.fill_password));
                    etPassword.requestFocus();
                } else if (password.length() < 6) {
                    tilPassword.setError(getString(R.string.password_longer));
                    etPassword.requestFocus();
                } else if (confirmPassword.isEmpty()) {
                    tilConfirmPassword.setError(getString(R.string.confirm_password));
                    etConfirmPassword.requestFocus();
                } else if (!password.equals(confirmPassword)) {
                    tilPassword.setError(getString(R.string.password_not_equals));
                    tilConfirmPassword.setError(getString(R.string.password_not_equals));
                    etPassword.requestFocus();
                } else {

                    disableFields();

                    try {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    StorageReference storage = FirebaseUtils.getStorage().getReference().child(USERS).child(mAuth.getCurrentUser().getUid());
                                    storage.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            Uri download = urlTask.getResult();

                                            DatabaseReference mDatabase = FirebaseUtils.getDatabase().getReference().child(USERS).child(mAuth.getCurrentUser().getUid());
                                            HashMap<String, String> user = new HashMap<>();
                                            user.put(EMAIL, email);
                                            user.put(NAME, name);
                                            user.put(LAST_NAME, lastName);
                                            user.put(TYPE_REGISTER, type);
                                            user.put(IMAGE, download.toString());

                                            mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        if (Objects.equals(type, "0")){

                                                            DatabaseReference databaseOab = FirebaseUtils.getDatabase().getReference().child(OAB).child(mAuth.getCurrentUser().getUid());
                                                            HashMap<String, String> oab = new HashMap<>();
                                                            oab.put(OAB_CODE, oabCode);
                                                            oab.put(OAB_UF, oabUf);
                                                            oab.put(VERIFIED, "false");
                                                            databaseOab.setValue(oab).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CREDITS).child(mAuth.getCurrentUser().getUid());
                                                                        HashMap<String, String> credits = new HashMap<>();
                                                                        credits.put(CREDITS, "70");
                                                                        database.setValue(credits).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    messageSignUp(String.format("%s\n%s", getString(R.string.welcome_signup), getString(R.string.sign_up_lawyer_crecits)));
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
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    enableFields();
                                                                    SnackError(e.getMessage());
                                                                }
                                                            });

                                                        }else if (Objects.equals(type, "1")) {
                                                            messageSignUp(getString(R.string.app_name));
                                                        }

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

                                } else {
                                    enableFields();
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthUserCollisionException h) {
                                        SnackError(getString(R.string.email_already_been_used));
                                    } catch (Exception i) {
                                        SnackError(i.getMessage());
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                enableFields();
                            }
                        });
                    } catch (Exception e) {
                        enableFields();
                        SnackError(e.getMessage());
                    }

                }

                break;
        }

    }

    private void messageSignUp(String msg) {
        new AwesomeSuccessDialog(SignUpActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(msg)
                .setColoredCircle(R.color.colorGreen)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false)
                .setPositiveButtonText(getString(R.string.continu))
                .setPositiveButtonbackgroundColor(R.color.colorGreen)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent intent = new Intent(SignUpActivity.this, ConfirmEmailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    public void SnackError(String msg) {
        Snackbar.with(SignUpActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
