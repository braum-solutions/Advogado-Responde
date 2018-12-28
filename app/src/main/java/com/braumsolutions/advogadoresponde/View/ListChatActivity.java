package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.Model.ChatMessage;
import com.braumsolutions.advogadoresponde.Model.ChatMessageAdapter;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ListChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView lvChat;
    private ValueEventListener valueEventListener;
    private ArrayAdapter<ChatMessage> adapter;
    private ArrayList<ChatMessage> arrayChatMessages;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView tvNoChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        loadChatMessages();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lista de bate-papo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ChatMessage chatMessage = (ChatMessage) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(USER, chatMessage.getReceiver());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        mDatabase.addValueEventListener(valueEventListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.removeEventListener(valueEventListener);
    }

    private void loadChatMessages() {
        arrayChatMessages = new ArrayList<>();
        adapter = new ChatMessageAdapter(ListChatActivity.this, arrayChatMessages);
        lvChat.setAdapter(adapter);

        mDatabase = FirebaseUtils.getDatabase().getReference().child(CHAT_MESSAGES).child(mAuth.getCurrentUser().getUid());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayChatMessages.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNoChat.setVisibility(View.VISIBLE);
                } else {
                    tvNoChat.setVisibility(View.GONE);
                    for (DataSnapshot chat : dataSnapshot.getChildren()) {
                        ChatMessage chatMessage = chat.getValue(ChatMessage.class);
                        arrayChatMessages.add(chatMessage);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        lvChat = findViewById(R.id.lvChat);
        tvNoChat = findViewById(R.id.tvNoChat);
    }
}
