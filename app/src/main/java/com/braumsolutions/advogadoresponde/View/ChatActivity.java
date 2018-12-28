package com.braumsolutions.advogadoresponde.View;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.Model.ChatMessage;
import com.braumsolutions.advogadoresponde.Model.Message;
import com.braumsolutions.advogadoresponde.Model.MessageAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.RECEIVER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SENDER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvChat;
    private TextView tvName, tvNoMessage;
    private EditText etMessage;
    private String user;
    private FirebaseAuth mAuth;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;
    private ValueEventListener valueEventListenerMessages;
    private  DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        getIntentBundle();
        castWidgets();
        setTypeface();
        getData();
        getMessages();

        mDatabase.addValueEventListener(valueEventListenerMessages);

    }

    private void getMessages() {
        messages = new ArrayList<>();
        adapter = new MessageAdapter(getApplicationContext(), messages);
        lvChat.setAdapter(adapter);

        mDatabase = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user);
        valueEventListenerMessages = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNoMessage.setVisibility(View.VISIBLE);
                } else {
                    tvNoMessage.setVisibility(View.GONE);
                }
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

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
        tvNoMessage.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.fbSend).setOnClickListener(this);
        tvNoMessage = findViewById(R.id.tvNoMessage);
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

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.removeEventListener(valueEventListenerMessages);
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.equals("")) {
            SnackWarning(getString(R.string.type_to_send));
        } else {

            //salva para o remetente
            mDatabase = FirebaseUtils.getDatabase().getInstance().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user).push();
            String push = mDatabase.getKey();
            Boolean returnSender = saveMessage(mAuth.getCurrentUser().getUid(), user, message, mDatabase);
            if (!returnSender) {
                SnackError(getString(R.string.fail_sent_message));
            } else {
                //salva para o destinatario
                mDatabase = FirebaseUtils.getDatabase().getInstance().getReference().child(MESSAGES).child(user).child(mAuth.getCurrentUser().getUid()).child(push);
                Boolean returnAddressee = saveMessage(mAuth.getCurrentUser().getUid(), user, message, mDatabase);
                if (!returnAddressee) {
                    SnackError(getString(R.string.fail_sent_message));
                } else {
                    etMessage.setText("");
                }
            }

            //Salva a Conversa para o Remetente
            ChatMessage chatSender = new ChatMessage();
            chatSender.setUid(user);
            chatSender.setMessage(message);

            Boolean returnSaveChatSender = saveChat(mAuth.getCurrentUser().getUid(), user, chatSender);
            if (!returnSaveChatSender) {
                SnackError(getString(R.string.error_save_chat));
            } else {
                //Salva a Conversa para o Remetente
                ChatMessage chatAddressee = new ChatMessage();
                chatAddressee.setUid(mAuth.getCurrentUser().getUid());
                chatAddressee.setMessage(message);

                Boolean returnSaveChatAdressee = saveChat(user, mAuth.getCurrentUser().getUid(), chatAddressee);
                if (!returnSaveChatAdressee) {
                    SnackError(getString(R.string.error_save_chat));
                }
            }

        }
    }

    private void profile() {

    }

    private boolean saveMessage(String sender, String receiver, String message, DatabaseReference datadase) {
        try {

            HashMap<String, String> msg = new HashMap<>();
            msg.put(SENDER, sender);
            msg.put(RECEIVER, receiver);
            msg.put(MESSAGE, message);
            //msg.put(DATE, null);
            msg.put(KEY, mDatabase.getKey());
            datadase.setValue(msg);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveChat(String uidSender, String uidAddressee, ChatMessage chatMessage) {
        try {

            mDatabase = FirebaseUtils.getDatabase().getInstance().getReference().child(CHAT_MESSAGES);
            mDatabase.child(uidSender).child(uidAddressee).setValue(chatMessage);

            return true;
        } catch (Exception e) {
            SnackError(e.getMessage());
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
