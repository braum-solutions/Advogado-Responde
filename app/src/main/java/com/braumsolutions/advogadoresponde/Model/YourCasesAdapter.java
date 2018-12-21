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

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceBold;
import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA;
import static com.braumsolutions.advogadoresponde.Utils.Utils.OCCUPATION_AREA_ARRAY;

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

            TextView tvArea = view.findViewById(R.id.tvArea);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            TextView tvLawyers = view.findViewById(R.id.tvLawyers);
            TextView tvPicture = view.findViewById(R.id.tvPicture);
            TextView tvPdf = view.findViewById(R.id.tvPdf);

            tvArea.setTypeface(TypefaceBold(context));
            tvDescription.setTypeface(TypefaceLight(context));
            tvLawyers.setTypeface(TypefaceLight(context));
            tvPicture.setTypeface(TypefaceLight(context));
            tvPdf.setTypeface(TypefaceLight(context));

            CasesModel casesModel = cases.get(position);

            tvArea.setText(OCCUPATION_AREA_ARRAY[Integer.parseInt(casesModel.getOccupation_area())]);

            if (casesModel.getDescription().length() > 60) {
                tvDescription.setText(String.format(context.getString(R.string.click_to_see_more), casesModel.getDescription().substring(0, 60)));
            } else {
                tvDescription.setText(casesModel.getDescription());
            }

            if (casesModel.getPdf() != null) {
                tvPdf.setVisibility(View.VISIBLE);
            }
            if (casesModel.getPicture() != null) {
                tvPicture.setVisibility(View.VISIBLE);
            }

        }

        return view;
    }
}
