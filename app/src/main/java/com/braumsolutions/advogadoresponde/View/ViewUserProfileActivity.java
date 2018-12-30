package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.addMask;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EMAIL;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ViewUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String image, name, lastName, email, phone, user, lawyer_name;
    private CircleImageView ivImage;
    private TextView tvName, tvEmail, tvEmailMsg, tvPhone, tvPhoneMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        mAuth = FirebaseAuth.getInstance();

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

    private void getDataUser() {
        DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.child(IMAGE).getValue(String.class);
                name = dataSnapshot.child(NAME).getValue(String.class);
                lastName = dataSnapshot.child(LAST_NAME).getValue(String.class);
                email = dataSnapshot.child(EMAIL).getValue(String.class);
                phone = dataSnapshot.child(PHONE).getValue(String.class);

                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.avatar).into(ivImage, null);
                tvName.setText(String.format("%s %s", name, lastName));
                tvEmailMsg.setText(email);
                tvPhoneMsg.setText(addMask(phone, "(##) #####-####"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mLawyer = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        mLawyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lawyer_name = dataSnapshot.child(NAME).getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTypeface() {
        tvName.setTypeface(TypefaceLight(getApplicationContext()));
        tvEmail.setTypeface(TypefaceLight(getApplicationContext()));
        tvEmailMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhone.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhoneMsg.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        tvEmail = findViewById(R.id.tvEmail);
        tvEmailMsg = findViewById(R.id.tvEmailMsg);
        tvName = findViewById(R.id.tvName);
        ivImage = findViewById(R.id.ivImage);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneMsg = findViewById(R.id.tvPhoneMsg);
        findViewById(R.id.fbWhats).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    private void dialogWhatsApp(final String phone, final String name, String last_name) {
        new AwesomeSuccessDialog(ViewUserProfileActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(String.format("%s\n%s: %s\n%s: %s", getString(R.string.whats_dialog), getString(R.string.user), String.format("%s %s", name, last_name), getString(R.string.number), addMask(phone, "(##) #####-####")))
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
                        String link = "https://api.whatsapp.com/send?phone=55" + phone + "&text=Olá%20" + name + ",%20Me%20chamo%20" + lawyer_name + ".%20Peguei%20seu%20caso%20no%20aplicativo%20" + getString(R.string.app_name) + ".%20Em%20que%20posso%20lhe%20ajudar?";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(link));
                        startActivity(i);
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbWhats:
                dialogWhatsApp(phone, name, lastName);
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
