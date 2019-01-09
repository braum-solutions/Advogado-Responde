package com.braumsolutions.advogadoresponde.View.Chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.braumsolutions.advogadoresponde.View.Profile.ViewLawyerProfileActivity;
import com.braumsolutions.advogadoresponde.View.Profile.ViewUserProfileActivity;
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
import java.util.Objects;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.getDateTime;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.RECEIVER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SENDER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SEND_DATE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvChat;
    private TextView tvNoMessage;
    private EditText etMessage;
    private Toolbar toolbar;
    private String user, type;
    private FirebaseAuth mAuth;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;
    private ValueEventListener valueEventListenerMessages;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        getIntentBundle();
        castWidgets();
        setTypeface();
        getMessages();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

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
                getSupportActionBar().setTitle(dataSnapshot.child(NAME).getValue(String.class));
                type = dataSnapshot.child(TYPE_REGISTER).getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTypeface() {
        tvNoMessage.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        findViewById(R.id.fbSend).setOnClickListener(this);
        tvNoMessage = findViewById(R.id.tvNoMessage);
        lvChat = findViewById(R.id.lvChat);
        etMessage = findViewById(R.id.etMessage);
        toolbar = findViewById(R.id.toolbar);
    }

    private void getIntentBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = bundle.getString(USER);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.nav_view_profile:
                profile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
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
            chatSender.setSend_date(getDateTime());

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
        if (Objects.equals(type, "1")) {
            Intent intent = new Intent(getApplicationContext(), ViewUserProfileActivity.class);
            intent.putExtra(USER, user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), ViewLawyerProfileActivity.class);
            intent.putExtra(USER, user);
            startActivity(intent);
        }

    }

    private boolean saveMessage(String sender, String receiver, String message, DatabaseReference datadase) {
        try {

            HashMap<String, String> msg = new HashMap<>();
            msg.put(SENDER, sender);
            msg.put(RECEIVER, receiver);
            msg.put(MESSAGE, message);
            msg.put(SEND_DATE, getDateTime());
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
