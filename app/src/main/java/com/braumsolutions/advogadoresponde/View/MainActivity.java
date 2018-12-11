package com.braumsolutions.advogadoresponde.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Toolbar toolbar;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();

        mAuth = FirebaseAuth.getInstance();

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.main_menu);

    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onResume() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            logout();
        } else {
            checkConfirmationEmail();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                new AwesomeSuccessDialog(MainActivity.this)
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

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkConfirmationEmail() {
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final FirebaseUser user = mAuth.getCurrentUser();

                if (!user.isEmailVerified()) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), ConfirmEmailActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            SnackError(e.getMessage());
                        }
                    });
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    //checkCompleteProfile();
                }

            }
        });
    }

    public void SnackError(String msg) {
        Snackbar.with(MainActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.SHORT)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
