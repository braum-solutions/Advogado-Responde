package com.braumsolutions.advogadoresponde.View.Main;

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
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.View.Others.AboutActivity;
import com.braumsolutions.advogadoresponde.View.Login.ConfirmEmailActivity;
import com.braumsolutions.advogadoresponde.View.Login.LoginActivity;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Toolbar toolbar;
    private String type;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        mAuth = FirebaseAuth.getInstance();

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        //getSupportActionBar().setSubtitle(R.string.main_menu);

    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void getuUserData() {
        DatabaseReference databaseUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type = dataSnapshot.child(TYPE_REGISTER).getValue(String.class);

                if (Objects.equals(type, "0")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flMain, new LawyerFragment()).commit();
                } else if (Objects.equals(type, "1")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.flMain, new UserFragment()).commit();
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            case R.id.nav_about:
                Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentAbout);
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
                    getuUserData();
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
