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

import java.util.ArrayList;

import androidx.annotation.Nullable;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAST_NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.NAME;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class CasesAdapter extends ArrayAdapter<CasesModel> {

    private ArrayList<CasesModel> cases;
    private Context context;

    public CasesAdapter(@NonNull Context context, @NonNull ArrayList<CasesModel> objects) {
        super(context, 0, objects);
        this.cases = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if (cases != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cases_layout, parent, false);

            TextView tvArea = view.findViewById(R.id.tvArea);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            final TextView tvUser = view.findViewById(R.id.tvUser);
            TextView tvAreMsg = view.findViewById(R.id.tvAreaMsg);
            TextView tvDescriptionMsg = view.findViewById(R.id.tvDescriptionMsg);
            TextView tvUserMsg = view.findViewById(R.id.tvUserMsg);

            tvArea.setTypeface(TypefaceLight(context));
            tvDescription.setTypeface(TypefaceLight(context));
            tvUser.setTypeface(TypefaceLight(context));
            tvAreMsg.setTypeface(TypefaceBold(context));
            tvDescriptionMsg.setTypeface(TypefaceBold(context));
            tvUserMsg.setTypeface(TypefaceBold(context));

            CasesModel casesModel = cases.get(position);

            DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(casesModel.getUser());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String fullname = String.format("%s %s", dataSnapshot.child(NAME).getValue(String.class), dataSnapshot.child(LAST_NAME).getValue(String.class));
                    tvUser.setText(fullname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            tvArea.setText(OCCUPATION_AREA_ARRAY[Integer.parseInt(casesModel.getOccupation_area())]);

            if (casesModel.getDescription().length() > 60) {
                tvDescription.setText(String.format(context.getString(R.string.click_to_see_more), casesModel.getDescription().substring(0, 60)));
            } else {
                tvDescription.setText(casesModel.getDescription());
            }

        }

        return view;
    }
}
