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
    private TextView tvFindCase, tvFindCaseMsg;
    private String verified;
    private int credits;
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

        DatabaseReference mCredits = FirebaseUtils.getDatabase().getReference().child(CREDITS).child(mAuth.getCurrentUser().getUid());
        mCredits.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(CREDITS).getValue(String.class) != null) {
                    credits = Integer.parseInt(dataSnapshot.child(CREDITS).getValue(String.class));
                } else {
                    credits = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setTypeface() {
        tvFindCase.setTypeface(TypefaceBold(getContext()));
        tvFindCaseMsg.setTypeface(TypefaceLight(getContext()));
    }

    private void castWidgets() {
        view.findViewById(R.id.cvProfile).setOnClickListener(this);
        view.findViewById(R.id.cvFindCase).setOnClickListener(this);
        tvFindCase = view.findViewById(R.id.tvFindCase);
        tvFindCaseMsg = view.findViewById(R.id.tvFindCaseMsg);
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
        }
    }

}
