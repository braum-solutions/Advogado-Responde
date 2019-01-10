package com.braumsolutions.advogadoresponde.View.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.View.Login.ConfirmEmailActivity;
import com.braumsolutions.advogadoresponde.View.Login.SignUpActivity;
import com.braumsolutions.advogadoresponde.View.Main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.addMask;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.ADDRESS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CEP;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CITY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CURRICULUM;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DDD;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DISPLAY_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NEIGHBORNHOOD;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NUMBER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_CODE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY_OAB;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.VERIFIED;

public class LawyerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView ivImage;
    private ImageView ivVerified;
    private TextView tvDisplayName, tvOAB, tvEditProfile, tvName, tvNameMsg, tvPhone, tvPhoneMsg, tvAddress, tvStreetNumberNeighborhood, tvCityStateMsg, tvCEP, tvCurriculum, tvCurriculumMsg;
    private FirebaseAuth mAuth;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_profile);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        createDialog(getString(R.string.please_wait), getString(R.string.loading));
        getUserData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvEditProfile:
                Intent intentProfile = new Intent(getApplicationContext(), EditLawyerProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    private void getUserData() {
        DatabaseReference databaseUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.with(getApplicationContext()).load(dataSnapshot.child(IMAGE).getValue(String.class)).placeholder(R.drawable.avatar).into(ivImage, null);
                if (dataSnapshot.child(DISPLAY_NAME).getValue(String.class) == null) {
                    tvDisplayName.setText(String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class)));
                } else {
                    tvDisplayName.setText(dataSnapshot.child(DISPLAY_NAME).getValue(String.class));
                }
                tvNameMsg.setText(String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class)));
                if (dataSnapshot.child(PHONE).getValue(String.class) == null) {
                    tvPhoneMsg.setText(getString(R.string.no_phone));
                } else {
                    tvPhoneMsg.setText(addMask(dataSnapshot.child(DDD).getValue(String.class) + dataSnapshot.child(PHONE).getValue(String.class), "(##) #####-####"));
                }

                if (dataSnapshot.child(CEP).getValue(String.class) == null) {
                    tvStreetNumberNeighborhood.setText(getString(R.string.no_address));
                    tvCityStateMsg.setVisibility(View.GONE);
                    tvCEP.setVisibility(View.GONE);
                } else {
                    tvCityStateMsg.setVisibility(View.VISIBLE);
                    tvCEP.setVisibility(View.VISIBLE);
                    tvStreetNumberNeighborhood.setText(String.format("%s, %s, %s", dataSnapshot.child(ADDRESS).getValue(String.class), dataSnapshot.child(NUMBER).getValue(String.class), dataSnapshot.child(NEIGHBORNHOOD).getValue(String.class)));
                    tvCityStateMsg.setText(String.format("%s - %s", dataSnapshot.child(CITY).getValue(String.class), UF_ARRAY[Integer.parseInt(dataSnapshot.child(UF).getValue(String.class))]));
                    tvCEP.setText(dataSnapshot.child(CEP).getValue(String.class));
                }

                if (dataSnapshot.child(CURRICULUM).getValue(String.class) == null) {
                    tvCurriculumMsg.setText(getString(R.string.no_data_curriculum));
                } else {
                    tvCurriculumMsg.setText(dataSnapshot.child(CURRICULUM).getValue(String.class));
                }


                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseOab = FirebaseUtils.getDatabase().getReference().child(OAB).child(mAuth.getCurrentUser().getUid());
        databaseOab.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvOAB.setText(String.format("%s - %s", dataSnapshot.child(OAB_CODE).getValue(String.class), UF_ARRAY_OAB[Integer.parseInt(dataSnapshot.child(OAB_UF).getValue(String.class))]));

                if (Objects.equals(dataSnapshot.child(VERIFIED).getValue(String.class), "true")) {
                    ivVerified.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(LawyerProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void setTypeface() {
        tvDisplayName.setTypeface(TypefaceLight(getApplicationContext()));
        tvOAB.setTypeface(TypefaceLight(getApplicationContext()));
        tvEditProfile.setTypeface(TypefaceBold(getApplicationContext()));
        tvName.setTypeface(TypefaceBold(getApplicationContext()));
        tvNameMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhone.setTypeface(TypefaceBold(getApplicationContext()));
        tvPhoneMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvAddress.setTypeface(TypefaceBold(getApplicationContext()));
        tvStreetNumberNeighborhood.setTypeface(TypefaceLight(getApplicationContext()));
        tvCityStateMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvCEP.setTypeface(TypefaceLight(getApplicationContext()));
        tvCurriculum.setTypeface(TypefaceBold(getApplicationContext()));
        tvCurriculumMsg.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.tvEditProfile).setOnClickListener(this);
        ivImage = findViewById(R.id.ivImage);
        ivVerified = findViewById(R.id.ivVerified);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvOAB = findViewById(R.id.tvOAB);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvName = findViewById(R.id.tvName);
        tvNameMsg = findViewById(R.id.tvNameMsg);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneMsg = findViewById(R.id.tvPhoneMsg);
        tvAddress = findViewById(R.id.tvAddress);
        tvStreetNumberNeighborhood = findViewById(R.id.tvStreetNumberNeighborhood);
        tvCityStateMsg = findViewById(R.id.tvCityStateMsg);
        tvCEP = findViewById(R.id.tvCEP);
        tvCurriculum = findViewById(R.id.tvCurriculum);
        tvCurriculumMsg = findViewById(R.id.tvCurriculumMsg);
    }

}
