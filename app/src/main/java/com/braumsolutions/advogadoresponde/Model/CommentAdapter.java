package com.braumsolutions.advogadoresponde.Model;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.IMAGE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class CommentAdapter extends ArrayAdapter<CommentModel> {

    private ArrayList<CommentModel> comments;
    private Context context;

    public CommentAdapter(@NonNull Context context, @NonNull ArrayList<CommentModel> objects) {
        super(context, 0, objects);
        this.comments = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (comments != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.comment_layout, parent, false);

            final TextView tvName = view.findViewById(R.id.tvName);
            final TextView tvComment = view.findViewById(R.id.tvComment);
            final CircleImageView ivImage = view.findViewById(R.id.ivImage);

            tvName.setTypeface(TypefaceBold(context));
            tvComment.setTypeface(TypefaceLight(context));

            final CommentModel commentModel = comments.get(position);

            DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(commentModel.getLawyer());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String fullname = String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class));
                    String image = dataSnapshot.child(IMAGE).getValue(String.class);
                    tvName.setText(fullname);
                    tvComment.setText(commentModel.getComment());
                    Picasso.with(context).load(image).placeholder(R.drawable.avatar).into(ivImage, null);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        return view;
    }
}
