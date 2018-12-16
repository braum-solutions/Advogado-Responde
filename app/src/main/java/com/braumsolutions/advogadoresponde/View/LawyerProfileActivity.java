package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.QUESTIONS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CREDITS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EMAIL;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_CODE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB_UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USERS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.VERIFIED;

public class LawyerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName, tvEmail, tvCredits, tvCreditsMsg, tvQuestion, tvQuestionMsg, tvOab;
    private CircleImageView ivImage;
    private FloatingActionButton fbChangeImage;
    private ImageView ivVerified;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_profile);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        getUserData();
        setTypeface();

    }

    private void setTypeface() {
        tvName.setTypeface(TypefaceLight(getApplicationContext()));
        tvOab.setTypeface(TypefaceLight(getApplicationContext()));
        tvCreditsMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvQuestionMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvCredits.setTypeface(TypefaceLight(getApplicationContext()));
        tvQuestion.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void getUserData() {
        DatabaseReference databaseUser = FirebaseUtils.getDatabase().getReference().child(USERS).child(mAuth.getCurrentUser().getUid());
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child(IMAGE).getValue(String.class);
                String name = dataSnapshot.child(NAME).getValue(String.class);
                String lastName = dataSnapshot.child(LAST_NAME).getValue(String.class);
                String email = dataSnapshot.child(EMAIL).getValue(String.class);
                String oab = dataSnapshot.child(OAB_CODE).getValue(String.class);
                String oabUf = dataSnapshot.child(OAB_UF).getValue(String.class);
                String verified = dataSnapshot.child(VERIFIED).getValue(String.class);

                tvEmail.setText(email);
                tvName.setText(String.format("%s %s", name, lastName));
                tvOab.setText(String.format("OAB: %s / %s", oab, UF_ARRAY[Integer.parseInt(oabUf)]));
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.avatar).into(ivImage, null);


                if (Objects.equals(verified, "true")) {
                    ivVerified.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseCredits = FirebaseUtils.getDatabase().getReference().child(CREDITS).child(mAuth.getCurrentUser().getUid());
        databaseCredits.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(CREDITS).getValue(String.class) == null) {
                    tvCredits.setText("0 CA");
                } else {
                    String credits = dataSnapshot.child(CREDITS).getValue(String.class);
                    tvCredits.setText(String.format("%s CA", credits));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseQuestions = FirebaseUtils.getDatabase().getReference().child(QUESTIONS).child(mAuth.getCurrentUser().getUid());
        databaseQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(QUESTIONS).getValue(String.class) == null) {
                    tvQuestion.setText("0");
                } else {
                    String credits = dataSnapshot.child(CREDITS).getValue(String.class);
                    tvQuestion.setText(String.format("%s", credits));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void castWidgets() {
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        ivImage = findViewById(R.id.ivImage);
        tvCredits = findViewById(R.id.tvCredits);
        tvCreditsMsg = findViewById(R.id.tvCreditsMsg);
        tvQuestion = findViewById(R.id.tvQuestions);
        tvQuestionMsg = findViewById(R.id.tvQuestionsMsg);
        tvOab = findViewById(R.id.tvOab);
        ivVerified = findViewById(R.id.ivVerified);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.fbChangeImage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.fbChangeImage:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                Uri imageUri = result.getUri();
                ivImage.setImageURI(imageUri);

                StorageReference mStorage = FirebaseUtils.getStorage().getReference().child(USERS).child(mAuth.getCurrentUser().getUid());
                mStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri download = urlTask.getResult();

                        DatabaseReference mDatabase = FirebaseUtils.getDatabase().getReference().child(USERS).child(mAuth.getCurrentUser().getUid());
                        HashMap<String, Object> user = new HashMap<>();
                        user.put(IMAGE, download.toString());

                        mDatabase.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SnackSuccess(getString(R.string.profile_pic_updated));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                SnackError(e.getMessage());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SnackError(e.getMessage());
                    }
                });

            }
        }

    }

    public void SnackError(String msg) {
        Snackbar.with(LawyerProfileActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(LawyerProfileActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}

