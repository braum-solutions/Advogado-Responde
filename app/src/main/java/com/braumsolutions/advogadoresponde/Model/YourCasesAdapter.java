package com.braumsolutions.advogadoresponde.Model;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.braumsolutions.advogadoresponde.R;
import java.util.ArrayList;
import androidx.annotation.Nullable;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class YourCasesAdapter extends ArrayAdapter<CasesModel> {

    private ArrayList<CasesModel> cases;
    private Context context;

    public YourCasesAdapter(@NonNull Context context, @NonNull ArrayList<CasesModel> objects) {
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
            view = inflater.inflate(R.layout.your_cases_layout, parent, false);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            tvDescription.setTypeface(TypefaceLight(context));

            CasesModel casesModel = cases.get(position);

            tvDescription.setText(casesModel.getDescription());

        }

        return view;
    }
}
