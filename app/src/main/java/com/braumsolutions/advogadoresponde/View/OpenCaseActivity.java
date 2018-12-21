package com.braumsolutions.advogadoresponde.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CATCH_CASE_VALUE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CREDITS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_A;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_B;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_C;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class OpenCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUser, tvUserMsg, tvOccupation, tvOccupationMsg, tvImage, tvImageMsg, tvPdf, tvPdfMsg, tvDescription, tvDescriptionMsg, tvLawyer, tvLawyerMsg;
    private Toolbar toolbar;
    private ProgressDialog dialog;
    private String key, area, image, pdf, description, user, lawyer_a, lawyer_b, lawyer_c;
    private int credits, lawyer = 0;
    private Button btnGetCase;
    private FirebaseAuth mAuth;
    private CardView cvComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_case);

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(OpenCaseActivity.this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);

        castWidgets();
        setTypeface();
        getIntentBundle();
        getCaseData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.user_case);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getCaseData() {
        dialog.show();

        DatabaseReference mCase = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
        mCase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                description = dataSnapshot.child(DESCRIPTION).getValue(String.class);
                area = dataSnapshot.child(OCCUPATION_AREA).getValue(String.class);
                if (dataSnapshot.child(LAWYER_A).getValue(String.class) != null) {
                    lawyer_a = dataSnapshot.child(LAWYER_A).getValue(String.class);
                    lawyer = 1;
                }
                if (dataSnapshot.child(LAWYER_B).getValue(String.class) != null) {
                    lawyer_b = dataSnapshot.child(LAWYER_B).getValue(String.class);
                    lawyer = 2;
                }
                if (dataSnapshot.child(LAWYER_C).getValue(String.class) != null) {
                    lawyer_c = dataSnapshot.child(LAWYER_C).getValue(String.class);
                    lawyer = 3;
                }
                if (dataSnapshot.child(IMAGE).getValue(String.class) == null) {
                    tvImageMsg.setText(getString(R.string.no_file));
                } else {
                    image = dataSnapshot.child(IMAGE).getValue(String.class);
                }
                if (dataSnapshot.child(PDF).getValue(String.class) == null) {
                    tvPdfMsg.setText(getString(R.string.no_file));
                } else {
                    pdf = dataSnapshot.child(PDF).getValue(String.class);
                }

                tvOccupationMsg.setText(OCCUPATION_AREA_ARRAY[Integer.parseInt(area)]);
                tvDescriptionMsg.setText(description);
                tvLawyerMsg.setText(String.format("%s/3", lawyer));

                if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                    btnGetCase.setVisibility(View.GONE);
                    cvComments.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvUserMsg.setText(String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class)));
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mCredits = FirebaseUtils.getDatabase().getReference().child(CREDITS).child(mAuth.getCurrentUser().getUid());
        mCredits.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(CREDITS).getValue(String.class) != null) {
                    credits = Integer.parseInt(dataSnapshot.child(CREDITS).getValue(String.class));
                } else {
                    credits = 0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString(KEY);
            user = bundle.getString(USER);
        }
    }

    private void setTypeface() {
        tvUser.setTypeface(TypefaceBold(getApplicationContext()));
        tvOccupation.setTypeface(TypefaceBold(getApplicationContext()));
        tvImage.setTypeface(TypefaceBold(getApplicationContext()));
        tvPdf.setTypeface(TypefaceBold(getApplicationContext()));
        tvDescription.setTypeface(TypefaceBold(getApplicationContext()));
        tvUserMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvOccupationMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvImageMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvPdfMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvDescriptionMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvLawyer.setTypeface(TypefaceBold(getApplicationContext()));
        tvLawyerMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnGetCase.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        tvUser = findViewById(R.id.tvUser);
        tvUserMsg = findViewById(R.id.tvUserMsg);
        tvOccupation = findViewById(R.id.tvOccupation);
        tvOccupationMsg = findViewById(R.id.tvOccupationMsg);
        tvImage = findViewById(R.id.tvImage);
        tvImageMsg = findViewById(R.id.tvImageMsg);
        tvPdf = findViewById(R.id.tvPdf);
        tvPdfMsg = findViewById(R.id.tvPdfMsg);
        tvDescription = findViewById(R.id.tvDescription);
        tvDescriptionMsg = findViewById(R.id.tvDescriptionMsg);
        btnGetCase = findViewById(R.id.btnGetCase);
        tvLawyer = findViewById(R.id.tvLawyer);
        cvComments = findViewById(R.id.cvComments);
        tvLawyerMsg = findViewById(R.id.tvLawyerMsg);
        findViewById(R.id.btnGetCase).setOnClickListener(this);
    }

    public void SnackError(String msg) {
        Snackbar.with(OpenCaseActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    private void addLawyerToCase(String lawyer) {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
        HashMap<String, Object> lawyers = new HashMap<>();
        lawyers.put(lawyer, mAuth.getCurrentUser().getUid());
        database.updateChildren(lawyers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    btnGetCase.setVisibility(View.GONE);
                    cvComments.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SnackError(e.getMessage());
            }
        });

    }

    private void DiscontCA(int value) {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CREDITS).child(mAuth.getCurrentUser().getUid());
        HashMap<String, Object> credits = new HashMap<>();
        credits.put(CREDITS, String.valueOf(value));
        database.updateChildren(credits).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SnackError(e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetCase:
                if (lawyer_a != null && lawyer_b != null && lawyer_c != null) {
                    SnackError("Este caso ja chegou no limite de Advogados!");
                } else if (credits > 30) {
                    new AwesomeSuccessDialog(OpenCaseActivity.this)
                            .setTitle(getString(R.string.get_case))
                            .setMessage(R.string.get_case_msg)
                            .setColoredCircle(R.color.colorAccent)
                            .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                            .setCancelable(false)
                            .setNegativeButtonText(getString(R.string.cancel))
                            .setNegativeButtonbackgroundColor(R.color.colorAccent)
                            .setNegativeButtonTextColor(R.color.white)
                            .setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {

                                }
                            })
                            .setPositiveButtonText(getString(R.string.continu))
                            .setPositiveButtonbackgroundColor(R.color.colorAccent)
                            .setPositiveButtonTextColor(R.color.white)
                            .setPositiveButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    DiscontCA(credits - CATCH_CASE_VALUE);
                                    if (lawyer_a == null) {
                                        addLawyerToCase(LAWYER_A);
                                    } else if (lawyer_b == null) {
                                        addLawyerToCase(LAWYER_B);
                                    } else {
                                        addLawyerToCase(LAWYER_C);
                                    }
                                }
                            })
                            .show();
                } else {
                    new AwesomeSuccessDialog(OpenCaseActivity.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage(R.string.insufficient_ca)
                            .setColoredCircle(R.color.colorAccent)
                            .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                            .setCancelable(false)
                            .setPositiveButtonText(getString(R.string.ok))
                            .setPositiveButtonbackgroundColor(R.color.colorAccent)
                            .setPositiveButtonTextColor(R.color.white)
                            .setPositiveButtonClick(new Closure() {
                                @Override
                                public void exec() {

                                }
                            })
                            .show();
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_case, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_report_case:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
