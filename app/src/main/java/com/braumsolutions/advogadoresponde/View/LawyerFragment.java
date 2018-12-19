package com.braumsolutions.advogadoresponde.View;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.braumsolutions.advogadoresponde.R;

public class LawyerFragment extends Fragment implements View.OnClickListener {

    private View view;

    public LawyerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lawyer, container, false);

        castWidgets();

        return view;
    }

    private void castWidgets() {
        view.findViewById(R.id.cvProfile).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvProfile:
                Intent intentProfile = new Intent(getContext(), LawyerProfileActivity.class);
                startActivity(intentProfile);
                break;
        }
    }

}
