package com.braumsolutions.advogadoresponde.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.braumsolutions.advogadoresponde.R;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.chat));
        getSupportActionBar().setSubtitle(getString(R.string.chat_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
    }
}
