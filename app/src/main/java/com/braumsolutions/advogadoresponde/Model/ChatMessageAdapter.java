package com.braumsolutions.advogadoresponde.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private Context context;
    private List<ChatMessage> chatMessageList;
    private FirebaseAuth mAuth;

    public ChatMessageAdapter(@NonNull Context c, @NonNull ArrayList<ChatMessage> objects) {
        super(c, 0, objects);
        this.chatMessageList = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        mAuth = FirebaseAuth.getInstance();

        if (chatMessageList != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.chat_list_item, parent, false);

            final ChatMessage chatMessage = getItem(position);

            final CircleImageView ivImage = view.findViewById(R.id.ivImage);
            final TextView tvName = view.findViewById(R.id.tvName);
            TextView tvMessage = view.findViewById(R.id.tvMessage);

            tvName.setTypeface(TypefaceBold(context));
            tvMessage.setTypeface(TypefaceLight(context));

            tvMessage.setText(chatMessage.getMessage());

            DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(chatMessage.getUid());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.with(context).load(dataSnapshot.child(IMAGE).getValue(String.class)).placeholder(R.drawable.avatar).into(ivImage);
                    tvName.setText(String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class)));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        return view;
    }
}
