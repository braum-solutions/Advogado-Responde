package com.braumsolutions.advogadoresponde.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.Model.CasesModel;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.addMask;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String image, name, lastName, email, phone;
    private CircleImageView ivImage;
    private TextView tvName, tvEmail, tvPhone, tvPhoneMsg, tvQuestions, tvQuestionsMsg;
    private int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getUserData();

    }

    private void getUserData() {
        DatabaseReference databaseUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.child(IMAGE).getValue(String.class);
                name = dataSnapshot.child(NAME).getValue(String.class);
                lastName = dataSnapshot.child(LAST_NAME).getValue(String.class);
                email = mAuth.getCurrentUser().getEmail();
                if (dataSnapshot.child(PHONE).getValue(String.class) != null) {
                    phone = dataSnapshot.child(PHONE).getValue(String.class);
                }

                if (phone == null) {
                    tvPhoneMsg.setText(getString(R.string.change_phone));
                } else {
                    tvPhoneMsg.setText(addMask(phone, "(##) #####-####"));
                }

                tvEmail.setText(email);
                tvName.setText(String.format("%s %s", name, lastName));
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.avatar).into(ivImage, null);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseQuestions = FirebaseUtils.getDatabase().getReference().child(CASES);
        databaseQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvQuestions.setText(String.valueOf(c));
                } else {
                    for (DataSnapshot cases : dataSnapshot.getChildren()) {
                        if (cases.child(USER).getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                            c += 1;
                            if (dataSnapshot.getChildrenCount() < 10) {
                                tvQuestions.setText(String.format("0%s", c));
                            } else {
                                tvQuestions.setText(String.format("%s", c));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setTypeface() {
        tvName.setTypeface(TypefaceLight(getApplicationContext()));
        tvEmail.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhone.setTypeface(TypefaceLight(getApplicationContext()));
        tvPhoneMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvQuestions.setTypeface(TypefaceLight(getApplicationContext()));
        tvQuestionsMsg.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        ivImage = findViewById(R.id.ivImage);
        tvPhone = findViewById(R.id.tvPhone);
        tvPhoneMsg = findViewById(R.id.tvPhoneMsg);
        tvQuestions = findViewById(R.id.tvQuestions);
        tvQuestionsMsg = findViewById(R.id.tvQuestionsMsg);
        findViewById(R.id.fbChangeImage).setOnClickListener(this);
        findViewById(R.id.fbChangeName).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.lvPhone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbChangeImage:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
            case R.id.fbChangeName:
                dialogEditName();
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.lvPhone:
                dialogEditPhone();
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

                StorageReference mStorage = FirebaseUtils.getStorage().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
                mStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri download = urlTask.getResult();

                        DatabaseReference mDatabase = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
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

    private void dialogEditName() {
        LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        View view = inflater.inflate(R.layout.name_dialog, null);
        builder.setTitle(getString(R.string.name_lastname));
        builder.setMessage(getString(R.string.update_name_last_name));
        builder.setView(view);
        builder.setCancelable(false);

        final TextInputEditText etName = view.findViewById(R.id.etName);
        final TextInputEditText etLastName = view.findViewById(R.id.etLastName);

        etName.setText(name);
        etLastName.setText(lastName);

        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();

                if (name.equals("")) {
                    SnackWarning(getString(R.string.fill_name));
                } else if (lastName.equals("")) {
                    SnackWarning(getString(R.string.fill_last_name));
                } else {
                    DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(NAME, name);
                    user.put(LAST_NAME, lastName);
                    database.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SnackSuccess(getString(R.string.name_updated_success));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            SnackError(e.getMessage());
                        }
                    });
                }

            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void dialogEditPhone() {
        LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);

        View view = inflater.inflate(R.layout.phone_dialog, null);
        builder.setTitle(getString(R.string.phone));
        builder.setMessage(getString(R.string.phone_msg));
        builder.setView(view);
        builder.setCancelable(false);

        final TextInputEditText etPhone = view.findViewById(R.id.etPhone);

        etPhone.setText(phone);

        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String phone = etPhone.getText().toString().trim();
                if (phone.equals("")) {
                    SnackWarning(getString(R.string.fill_phone));
                } else if (phone.length() < 11) {
                    SnackWarning(getString(R.string.incomplet_phone));
                } else {
                    DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
                    HashMap<String, Object> phones = new HashMap<>();
                    phones.put(PHONE, phone);
                    database.updateChildren(phones).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SnackSuccess(getString(R.string.phone_updated));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            SnackError(e.getMessage());
                        }
                    });

                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void SnackError(String msg) {
        Snackbar.with(UserProfileActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(UserProfileActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackWarning(String msg) {
        Snackbar.with(UserProfileActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
