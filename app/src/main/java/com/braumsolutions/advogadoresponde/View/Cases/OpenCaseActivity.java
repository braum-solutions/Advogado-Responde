package com.braumsolutions.advogadoresponde.View.Cases;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.View.Chat.ChatActivity;
import com.braumsolutions.advogadoresponde.View.NewCase.OccupationAreaCaseActivity;
import com.braumsolutions.advogadoresponde.View.Profile.ViewUserProfileActivity;
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
import com.himangi.imagepreview.ImagePreviewActivity;
import com.himangi.imagepreview.PreviewFile;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CATCH_CASE_VALUE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CREDITS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DESCRIPTION;
import static com.braumsolutions.advogadoresponde.Utils.Utils.EDIT;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_A;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_B;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_C;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_BR;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY_EN;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PICTURE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class OpenCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUser, tvUserMsg, tvOccupation, tvOccupationMsg, tvImage, tvImageMsg, tvPdf, tvPdfMsg, tvDescription, tvDescriptionMsg, tvLawyer, tvLawyerMsg;
    private Toolbar toolbar;
    private String key, area, image, pdf, description, user, lawyer_a, lawyer_b, lawyer_c, name, last_name, phone, lawyer_name, type;
    private int credits, lawyer = 0;
    private Button btnGetCase;
    private FirebaseAuth mAuth;
    private FloatingActionButton fbChat;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_case);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();
        setTypeface();
        getIntentBundle();
        getCaseData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.user_case);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (type.equals("1")) {
            btnGetCase.setVisibility(View.GONE);
        }

    }

    private void editCase() {
        Intent intent = new Intent(getApplicationContext(), OccupationAreaCaseActivity.class);
        intent.putExtra(KEY, key);
        intent.putExtra(OCCUPATION_AREA, area);
        intent.putExtra(PICTURE, image);
        intent.putExtra(PDF, pdf);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(EDIT, true);
        startActivity(intent);
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(OpenCaseActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void getCaseData() {
        dialog.show();

        DatabaseReference mCase = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
        mCase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
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
                if (dataSnapshot.child(PICTURE).getValue(String.class) != null) {
                    image = dataSnapshot.child(PICTURE).getValue(String.class);
                } else {
                    if (Objects.equals(type, "1")) {
                        tvImage.setText(R.string.no_file);
                    }
                }
                if (dataSnapshot.child(PDF).getValue(String.class) != null) {
                    pdf = dataSnapshot.child(PDF).getValue(String.class);
                } else {
                    if (Objects.equals(type, "1")) {
                        tvPdf.setText(R.string.no_file);
                    }
                }

                if (Objects.equals(Locale.getDefault().getDisplayLanguage(), "English")) {
                    tvOccupationMsg.setText(OCCUPATION_AREA_ARRAY_EN[Integer.parseInt(area)]);
                } else {
                    tvOccupationMsg.setText(OCCUPATION_AREA_ARRAY_BR[Integer.parseInt(area)]);
                }

                tvDescriptionMsg.setText(description);
                tvLawyerMsg.setText(String.format("%s/3", lawyer));

                if (type.equals("0")) {
                    if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                        CheckIfHaveImageFile();
                    } else {
                        tvPdfMsg.setText(getString(R.string.free_link));
                        tvImageMsg.setText(getString(R.string.free_link));
                    }
                } else {
                    CheckIfHaveImageFile();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (type.equals("1")) {
            DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
            mUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child(NAME).getValue(String.class);
                    last_name = dataSnapshot.child(LAST_NAME).getValue(String.class);
                    tvUserMsg.setText(String.format("%s %s", name, last_name));
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
            mUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    phone = dataSnapshot.child(PHONE).getValue(String.class);
                    name = dataSnapshot.child(NAME).getValue(String.class);
                    last_name = dataSnapshot.child(LAST_NAME).getValue(String.class);
                    tvUserMsg.setText(String.format("%s %s", name, last_name));

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

            DatabaseReference mLawyer = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
            mLawyer.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lawyer_name = dataSnapshot.child(NAME).getValue(String.class);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString(KEY);
            user = bundle.getString(USER);
            type = bundle.getString(TYPE_REGISTER);
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
        tvDescription = findViewById(R.id.tvComment);
        tvDescriptionMsg = findViewById(R.id.tvDescriptionMsg);
        btnGetCase = findViewById(R.id.btnGetCase);
        tvLawyer = findViewById(R.id.tvLawyer);
        tvLawyerMsg = findViewById(R.id.tvLawyerMsg);
        findViewById(R.id.btnGetCase).setOnClickListener(this);
        findViewById(R.id.tvPdfMsg).setOnClickListener(this);
        findViewById(R.id.tvImageMsg).setOnClickListener(this);
        findViewById(R.id.tvUserMsg).setOnClickListener(this);
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

    public void SnackWarning(String msg) {
        Snackbar.with(OpenCaseActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(OpenCaseActivity.this, null)
                .type(Type.SUCCESS)
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
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CheckIfHaveImageFile();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SnackError(e.getMessage());
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void CheckIfHaveImageFile() {
        btnGetCase.setVisibility(View.GONE);

        if (pdf == null) {
            tvPdfMsg.setText(getString(R.string.no_file));
        } else {
            tvPdfMsg.setText(getString(R.string.click_to_view));
        }

        if (image == null) {
            tvImageMsg.setText(getString(R.string.no_file));
        } else {
            tvImageMsg.setText(getString(R.string.click_to_view));
        }
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

    private void catchCase() {
        if (lawyer_a != null && lawyer_b != null && lawyer_c != null) {
            SnackError(getString(R.string.limit_lawyer));
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

                            if (pdf == null) {
                                tvPdfMsg.setText(getString(R.string.no_file));
                            } else {
                                tvPdfMsg.setText(getString(R.string.click_to_view));
                            }

                            if (image == null) {
                                tvImageMsg.setText(getString(R.string.no_file));
                            } else {
                                tvImageMsg.setText(getString(R.string.click_to_view));
                            }

                            addToMyCases();

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
    }

    private void addToMyCases() {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(LAWYER_CASES).child(mAuth.getCurrentUser().getUid()).child(key);
        HashMap<String, String> cases = new HashMap<>();
        cases.put(CASES, key);
        database.setValue(cases).addOnFailureListener(new OnFailureListener() {
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
                catchCase();
                break;
            case R.id.tvPdfMsg:
                if (Objects.equals(type, "1")) {
                    openPDF();
                } else {
                    if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                        openPDF();
                    }
                }
                break;
            case R.id.tvImageMsg:
                if (Objects.equals(type, "1")) {
                    openImage();
                } else {
                    if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                        openImage();
                    }
                }
                break;
            case R.id.tvUserMsg:
                if (Objects.equals(type, "0")) {
                    if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid()) || Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                        Intent intent = new Intent(getApplicationContext(), ViewUserProfileActivity.class);
                        intent.putExtra(USER, user);
                        startActivity(intent);
                    } else {
                        SnackWarning(getString(R.string.get_case_to_see_profile));
                    }
                }
                break;
        }
    }

    private void openImage() {
        if (image != null) {
            try {
                final ArrayList<PreviewFile> previewFiles = new ArrayList<>();
                previewFiles.add(new PreviewFile(image, String.format("%s: %s %s", getString(R.string.user), name, last_name)));
                Intent intent = new Intent(OpenCaseActivity.this, ImagePreviewActivity.class);
                intent.putExtra(ImagePreviewActivity.IMAGE_LIST, previewFiles);
                intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0);
                startActivity(intent);
            } catch (Exception e) {
                SnackError(e.getMessage());
            }
        }
    }

    private void openPDF() {
        if (pdf != null) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(pdf), "application/pdf");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } catch (Exception e) {
                SnackError(e.getMessage());
            }
        }
    }

    private void openChat() {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        DatabaseReference mCase = FirebaseUtils.getDatabase().getReference().child(CASES).child(key);
        mCase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(type, "1")) {
                    getMenuInflater().inflate(R.menu.menu_case_user, menu);
                } else {
                    if (Objects.equals(dataSnapshot.child(LAWYER_A).getValue(String.class), mAuth.getCurrentUser().getUid()) || Objects.equals(dataSnapshot.child(LAWYER_B).getValue(String.class), mAuth.getCurrentUser().getUid()) || Objects.equals(dataSnapshot.child(LAWYER_C).getValue(String.class), mAuth.getCurrentUser().getUid())) {
                        getMenuInflater().inflate(R.menu.menu_case_lawyer, menu);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_edit_case:
                editCase();
                break;
            case R.id.nav_chat:
                openChat();
                break;
            case R.id.nav_report_case:
                Intent intent = new Intent(getApplicationContext(), ReportCaseActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_leave_case:
                new AwesomeInfoDialog(OpenCaseActivity.this)
                        .setTitle(getString(R.string.leave_case))
                        .setMessage(R.string.leave_case_msg)
                        .setColoredCircle(R.color.colorYellow)
                        .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                        .setCancelable(false)
                        .setNegativeButtonText(getString(R.string.cancel))
                        .setNegativeButtonbackgroundColor(R.color.colorYellow)
                        .setNegativeButtonTextColor(R.color.white)
                        .setNegativeButtonClick(new Closure() {
                            @Override
                            public void exec() {

                            }
                        })
                        .setPositiveButtonText(getString(R.string.continu))
                        .setPositiveButtonbackgroundColor(R.color.colorYellow)
                        .setPositiveButtonTextColor(R.color.white)
                        .setPositiveButtonClick(new Closure() {
                            @Override
                            public void exec() {

                                if (Objects.equals(lawyer_a, mAuth.getCurrentUser().getUid())) {
                                    leaveCase(LAWYER_A);
                                } else if (Objects.equals(lawyer_b, mAuth.getCurrentUser().getUid())) {
                                    leaveCase(LAWYER_B);
                                } else if (Objects.equals(lawyer_c, mAuth.getCurrentUser().getUid())) {
                                    leaveCase(LAWYER_C);
                                }


                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void leaveCase(String lawyer) {
        SnackSuccess(getString(R.string.leave_case_wait));

        DatabaseReference mCase = FirebaseUtils.getDatabase().getReference().child(CASES).child(key).child(lawyer);
        mCase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference mCaseLawyer = FirebaseUtils.getDatabase().getReference().child(LAWYER_CASES).child(mAuth.getCurrentUser().getUid()).child(key);
                    mCaseLawyer.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                DatabaseReference mChat = FirebaseUtils.getDatabase().getReference().child(CHAT_MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user);
                                mChat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            DatabaseReference mMessages = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user);
                                            mMessages.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        finish();

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
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        SnackError(e.getMessage());
                                    }
                                });

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SnackError(e.getMessage());
            }
        });
    }

}
