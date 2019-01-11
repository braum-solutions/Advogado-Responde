package com.braumsolutions.advogadoresponde.View.NewCase;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.View.Main.MainActivity;
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
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EDIT;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
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
    private String key, area, image, pdf, description, edit;
    private FirebaseAuth mAuth;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_case);

        mAuth = FirebaseAuth.getInstance();

        castWigets();
        setTypeface();
        setAnimation();
        getIntentBundle();

        if (Objects.equals(edit, "true")) {
            etDescription.setText(description);
            btnComplete.setText(getString(R.string.save_edition));
        }

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
        tvDescription = findViewById(R.id.tvComment);
        tvDescriptionMsg = findViewById(R.id.tvDecriptionMsg);
        tvMsg = findViewById(R.id.tvMsg);
        etDescription = findViewById(R.id.etDescription);
        tilDescription = findViewById(R.id.tilDescription);
        btnComplete = findViewById(R.id.btnComplete);
        loading = findViewById(R.id.loading);
        findViewById(R.id.btnComplete).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(KEY) != null) {
                key = bundle.getString(KEY);
            }
            if (bundle.getString(OCCUPATION_AREA) != null) {
                area = bundle.getString(OCCUPATION_AREA);
            }
            if (bundle.getString(PICTURE) != null) {
                image = bundle.getString(PICTURE);
            }
            if (bundle.getString(PDF) != null) {
                pdf = bundle.getString(PDF);
            }
            if (bundle.getString(DESCRIPTION) != null) {
                description = bundle.getString(DESCRIPTION);
            }
            if (bundle.getString(EDIT) != null){
                edit = bundle.getString(EDIT);
            }
        }
    }

    private void enableFields() {
        tilDescription.setEnabled(true);
        loading.setVisibility(View.GONE);
    }

    private void disableFields() {
        tilDescription.setEnabled(false);
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
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
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

    private void dialogEditDone() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        new AwesomeInfoDialog(DescriptionCaseActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(R.string.case_edited)
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

        StorageReference mStoragePdf = FirebaseUtils.getStorage().getReference().child(CASES).child(database.getKey()).child(String.format("%s.%s", database.getKey(), "pdf"));
        mStoragePdf.putFile(Uri.parse(pdf)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                enableFields();
                SnackError(e.getMessage());
            }
        });
    }

    private void uploadPdf(final String key) {
        if (pdf.substring(0, 38).equals("https://firebasestorage.googleapis.com")) {
            dialogEditDone();
        } else {
            StorageReference mStoragePdf = FirebaseUtils.getStorage().getReference().child(CASES).child(key).child(String.format("%s.%s", key, "pdf"));
            mStoragePdf.putFile(Uri.parse(pdf)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri download = urlTask.getResult();

                    DatabaseReference databasePdf = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
                    HashMap<String, Object> pdf = new HashMap<>();
                    pdf.put(PDF, download.toString());
                    databasePdf.updateChildren(pdf).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialogEditDone();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            enableFields();
                            SnackError(e.getMessage());
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    enableFields();
                    SnackError(e.getMessage());
                }
            });
        }
    }

    private void uploadImage(final DatabaseReference database) {

        StorageReference mStorageImage = FirebaseUtils.getStorage().getReference().child(CASES).child(database.getKey()).child(String.format("%s.%s", database.getKey(), "jpeg"));
        mStorageImage.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                            if (pdf == null) {
                                dialogDone();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        enableFields();
                        SnackError(e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                enableFields();
                SnackError(e.getMessage());
            }
        });

    }

    private void uploadImage(final String key) {
        if (image.substring(0, 38).equals("https://firebasestorage.googleapis.com")) {
            dialogEditDone();
        } else {
            StorageReference mStorageImage = FirebaseUtils.getStorage().getReference().child(CASES).child(key).child(String.format("%s.%s", key, "jpeg"));
            mStorageImage.putFile(Uri.parse(image)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri download = urlTask.getResult();

                    DatabaseReference databasePicture = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
                    HashMap<String, Object> image = new HashMap<>();
                    image.put(PICTURE, download.toString());
                    databasePicture.updateChildren(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (pdf == null) {
                                    dialogEditDone();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            enableFields();
                            SnackError(e.getMessage());
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    enableFields();
                    SnackError(e.getMessage());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(DescriptionCaseActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
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
                    createDialog(getString(R.string.please_wait), getString(R.string.saving));

                    //Salva os dados do caso
                    if (Objects.equals(edit, "true")) {
                        Toast.makeText(getApplicationContext(), "EDIT", Toast.LENGTH_LONG).show();

                        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
                        HashMap<String, Object> editCase = new HashMap<>();
                        editCase.put(OCCUPATION_AREA, area);
                        editCase.put(DESCRIPTION, description);
                        database.updateChildren(editCase).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (pdf == null && image == null) {
                                        dialogEditDone();
                                    }

                                    if (image != null) {
                                        uploadImage(key);
                                    }

                                    if (pdf != null) {
                                        uploadPdf(key);
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

                    } else {
                        Toast.makeText(getApplicationContext(), "NEW", Toast.LENGTH_LONG).show();

                        final DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).push();
                        HashMap<String, String> newcase = new HashMap<>();
                        newcase.put(USER, mAuth.getCurrentUser().getUid());
                        newcase.put(OCCUPATION_AREA, area);
                        newcase.put(DESCRIPTION, description);
                        newcase.put(KEY, database.getKey());
                        database.setValue(newcase).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (pdf == null && image == null) {
                                        dialogDone();
                                    }

                                    if (image != null) {
                                        uploadImage(database);
                                    }

                                    if (pdf != null) {
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

                }

                break;
        }
    }

}
