package com.braumsolutions.advogadoresponde.View.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
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

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView ivImage;
    private TextView tvDisplayName, tvEditProfile, tvEmail, tvEmailMsg, tvPhone, tvPhoneMsg, tvAddress, tvStreetNumberNeighborhood, tvCityStateMsg, tvCEP;
    private FirebaseAuth mAuth;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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
                Intent intentProfile = new Intent(getApplicationContext(), EditUserProfileActivity.class);
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
                tvDisplayName.setText(String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class)));
                tvEmailMsg.setText(mAuth.getCurrentUser().getEmail());
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

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(UserProfileActivity.this)
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
        tvEditProfile.setTypeface(TypefaceBold(getApplicationContext()));
        tvEmail.setTypeface(TypefaceBold(getApplicationContext()));
        tvEmailMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhone.setTypeface(TypefaceBold(getApplicationContext()));
        tvPhoneMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvAddress.setTypeface(TypefaceBold(getApplicationContext()));
        tvStreetNumberNeighborhood.setTypeface(TypefaceLight(getApplicationContext()));
        tvCityStateMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvCEP.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.tvEditProfile).setOnClickListener(this);
        ivImage = findViewById(R.id.ivImage);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmailMsg = findViewById(R.id.tvEmailMsg);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneMsg = findViewById(R.id.tvPhoneMsg);
        tvAddress = findViewById(R.id.tvAddress);
        tvStreetNumberNeighborhood = findViewById(R.id.tvStreetNumberNeighborhood);
        tvCityStateMsg = findViewById(R.id.tvCityStateMsg);
        tvCEP = findViewById(R.id.tvCEP);
    }

}
