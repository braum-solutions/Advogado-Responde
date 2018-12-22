package com.braumsolutions.advogadoresponde.View;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.braumsolutions.advogadoresponde.Utils.Utils;
import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.PHONE;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class UserFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvNewCase, tvNewCaseMsg, tvYourCases, tvYourCasesMsg, tvChat, tvChatMsg, tvProfile, tvProfileMsg;
    private String phone;
    private FirebaseAuth mAuth;

    public UserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getData();

        return view;
    }

    private void getData() {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(PHONE).getValue(String.class) != null) {
                    phone = dataSnapshot.child(PHONE).getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTypeface() {
        tvNewCase.setTypeface(TypefaceBold(getContext()));
        tvYourCases.setTypeface(TypefaceBold(getContext()));
        tvChat.setTypeface(TypefaceBold(getContext()));
        tvProfile.setTypeface(TypefaceBold(getContext()));
        tvNewCaseMsg.setTypeface(TypefaceLight(getContext()));
        tvYourCasesMsg.setTypeface(TypefaceLight(getContext()));
        tvChatMsg.setTypeface(TypefaceLight(getContext()));
        tvProfileMsg.setTypeface(TypefaceLight(getContext()));
    }

    private void castWidgets() {
        view.findViewById(R.id.cvProfile).setOnClickListener(this);
        view.findViewById(R.id.cvNewCase).setOnClickListener(this);
        view.findViewById(R.id.cvYourCases).setOnClickListener(this);
        view.findViewById(R.id.cvChat).setOnClickListener(this);
        tvNewCase = view.findViewById(R.id.tvNewCase);
        tvNewCaseMsg = view.findViewById(R.id.tvNewCaseMsg);
        tvYourCases = view.findViewById(R.id.tvYourCases);
        tvYourCasesMsg = view.findViewById(R.id.tvYourCasesMsg);
        tvChat = view.findViewById(R.id.tvChat);
        tvChatMsg = view.findViewById(R.id.tvChatMsg);
        tvProfile = view.findViewById(R.id.tvProfile);
        tvProfileMsg = view.findViewById(R.id.tvProfileMsg);
    }

    public void SnackWarning(String msg) {
        Snackbar.with(getContext(), null)
                .type(Type.WARNING)
                .message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.LEFT)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvProfile:
                Intent intentProfile = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.cvNewCase:
                if (phone == null) {
                    SnackWarning(getString(R.string.update_phone_msg));
                } else {
                    Intent intentNewCase = new Intent(getContext(), OccupationAreaCaseActivity.class);
                    startActivity(intentNewCase);
                }
                break;
            case R.id.cvYourCases:
                Intent intentYourCases = new Intent(getContext(), YourCasesActivity.class);
                startActivity(intentYourCases);
                break;
            case R.id.cvChat:
                Intent intentChat = new Intent(getContext(), ChatActivity.class);
                startActivity(intentChat);
                break;
        }
    }
}
