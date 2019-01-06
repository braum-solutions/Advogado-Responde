package com.braumsolutions.advogadoresponde.View.Others;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.braumsolutions.advogadoresponde.BuildConfig;
import com.braumsolutions.advogadoresponde.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import static com.braumsolutions.advogadoresponde.Utils.Utils.BRAUM_SITE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.BRAUM_WHATSAPP;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        View aboutView = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.rsz_arlogo)
                .setDescription(getString(R.string.about_description))
                .addGroup(getString(R.string.contact))
                .addItem(new Element().setTitle(getString(R.string.talk_whats)).setIconDrawable(R.drawable.whatsapp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BRAUM_WHATSAPP));
                        startActivity(intent);
                    }
                }))
                .addGroup(getString(R.string.conect_with_us))
                .addEmail("braumsolutions@gmail.com")
                .addWebsite("http://www.braumsolutions.com")
                .addFacebook("braumsolutions")
                //.addYoutube("#########")
                .addPlayStore("com.braumsolutions.advogadoresponde")
                .addInstagram("braumsolutions")
                .addGroup(getString(R.string.version))
                .addItem(new Element().setTitle(versionName + " - " + String.valueOf(versionCode)).setIconDrawable(R.drawable.version))
                .addGroup(getString(R.string.developed))
                .addItem(new Element().setTitle("Braum Solutions © ").setIconDrawable(R.drawable.dev).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BRAUM_SITE));
                        startActivity(intent);
                    }
                }))
                .create();

        setContentView(R.layout.activity_about);
        LinearLayout mActivityRoot = ((LinearLayout) findViewById(R.id.main_view));
        mActivityRoot.addView(aboutView, 1);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getString(R.string.about));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
