package com.braumsolutions.advogadoresponde.Utils;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastUtils {

    public static void ToastError(Context context, String message) {
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public static void ToastSuccess(Context context, String message) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public static void ToastInfo(Context context, String message) {
        Toasty.info(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public static void ToastWarning(Context context, String message) {
        Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show();
    }

}
