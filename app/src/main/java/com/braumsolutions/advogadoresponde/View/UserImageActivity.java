package com.braumsolutions.advogadoresponde.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CODE_IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.REQ_CODE;

public class UserImageActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHowAbout, tvHowAboutMsg;
    private CircleImageView ivImage;
    private Button btnChoseImage, btnContinue;
    private Uri imageUri = null;
    private String name, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);

        castWidgets();
        setTypeface();
        getIntentBundle();
        setAnimation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnChoseImage:
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    imagePicker();
                }
                break;
            case R.id.btnContinue:
                if (imageUri == null) {
                    SnackWarning(getString(R.string.chose_image_continue));
                } else {
                    Intent intentImage = new Intent(getApplicationContext(), TypeRegistrationActivity.class);
                    intentImage.putExtra(NAME, name);
                    intentImage.putExtra(LAST_NAME, lastName);
                    intentImage.putExtra(IMAGE, imageUri.toString());
                    startActivity(intentImage);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                imageUri = result.getUri();
                ivImage.setImageURI(imageUri);
            }
        }
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(NAME);
            lastName = bundle.getString(LAST_NAME);
        }
    }

    private void setAnimation() {
        tvHowAbout.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvHowAboutMsg.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        ivImage.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnChoseImage.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnContinue.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    private void setTypeface() {
        tvHowAbout.setTypeface(TypefaceBold(getApplicationContext()));
        tvHowAboutMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnChoseImage.setTypeface(TypefaceLight(getApplicationContext()));
        btnContinue.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        tvHowAbout = findViewById(R.id.tvHowAbout);
        tvHowAboutMsg = findViewById(R.id.tvHowAboutMsg);
        ivImage = findViewById(R.id.ivImage);
        btnChoseImage = findViewById(R.id.btnChoseImage);
        btnContinue = findViewById(R.id.btnContinue);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnContinue).setOnClickListener(this);
        findViewById(R.id.btnChoseImage).setOnClickListener(this);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(UserImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                SnackWarning(getString(R.string.accept_permission));
                ActivityCompat.requestPermissions(UserImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE);
            } else {
                ActivityCompat.requestPermissions(UserImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE);
            }
        } else {
            imagePicker();
        }
    }

    public void SnackWarning(String msg) {
        Snackbar.with(UserImageActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(UserImageActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
