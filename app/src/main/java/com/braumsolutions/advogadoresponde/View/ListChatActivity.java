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
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.braumsolutions.advogadoresponde.Model.ChatMessage;
import com.braumsolutions.advogadoresponde.Model.ChatMessageAdapter;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CHAT_MESSAGES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.MESSAGES;
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
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();
        loadChatMessages();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.chat));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ChatMessage chatMessage = (ChatMessage) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(USER, chatMessage.getUid());
                startActivity(intent);
            }
        });

        lvChat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AwesomeInfoDialog(ListChatActivity.this)
                        .setTitle(R.string.delete_chat)
                        .setMessage(R.string.delete_chat_msg)
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
                                final ChatMessage chatMessage = (ChatMessage) parent.getAdapter().getItem(position);
                                DatabaseReference chat =FirebaseUtils.getDatabase().getReference().child(CHAT_MESSAGES).child(mAuth.getCurrentUser().getUid()).child(chatMessage.getUid());
                                chat.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference message = FirebaseUtils.getDatabase().getReference().child(MESSAGES).child(mAuth.getCurrentUser().getUid()).child(chatMessage.getUid());
                                            message.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        SnackSuccess(getString(R.string.chat_deleted));
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    SnackError(e.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        SnackError(e.getMessage());
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            }
        });

    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(ListChatActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void loadChatMessages() {
        arrayChatMessages = new ArrayList<>();
        adapter = new ChatMessageAdapter(ListChatActivity.this, arrayChatMessages);
        lvChat.setAdapter(adapter);

        mDatabase = FirebaseUtils.getDatabase().getInstance().getReference().child(CHAT_MESSAGES).child(mAuth.getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayChatMessages.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNoChat.setVisibility(View.VISIBLE);
                } else {
                    tvNoChat.setVisibility(View.GONE);
                    for (DataSnapshot chat : dataSnapshot.getChildren()) {
                        try {
                            ChatMessage chatMessage = chat.getValue(ChatMessage.class);
                            arrayChatMessages.add(chatMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        lvChat = findViewById(R.id.lvChat);
        tvNoChat = findViewById(R.id.tvNoChat);
        tvNoChat.setTypeface(TypefaceLight(getApplicationContext()));
    }

    public void SnackError(String msg) {
        Snackbar.with(ListChatActivity.this, null)
                .type(Type.ERROR)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackWarning(String msg) {
        Snackbar.with(ListChatActivity.this, null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    public void SnackSuccess(String msg) {
        Snackbar.with(ListChatActivity.this, null)
                .type(Type.SUCCESS)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

}
