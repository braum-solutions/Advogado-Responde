package com.braumsolutions.advogadoresponde.View;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class OpenCaseActivity extends AppCompatActivity {

    private TextView tvUser, tvUserMsg, tvOccupation, tvOccupationMsg, tvImage, tvImageMsg, tvPdf, tvPdfMsg, tvDescription, tvDescriptionMsg;
    private Toolbar toolbar;
    private ProgressDialog dialog;
    private String key, area, image, pdf, description, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_case);

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
        mCase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                description = dataSnapshot.child(DESCRIPTION).getValue(String.class);
                area = dataSnapshot.child(OCCUPATION_AREA).getValue(String.class);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
}
