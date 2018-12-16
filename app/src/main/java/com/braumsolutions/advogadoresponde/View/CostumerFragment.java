package com.braumsolutions.advogadoresponde.View;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.braumsolutions.advogadoresponde.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CostumerFragment extends Fragment {

    private View view;

    public CostumerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_costumer, container, false);





        return view;
    }

}
