package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PICTURE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class DescriptionCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDescription, tvDescriptionMsg, tvMsg;
    private TextInputEditText etDescription;
    private TextInputLayout tilDescription;
    private Button btnComplete;
    private ProgressBar loading;
    private String area, uriPdf, uriPicture;
    private FirebaseAuth mAuth;
    private Boolean pdfUploaded, pictureUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_case);

        mAuth = FirebaseAuth.getInstance();

        castWigets();
        setTypeface();
        setAnimation();
        getIntentBundle();

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilDescription.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAnimation() {
        tvDescription.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvDescriptionMsg.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        tilDescription.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        etDescription.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnComplete.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        tvMsg.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void setTypeface() {
        tvDescription.setTypeface(TypefaceBold(getApplicationContext()));
        tvDescriptionMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvMsg.setTypeface(TypefaceLight(getApplicationContext()));
        etDescription.setTypeface(TypefaceLight(getApplicationContext()));
        tilDescription.setTypeface(TypefaceLight(getApplicationContext()));
        btnComplete.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWigets() {
        tvDescription = findViewById(R.id.tvDescription);
        tvDescriptionMsg = findViewById(R.id.tvDecriptionMsg);
        tvMsg = findViewById(R.id.tvMsg);
        etDescription = findViewById(R.id.etDescription);
        tilDescription = findViewById(R.id.tilDescription);
        btnComplete = findViewById(R.id.btnComplete);
        loading = findViewById(R.id.loading);
        findViewById(R.id.btnComplete).setOnClickListener(this);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            area = bundle.getString(OCCUPATION_AREA);
            if (bundle.getString(PDF) != null) {
                uriPdf = bundle.getString(PDF);
            }
            if (bundle.getString(PICTURE) != null) {
                uriPicture = bundle.getString(PICTURE);
            }
        }
    }

    private void enableFields() {
        tilDescription.setEnabled(true);
        btnComplete.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

    private void disableFields() {
        tilDescription.setEnabled(false);
        btnComplete.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    public void SnackError(String msg) {
        Snackbar.with(DescriptionCaseActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    private void dialogDone() {
        new AwesomeInfoDialog(DescriptionCaseActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(R.string.case_done_msg)
                .setColoredCircle(R.color.colorGreen)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(false)
                .setPositiveButtonText(getString(R.string.continu))
                .setPositiveButtonbackgroundColor(R.color.colorGreen)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void uploadPdf(final DatabaseReference database) {

        StorageReference mStoragePdf = FirebaseUtils.getStorage().getReference().child(CASES).child(database.getKey()).child(String.format("%s.%s", database.getKey(),"pdf"));
        mStoragePdf.putFile(Uri.parse(uriPdf)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri download = urlTask.getResult();

                DatabaseReference databasePdf = FirebaseUtils.getDatabase().getReference().child(CASES).child(database.getKey());
                HashMap<String, Object> pdf = new HashMap<>();
                pdf.put(PDF, download.toString());
                databasePdf.updateChildren(pdf).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialogDone();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableFields();
                SnackError(e.getMessage());
            }
        });
    }

    private void uploadImage(final DatabaseReference database) {

        StorageReference mStorageImage = FirebaseUtils.getStorage().getReference().child(CASES).child(database.getKey()).child(String.format("%s.%s", database.getKey(),"jpeg"));
        mStorageImage.putFile(Uri.parse(uriPicture)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri download = urlTask.getResult();

                DatabaseReference databasePicture = FirebaseUtils.getDatabase().getReference().child(CASES).child(database.getKey());
                HashMap<String, Object> image = new HashMap<>();
                image.put(PICTURE, download.toString());
                databasePicture.updateChildren(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (uriPdf == null) {
                                dialogDone();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                enableFields();
                SnackError(e.getMessage());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnComplete:
                String description = etDescription.getText().toString().trim();

                if (Objects.equals(description, "")) {
                    tilDescription.setError(getString(R.string.fill_description));
                } else if (description.length() < 30) {
                    tilDescription.setError(getString(R.string.lenght_description));
                } else {

                    disableFields();

                    //Salva os dados do caso
                    final DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).push();
                    HashMap<String, String> newcase = new HashMap<>();
                    newcase.put(USER, mAuth.getCurrentUser().getUid());
                    newcase.put(OCCUPATION_AREA, area);
                    newcase.put(DESCRIPTION, description);
                    database.setValue(newcase).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (uriPicture != null) {
                                    uploadImage(database);
                                }
                                
                                if (uriPdf != null) {
                                    uploadPdf(database);
                                }

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            enableFields();
                            SnackError(e.getMessage());
                        }
                    });


                }

                break;
        }
    }

}
