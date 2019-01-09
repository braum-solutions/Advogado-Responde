package com.braumsolutions.advogadoresponde.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.getDateTime;
import static com.braumsolutions.advogadoresponde.Utils.MethodsUtils.getDateTimeMessage;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private ArrayList<Message> messages;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public MessageAdapter(@NonNull Context c, @NonNull ArrayList<Message> objects) {
        super(c, 0, objects);
        this.context = c;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        mAuth = FirebaseAuth.getInstance();

        if (messages != null) {
            Message message = messages.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            if (Objects.equals(message.getSender(), mAuth.getCurrentUser().getUid())) {
                view = inflater.inflate(R.layout.message_left, parent, false);
            } else {
                view = inflater.inflate(R.layout.message_rigth, parent, false);
            }

            final CircleImageView ivImage = view.findViewById(R.id.ivImage);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            TextView tvDate = view.findViewById(R.id.tvDate);

            tvMessage.setTypeface(TypefaceLight(context));
            tvMessage.setText(message.getMessage());
            tvDate.setText(getDateTimeMessage(message.getSend_date()));
            tvDate.setTypeface(TypefaceLight(context));
            DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(message.getSender());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.with(context).load(dataSnapshot.child(IMAGE).getValue(String.class)).placeholder(R.drawable.avatar).into(ivImage, null);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        return view;
    }
}
