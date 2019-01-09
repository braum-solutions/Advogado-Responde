package com.braumsolutions.advogadoresponde.View.Profile;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.Locale;
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
import static com.braumsolutions.advogadoresponde.Utils.Utils.EMAIL;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
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

public class ViewLawyerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String image, display_name, email, ddd, phone, user, address, number, neighborhood, city, uf, cep, curriculum;
    private CircleImageView ivImage;
    private ImageView ivVerified;
    private TextView tvCityState, tvName, tvEmail, tvEmailMsg, tvPhone, tvPhoneMsg, tvAddress, tvStreetNumberNeighborhood, tvCityStateMsg, tvCEP, tvCurriculum, tvCurriculumMsg, tvOAB;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lawyer_profile);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();
        setTypeface();
        getIntentBundle();
        getDataUser();

    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = bundle.getString(USER);
        }
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(ViewLawyerProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void getDataUser() {
        DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.child(IMAGE).getValue(String.class);
                display_name = dataSnapshot.child(DISPLAY_NAME).getValue(String.class);
                email = dataSnapshot.child(EMAIL).getValue(String.class);
                phone = dataSnapshot.child(PHONE).getValue(String.class);
                ddd = dataSnapshot.child(DDD).getValue(String.class);
                address = dataSnapshot.child(ADDRESS).getValue(String.class);
                number = dataSnapshot.child(NUMBER).getValue(String.class);
                neighborhood = dataSnapshot.child(NEIGHBORNHOOD).getValue(String.class);
                city = dataSnapshot.child(CITY).getValue(String.class);
                uf = dataSnapshot.child(UF).getValue(String.class);
                cep = dataSnapshot.child(CEP).getValue(String.class);
                curriculum = dataSnapshot.child(CURRICULUM).getValue(String.class);

                tvStreetNumberNeighborhood.setText(String.format("%s, %s, %s", address, number, neighborhood));
                tvCityStateMsg.setText(String.format("%s - %s", city, UF_ARRAY[Integer.parseInt(uf)]));
                tvCEP.setText(cep);
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.avatar).into(ivImage, null);
                tvName.setText(display_name);
                tvEmailMsg.setText(email);
                tvPhoneMsg.setText(addMask(ddd + phone, "(##) #####-####"));
                tvCurriculumMsg.setText(curriculum);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseOab = FirebaseUtils.getDatabase().getReference().child(OAB).child(user);
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

    private void setTypeface() {
        tvName.setTypeface(TypefaceBold(getApplicationContext()));
        tvOAB.setTypeface(TypefaceLight(getApplicationContext()));
        tvEmail.setTypeface(TypefaceBold(getApplicationContext()));
        tvEmailMsg.setTypeface(TypefaceLight(getApplicationContext()));
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
        ivVerified = findViewById(R.id.ivVerified);
        tvOAB = findViewById(R.id.tvOAB);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmailMsg = findViewById(R.id.tvEmailMsg);
        tvName = findViewById(R.id.tvName);
        ivImage = findViewById(R.id.ivImage);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneMsg = findViewById(R.id.tvPhoneMsg);
        tvAddress = findViewById(R.id.tvAddress);
        tvStreetNumberNeighborhood = findViewById(R.id.tvStreetNumberNeighborhood);
        tvCityStateMsg = findViewById(R.id.tvCityStateMsg);
        tvCEP = findViewById(R.id.tvCEP);
        tvCurriculum = findViewById(R.id.tvCurriculum);
        tvCurriculumMsg = findViewById(R.id.tvCurriculumMsg);
        findViewById(R.id.fbWhats).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbWhats:
                //dialogWhatsApp(phone, ddd, display_name, lastName);
                SnackSuccess(getString(R.string.soon));
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(ViewLawyerProfileActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
