package com.braumsolutions.advogadoresponde.View.Profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.congfandi.simpledatepicker.Picker;
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
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.ADDRESS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CEP;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CITY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.COMPLEMENT;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CPF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DATE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.DDD;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NEIGHBORNHOOD;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NUMBER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.UF_ARRAY_FULL;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private String image, name, lastName, cpf, date, ddd, phone, cep, city, uf, address, number, neighborhood, complement;
    private CircleImageView ivImage;
    private Button btnChangeImage;
    private TextView tvProfileImage, tvPersonalData, tvPhone, tvAddress;
    private TextInputEditText etName, etLastName, etCPF, etDate, etDDD, etPhone, etCEP, etCity, etAddress, etNumber, etNeighborhood, etComplement;
    private TextInputLayout tilName, tilLastName, tilCPF, tilDate, tilDDD, tilPhone, tilCEP, tilCity, tilAddress, tilNumber, tilNeighborhood, tilComplement;
    private MaterialSpinner spUF;
    private KProgressHUD dialog;
    private boolean haveCEP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getUserData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_profile);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textWatcherEditTexts();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeImage:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
            case R.id.fbSave:
                saveData();
                break;
            case R.id.etDate:
                new Picker().show(getSupportFragmentManager(),null);
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

                createDialog(getString(R.string.please_wait), getString(R.string.saving));

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
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tilDate.setError(null);
        String dd = String.valueOf(dayOfMonth);
        String mm = String.valueOf(month + 1);
        String yy = String.valueOf(year);
        if (dayOfMonth < 10){
            dd = "0" + dd;
        }
        if (month + 1 < 10) {
            mm = "0" + mm;
        }
        date = String.format("%s/%s/%s", dd, mm, yy);
        etDate.setText(date);
    }

    private void saveData() {
        if (etName.getText().toString().trim().equals("")) {
            tilName.setError(getString(R.string.fill_name));
            etName.requestFocus();
        } else if (etLastName.getText().toString().trim().equals("")) {
            tilLastName.setError(getString(R.string.fill_last_name));
            etLastName.requestFocus();
        } else if (etCPF.getText().toString().trim().equals("")) {
            tilCPF.setError(getString(R.string.fill_cpf));
            etCPF.requestFocus();
        } else if (etDate.getText().toString().trim().equals("")) {
            tilDate.setError(getString(R.string.fill_date));
            etDate.requestFocus();
        } else if (etDDD.getText().toString().trim().equals("")) {
            tilDDD.setError(getString(R.string.fill_ddd));
            etDDD.requestFocus();
        } else if (etPhone.getText().toString().trim().equals("")) {
            tilPhone.setError(getString(R.string.fill_phone_user));
            etPhone.requestFocus();
        } else if (etCEP.getText().toString().trim().equals("")) {
            tilCEP.setError(getString(R.string.fill_cep));
            etCEP.requestFocus();
        } else if (etCity.getText().toString().trim().equals("")) {
            tilCity.setError(getString(R.string.fill_city));
            etCity.requestFocus();
        } else if (spUF.getSelectedIndex() == 0) {
            SnackWarning(getString(R.string.select_uf));
        } else if (etAddress.getText().toString().trim().equals("")) {
            tilAddress.setError(getString(R.string.fill_address));
            etAddress.requestFocus();
        } else if (etNumber.getText().toString().trim().equals("")) {
            tilNumber.setError(getString(R.string.fill_number));
            etNumber.requestFocus();
        } else if (etNeighborhood.getText().toString().trim().equals("")) {
            tilNeighborhood.setError(getString(R.string.fill_neighborhood));
            etNeighborhood.requestFocus();
        } else {

            HashMap<String, Object> u = new HashMap<>();
            u.put(NAME, etName.getText().toString().trim());
            u.put(LAST_NAME, etLastName.getText().toString().trim());
            u.put(CPF, etCPF.getText().toString().trim());
            u.put(DATE, etDate.getText().toString().trim());
            u.put(DDD, etDDD.getText().toString().trim());
            u.put(PHONE, etPhone.getText().toString().trim());
            u.put(CEP, etCEP.getText().toString().trim());
            u.put(CITY, etCity.getText().toString().trim());
            u.put(UF, String.valueOf(spUF.getSelectedIndex()));
            u.put(ADDRESS, etAddress.getText().toString().trim());
            u.put(NUMBER, etNumber.getText().toString().trim());
            u.put(NEIGHBORNHOOD, etNeighborhood.getText().toString().trim());
            u.put(COMPLEMENT, etComplement.getText().toString().trim());

            createDialog(getString(R.string.please_wait), getString(R.string.saving));

            DatabaseReference dbUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
            dbUser.updateChildren(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        SnackSuccess(getString(R.string.data_saved));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    SnackError(e.getMessage());
                }
            });

        }
    }

    private void textWatcherEditTexts() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilLastName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCPF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilCPF.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilDate.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDDD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilDDD.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCEP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilCEP.setError(null);
                }

                if (s.length() == 8) {
                    if (!haveCEP) {
                        createDialog(getString(R.string.please_wait), getString(R.string.loading));
                        try {
                            Ion.with(getApplicationContext())
                                    .load("http://viacep.com.br/ws/" + s + "/json/")
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            etCity.setText(result.get("localidade").getAsString());
                                            spUF.setSelectedIndex(indexOf(result.get("uf").getAsString(), UF_ARRAY));
                                            etNeighborhood.setText(result.get("bairro").getAsString());
                                            etAddress.setText(result.get("logradouro").getAsString());
                                            etComplement.setText(result.get("complemento").getAsString());
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SnackError(e.getMessage());
                        }

                    }
                    haveCEP = false;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilCity.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilAddress.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNeighborhood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != "") {
                    tilNeighborhood.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(EditUserProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private static int indexOf(String c, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (Objects.equals(arr[i], c)) {
                return i;
            }
        }
        return -1;
    }

    private void getUserData() {
        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        DatabaseReference databaseUser = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.child(IMAGE).getValue(String.class);
                name = dataSnapshot.child(NAME).getValue(String.class);
                lastName = dataSnapshot.child(LAST_NAME).getValue(String.class);
                cpf = dataSnapshot.child(CPF).getValue(String.class);
                date = dataSnapshot.child(DATE).getValue(String.class);
                ddd = dataSnapshot.child(DDD).getValue(String.class);
                phone = dataSnapshot.child(PHONE).getValue(String.class);
                cep = dataSnapshot.child(CEP).getValue(String.class);
                city = dataSnapshot.child(CITY).getValue(String.class);
                uf = dataSnapshot.child(UF).getValue(String.class);
                address = dataSnapshot.child(ADDRESS).getValue(String.class);
                number = dataSnapshot.child(NUMBER).getValue(String.class);
                neighborhood = dataSnapshot.child(NEIGHBORNHOOD).getValue(String.class);
                complement = dataSnapshot.child(COMPLEMENT).getValue(String.class);

                if (cep != null) {
                    haveCEP = true;
                }
                spUF.setItems(UF_ARRAY_FULL);
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.avatar).into(ivImage, null);
                etName.setText(name);
                etLastName.setText(lastName);
                etCPF.setText(cpf);
                etDate.setText(date);
                etDDD.setText(ddd);
                etPhone.setText(phone);
                etCEP.setText(cep);
                etCity.setText(city);
                etAddress.setText(address);
                etNumber.setText(number);
                etNeighborhood.setText(neighborhood);
                etComplement.setText(complement);
                if (uf != null) {
                    spUF.setSelectedIndex(Integer.parseInt(uf));
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

    private void setTypeface() {
        spUF.setTypeface(TypefaceLight(getApplicationContext()));
        btnChangeImage.setTypeface(TypefaceLight(getApplicationContext()));
        tilName.setTypeface(TypefaceLight(getApplicationContext()));
        tilLastName.setTypeface(TypefaceLight(getApplicationContext()));
        tilCPF.setTypeface(TypefaceLight(getApplicationContext()));
        tilDDD.setTypeface(TypefaceLight(getApplicationContext()));
        tilDate.setTypeface(TypefaceLight(getApplicationContext()));
        tilPhone.setTypeface(TypefaceLight(getApplicationContext()));
        tilCEP.setTypeface(TypefaceLight(getApplicationContext()));
        tilCity.setTypeface(TypefaceLight(getApplicationContext()));
        tilAddress.setTypeface(TypefaceLight(getApplicationContext()));
        tilNumber.setTypeface(TypefaceLight(getApplicationContext()));
        tilNeighborhood.setTypeface(TypefaceLight(getApplicationContext()));
        tilComplement.setTypeface(TypefaceLight(getApplicationContext()));
        etName.setTypeface(TypefaceLight(getApplicationContext()));
        etLastName.setTypeface(TypefaceLight(getApplicationContext()));
        etCPF.setTypeface(TypefaceLight(getApplicationContext()));
        etDDD.setTypeface(TypefaceLight(getApplicationContext()));
        etDate.setTypeface(TypefaceLight(getApplicationContext()));
        etPhone.setTypeface(TypefaceLight(getApplicationContext()));
        etCEP.setTypeface(TypefaceLight(getApplicationContext()));
        etCity.setTypeface(TypefaceLight(getApplicationContext()));
        etAddress.setTypeface(TypefaceLight(getApplicationContext()));
        etNumber.setTypeface(TypefaceLight(getApplicationContext()));
        etNeighborhood.setTypeface(TypefaceLight(getApplicationContext()));
        etComplement.setTypeface(TypefaceLight(getApplicationContext()));
        tvProfileImage.setTypeface(TypefaceBold(getApplicationContext()));
        tvPersonalData.setTypeface(TypefaceBold(getApplicationContext()));
        tvPhone.setTypeface(TypefaceBold(getApplicationContext()));
        tvAddress.setTypeface(TypefaceBold(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        spUF = findViewById(R.id.spUF);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        tilName = findViewById(R.id.tilName);
        tilLastName = findViewById(R.id.tilLastName);
        tilCPF = findViewById(R.id.tilCPF);
        tilDate = findViewById(R.id.tilDate);
        tilDDD = findViewById(R.id.tilDDD);
        tilPhone = findViewById(R.id.tilPhone);
        tilCEP = findViewById(R.id.tilCEP);
        tilCity = findViewById(R.id.tilCity);
        tilAddress = findViewById(R.id.tilAddress);
        tilNumber = findViewById(R.id.tilNumber);
        tilNeighborhood = findViewById(R.id.tilNeighborhood);
        tilComplement = findViewById(R.id.tilComplement);
        ivImage = findViewById(R.id.ivImage);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        tvProfileImage = findViewById(R.id.tvProfileImage);
        tvPersonalData = findViewById(R.id.tvPersonalData);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etCPF = findViewById(R.id.etCPF);
        etDate = findViewById(R.id.etDate);
        etDDD = findViewById(R.id.etDDD);
        etPhone = findViewById(R.id.etPhone);
        etCEP = findViewById(R.id.etCEP);
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etNumber = findViewById(R.id.etNumber);
        etNeighborhood = findViewById(R.id.etNeighborhood);
        etComplement = findViewById(R.id.etComplement);
        findViewById(R.id.btnChangeImage).setOnClickListener(this);
        findViewById(R.id.fbSave).setOnClickListener(this);
        findViewById(R.id.etDate).setOnClickListener(this);
    }

    public void SnackError(String msg) {
        Snackbar.with(EditUserProfileActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(EditUserProfileActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackWarning(String msg) {
        Snackbar.with(EditUserProfileActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
