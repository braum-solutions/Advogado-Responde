package com.braumsolutions.advogadoresponde.Model;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.braumsolutions.advogadoresponde.R;

import java.util.Locale;
import java.util.Objects;


import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SLIDE_TEXT_BR;
import static com.braumsolutions.advogadoresponde.Utils.Utils.SLIDE_TEXT_EN;

public class SliderAdapter extends PagerAdapter {
    private Context context;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return SLIDE_TEXT_BR.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        TextView text = view.findViewById(R.id.tvText);
        text.setTypeface(TypefaceLight(context));
        text.setShadowLayer(5, 0, 0, Color.BLACK);

        if (Objects.equals(Locale.getDefault().getDisplayLanguage(), "English")) {
            text.setText(SLIDE_TEXT_EN[position]);
        } else {
            text.setText(SLIDE_TEXT_BR[position]);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}