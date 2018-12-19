package com.braumsolutions.advogadoresponde.View;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class UserFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvNewCase, tvNewCaseMsg, tvYourCases, tvYourCasesMsg, tvChat, tvChatMsg, tvProfile, tvProfileMsg;

    public UserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        castWidgets();
        setTypeface();

        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvProfile:
                Intent intentProfile = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.cvNewCase:
                Intent intentNewCase = new Intent(getContext(), NewCaseActivity.class);
                startActivity(intentNewCase);
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
