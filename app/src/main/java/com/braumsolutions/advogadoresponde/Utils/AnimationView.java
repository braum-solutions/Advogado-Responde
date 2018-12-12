package com.braumsolutions.advogadoresponde.Utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.braumsolutions.advogadoresponde.R;

public class AnimationView {

    public static Animation AnimationFadeIn500(Context context) {
        return AnimationUtils.loadAnimation((context), R.anim.animation_fade_in_500);
    }

    public static Animation AnimationFadeIn1000(Context context) {
        return AnimationUtils.loadAnimation((context), R.anim.animation_fade_in_1000);
    }

    public static Animation AnimationFadeIn1500(Context context) {
        return AnimationUtils.loadAnimation((context), R.anim.animation_fade_in_1500);
    }

    public static Animation AnimationFadeIn2000(Context context) {
        return AnimationUtils.loadAnimation((context), R.anim.animation_fade_in_2000);
    }

    public static Animation AnimationFadeIn2500(Context context) {
        return AnimationUtils.loadAnimation((context), R.anim.animation_fade_in_2500);
    }

}
