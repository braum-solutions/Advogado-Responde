package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.R;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn1500;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2000;
import static com.braumsolutions.advogadoresponde.Utils.AnimationView.AnimationFadeIn2500;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CODE_PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PDF;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PICTURE;

public class FileCaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUpload, tvUploadMsg, tvMsg;
    private Button btnPdf, btnPicture, btnNext;
    private String area;
    private Uri uriPdf, uriPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_case);

        castWidets();
        setTypeface();
        setAnimation();
        getIntentBundle();

    }

    private void setAnimation() {
        tvUpload.setAnimation(AnimationFadeIn1000(getApplicationContext()));
        tvUploadMsg.setAnimation(AnimationFadeIn1500(getApplicationContext()));
        btnPdf.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnPicture.setAnimation(AnimationFadeIn2000(getApplicationContext()));
        btnNext.setAnimation(AnimationFadeIn2500(getApplicationContext()));
        tvMsg.setAnimation(AnimationFadeIn2500(getApplicationContext()));
    }

    private void setTypeface() {
        tvUpload.setTypeface(TypefaceBold(getApplicationContext()));
        tvUploadMsg.setTypeface(TypefaceLight(getApplicationContext()));
        tvMsg.setTypeface(TypefaceLight(getApplicationContext()));
        btnPicture.setTypeface(TypefaceLight(getApplicationContext()));
        btnPdf.setTypeface(TypefaceLight(getApplicationContext()));
        btnNext.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidets() {
        tvUpload = findViewById(R.id.tvDescription);
        tvUploadMsg = findViewById(R.id.tvDecriptionMsg);
        tvMsg = findViewById(R.id.tvMsg);
        btnNext = findViewById(R.id.btnComplete);
        btnPdf = findViewById(R.id.btnPdf);
        btnPicture = findViewById(R.id.btnPicture);
        findViewById(R.id.btnPdf).setOnClickListener(this);
        findViewById(R.id.btnPicture).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnComplete).setOnClickListener(this);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            area = bundle.getString(OCCUPATION_AREA);
        }
    }

    private void goNext() {
        Intent intent = new Intent(getApplicationContext(), DescriptionCaseActivity.class);
        intent.putExtra(OCCUPATION_AREA, area);
        if (uriPdf != null) {
            intent.putExtra(PDF, uriPdf.toString());
        }
        if (uriPicture != null) {
            intent.putExtra(PICTURE, uriPicture.toString());
        }
        startActivity(intent);
    }

    public void SnackWarning(String msg) {
        Snackbar.with(FileCaseActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPdf:
                Intent intentPdf = new Intent(Intent.ACTION_GET_CONTENT);
                intentPdf.setType("application/pdf");
                startActivityForResult(intentPdf, CODE_PDF);
                break;
            case R.id.btnPicture:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;
            case R.id.btnComplete:
                if (uriPdf == null && uriPicture == null) {
                    new AwesomeInfoDialog(FileCaseActivity.this)
                            .setTitle(R.string.no_files)
                            .setMessage(R.string.no_file_msg)
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
                                    goNext();
                                }
                            })
                            .show();
                } else {
                    goNext();
                }
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                uriPicture = result.getUri();
                btnPicture.setBackground(getResources().getDrawable(R.drawable.corner_button_blue));
            }
        }

        if (resultCode == RESULT_OK && requestCode == CODE_PDF && data != null) {
            uriPdf = data.getData();
            btnPdf.setBackground(getResources().getDrawable(R.drawable.corner_button_blue));
        }
    }
}
