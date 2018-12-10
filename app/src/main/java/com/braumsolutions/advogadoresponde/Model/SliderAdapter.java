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

import java.util.Objects;


import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    String locale = java.util.Locale.getDefault().getLanguage();

    public SliderAdapter(Context context) {
        this.context = context;
    }

    private String[] slide_text_br = {
            "TESTE 1",
            "TESTE 2",
            "TESTE 3"
    };

    private String[] slide_text_en = {
            "TESTE 1",
            "TESTE 2",
            "TESTE 3"
    };

    private String[] slide_text_es = {
            "TESTE 1",
            "TESTE 2",
            "TESTE 3"
    };

    @Override
    public int getCount() {
        return slide_text_br.length;
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

        if (Objects.equals(locale, "pt")) {
            text.setText(slide_text_br[position]);
        } else if (Objects.equals(locale, "es")) {
            text.setText(slide_text_es[position]);
        } else {
            text.setText(slide_text_en[position]);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}