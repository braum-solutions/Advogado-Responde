package com.braumsolutions.advogadoresponde.View;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.Model.ChatMessage;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.RECEIVER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SENDER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvChat;
    private TextView tvName;
    private EditText etMessage;
    private String user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        getIntentBundle();
        castWidgets();
        setTypeface();
        getData();

    }

    private void getData() {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(user);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvName.setText(dataSnapshot.child(NAME).getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTypeface() {
        tvName.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.fbSend).setOnClickListener(this);
        lvChat = findViewById(R.id.lvChat);
        tvName = findViewById(R.id.tvName);
        etMessage = findViewById(R.id.etMessage);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = bundle.getString(USER);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnProfile:
                profile();
                break;
            case R.id.fbSend:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.equals("")) {
            SnackWarning(getString(R.string.type_to_send));
        } else {
            DatabaseReference mSender = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user).push();

            Boolean returnSender = saveMessage(mAuth.getCurrentUser().getUid(), user, message, mSender);
            if (!returnSender) {
                SnackError(getString(R.string.fail_sent_message));
            } else {
                DatabaseReference mReceiver = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(user).child(mAuth.getCurrentUser().getUid()).push();

                Boolean returnReceiver = saveMessage(mAuth.getCurrentUser().getUid(), user, message, mReceiver);
                if (!returnReceiver) {
                    SnackError(getString(R.string.fail_sent_message));
                } else {

                    ChatMessage chat = new ChatMessage();
                    chat.setMessage(message);

                    Boolean returnSaveChatSender = saveChat(mAuth.getCurrentUser().getUid(), user, chat);
                    if (!returnSaveChatSender) {
                        SnackError(getString(R.string.error_save_chat));
                    } else {

                        etMessage.setText("");
                        etMessage.requestFocus();

                    }

                }
            }
        }
    }

    private void profile() {

    }

    private boolean saveMessage(String sender, String receiver, String message, DatabaseReference database) {
        try {
            HashMap<String, String> msg = new HashMap<>();
            //msg.put(SENDER, sender);
            //msg.put(RECEIVER, receiver);
            msg.put(MESSAGE, message);
            msg.put(KEY, database.getKey());
            database.setValue(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveChat(String sender, String receiver, ChatMessage chatMessage) {
        try {
            DatabaseReference database = FirebaseUtils.getDatabase().getInstance().getReference().child(CHAT_MESSAGES);
            database.child(sender).child(receiver).setValue(chatMessage);
            database.child(receiver).child(sender).setValue(chatMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void SnackError(String msg) {
        Snackbar.with(ChatActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackWarning(String msg) {
        Snackbar.with(ChatActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(ChatActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
