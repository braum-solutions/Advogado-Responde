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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
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
    private  DatabaseReference database;

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

        database.addValueEventListener(valueEventListenerMessages);

    }

    private void getMessages() {
        messages = new ArrayList<>();
        adapter = new MessageAdapter(getApplicationContext(), messages);
        lvChat.setAdapter(adapter);

        database = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(user);
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
        database.removeEventListener(valueEventListenerMessages);
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.equals("")) {
            SnackWarning(getString(R.string.type_to_send));
        } else {
            Message msg = new Message();
            msg.setMessage(message);
            msg.setSender(mAuth.getCurrentUser().getUid());

            Boolean returnSaveMessage = saveMessage(mAuth.getCurrentUser().getUid(), user, msg);
            if (!returnSaveMessage) {
                SnackError(getString(R.string.fail_sent_message));
            } else {

                ChatMessage chat = new ChatMessage();
                chat.setMessage(message);
                chat.setSender(mAuth.getCurrentUser().getUid());
                chat.setReceiver(user);

                Boolean returnSaveChat = saveChat(mAuth.getCurrentUser().getUid(), user, chat);
                if (!returnSaveChat) {
                    SnackError(getString(R.string.error_save_chat));
                } else {

                    etMessage.setText("");
                    etMessage.requestFocus();

                }

            }
        }
    }

    private void profile() {

    }

    private boolean saveMessage(String sender, String receiver, Message message) {
        try {
            DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(MESSAGES);
            database.child(sender).child(receiver).push().setValue(message).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            database.child(receiver).child(sender).push().setValue(message).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveChat(String sender, String receiver, ChatMessage chatMessage) {
        try {
            DatabaseReference database = FirebaseUtils.getDatabase().getInstance().getReference().child(CHAT_MESSAGES);
            database.child(sender).child(receiver).setValue(chatMessage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            database.child(receiver).child(sender).setValue(chatMessage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
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
