package com.braumsolutions.advogadoresponde.View;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
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
import static com.braumsolutions.advogadoresponde.Utils.Utils.CREDITS;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OAB;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.VERIFIED;

public class LawyerFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvFindCase, tvFindCaseMsg, tvChat, tvChatMsg, tvCredit, tvCreditMsg, tvProfile, tvProfileMsg, tvYourCases, tvYourCasesMsg;
    private String verified;
    private FirebaseAuth mAuth;

    public LawyerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lawyer, container, false);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();
        setTypeface();
        getData();

        return view;
    }

    private void getData() {
        DatabaseReference mUser = FirebaseUtils.getDatabase().getReference().child(OAB).child(mAuth.getCurrentUser().getUid());
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                verified = dataSnapshot.child(VERIFIED).getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setTypeface() {
        tvFindCase.setTypeface(TypefaceBold(getContext()));
        tvYourCases.setTypeface(TypefaceBold(getContext()));
        tvChat.setTypeface(TypefaceBold(getContext()));
        tvCredit.setTypeface(TypefaceBold(getContext()));
        tvProfile.setTypeface(TypefaceBold(getContext()));
        tvFindCaseMsg.setTypeface(TypefaceLight(getContext()));
        tvChatMsg.setTypeface(TypefaceLight(getContext()));
        tvCreditMsg.setTypeface(TypefaceLight(getContext()));
        tvProfileMsg.setTypeface(TypefaceLight(getContext()));
        tvYourCasesMsg.setTypeface(TypefaceLight(getContext()));

    }

    private void castWidgets() {
        view.findViewById(R.id.cvProfile).setOnClickListener(this);
        view.findViewById(R.id.cvFindCase).setOnClickListener(this);
        view.findViewById(R.id.cvChat).setOnClickListener(this);
        view.findViewById(R.id.cvCredit).setOnClickListener(this);
        tvFindCase = view.findViewById(R.id.tvFindCase);
        tvFindCaseMsg = view.findViewById(R.id.tvFindCaseMsg);
        tvChat = view.findViewById(R.id.tvChat);
        tvChatMsg = view.findViewById(R.id.tvChatMsg);
        tvYourCases = view.findViewById(R.id.tvYourCases);
        tvYourCasesMsg = view.findViewById(R.id.tvYourCasesMsg);
        tvCredit = view.findViewById(R.id.tvCredit);
        tvCreditMsg = view.findViewById(R.id.tvCreditMsg);
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

    public void SnackSuccess(String msg) {
        Snackbar.with(getContext(), null)
                .type(Type.SUCCESS)
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
                Intent intentProfile = new Intent(getContext(), LawyerProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.cvFindCase:
                if (verified.equals("false")) {
                    SnackWarning(getString(R.string.no_verified_acc));
                } else {
                    Intent intentFindCase = new Intent(getContext(), CasesActivity.class);
                    startActivity(intentFindCase);
                }
                break;
            case R.id.cvChat:
                Intent intentChat = new Intent(getContext(), ListChatActivity.class);
                startActivity(intentChat);
                break;
            case R.id.cvCredit:

                break;
        }
    }

}
