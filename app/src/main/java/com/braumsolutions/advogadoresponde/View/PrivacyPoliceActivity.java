package com.braumsolutions.advogadoresponde.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.braumsolutions.advogadoresponde.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class PrivacyPoliceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView web;
    private InputStreamReader inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_police);

        web = findViewById(R.id.web);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.privacy_police);
        getSupportActionBar().setSubtitle(R.string.app_name);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            inputStream = new InputStreamReader(getAssets().open("polices_br.html"));

            StringBuilder codigo = new StringBuilder();
            char[] b = new char[1024];

            while (inputStream.read(b) != -1) {
                codigo.append(b);
            }
            WebSettings webSettings = web.getSettings();
            webSettings.setDefaultTextEncodingName("utf-8");
            web.loadData(codigo.toString(), "text/html; charset=utf-8", "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
