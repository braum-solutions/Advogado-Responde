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

            tvMessage.setTypeface(TypefaceLight(context));
            tvMessage.setText(message.getMessage());
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
            if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.SECONDS) < 60) {
                String seg = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.SECONDS));
                if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.SECONDS) < 10) {
                    seg = "0" + seg;
                }
                Log.d("QWERTY", String.format("%s seg", seg));
            } else if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.MINUTES) < 60) {
                String min = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.MINUTES));
                if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.MINUTES) < 10) {
                    min = "0" + min;
                }
                Log.d("QWERTY", String.format("%s min", min));
            } else if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.HOURS) < 24) {
                String hour = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.HOURS));
                if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.HOURS) < 10) {
                    hour = "0" + hour;
                }
                Log.d("QWERTY", String.format("%s hora", hour));
            } else if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.DAYS) < 30) {
                String day = String.valueOf(DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.DAYS));
                if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.DAYS) < 10) {
                    day = "0" + day;
                }
                Log.d("QWERTY", String.format("%s dia", day));
            } else if (DateTimeUtils.getDateDiff(getDateTime(), message.getSend_date(), DateTimeUnits.DAYS) > 30) {
                Log.d("QWERTY", "Mais de 30 dias");
            }

        }

        return view;
    }
}
