package com.braumsolutions.advogadoresponde.View.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.View.Main.MainActivity;
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
    private Button btnResend, btnIncorrectEmail;
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
        getSupportActionBar().setSubtitle(mAuth.getCurrentUser().getEmail());

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
                                        SnackSuccess(getString(R.string.email_sended));
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
            case R.id.btnIncorrectEmail:
                SnackSuccess(getString(R.string.soon));
                /* new AwesomeSuccessDialog(ConfirmEmailActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.incorrect_email_msg)
                        .setColoredCircle(R.color.colorYellow)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setCancelable(false)
                        .setNegativeButtonText(getString(R.string.cancel))
                        .setNegativeButtonbackgroundColor(R.color.colorYellow)
                        .setNegativeButtonTextColor(R.color.white)
                        .setNegativeButtonClick(new Closure() {
                            @Override
                            public void exec() {

                            }
                        })
                        .setPositiveButtonText(getString(R.string.update))
                        .setPositiveButtonbackgroundColor(R.color.colorYellow)
                        .setPositiveButtonTextColor(R.color.white)
                        .setPositiveButtonClick(new Closure() {
                            @Override
                            public void exec() {

                            }
                        })
                        .show(); */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                new AwesomeSuccessDialog(ConfirmEmailActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.logout_msg)
                        .setColoredCircle(R.color.colorYellow)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setCancelable(false)
                        .setNegativeButtonText(getString(R.string.cancel))
                        .setNegativeButtonbackgroundColor(R.color.colorYellow)
                        .setNegativeButtonTextColor(R.color.white)
                        .setNegativeButtonClick(new Closure() {
                            @Override
                            public void exec() {

                            }
                        })
                        .setPositiveButtonText(getString(R.string.logout))
                        .setPositiveButtonbackgroundColor(R.color.colorYellow)
                        .setPositiveButtonTextColor(R.color.white)
                        .setPositiveButtonClick(new Closure() {
                            @Override
                            public void exec() {
                                mAuth.signOut();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        btnIncorrectEmail.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        btnResend = findViewById(R.id.btnResend);
        btnIncorrectEmail = findViewById(R.id.btnIncorrectEmail);
        tvMsg = findViewById(R.id.tvMsg);
        tvTitle = findViewById(R.id.tvTitle);
        loading = findViewById(R.id.loading);
        findViewById(R.id.btnResend).setOnClickListener(this);
        findViewById(R.id.btnIncorrectEmail).setOnClickListener(this);
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
