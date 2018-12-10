package com.braumsolutions.advogadoresponde.Utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceUtils {

    public static Typeface TypefaceBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
    }

    public static Typeface TypefaceLight(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
    }

    public static Typeface TypefaceItalic(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Italic.ttf");
    }

    public static Typeface TypefaceRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }

}
