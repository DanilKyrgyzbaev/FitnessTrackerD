package com.mad_devs.fitnesstrackerd.uttils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;


public final class AndroidUtils {
    public static void showShortTost(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);

    }

    public static void showLongTost(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String msg, int length) {
        Toast.makeText(context, msg, length).show();
    }

    private static void showSnackBar(Activity activity, String msg, int length) {
        Snackbar.make(activity.findViewById(android.R.id.content), msg, length).show();
    }

    public static void showLongSnackBar(Activity activity, String msg) {
        showSnackBar(activity, msg, Snackbar.LENGTH_LONG);
    }

    public static void showShortSnackBar(Activity activity, String msg) {
        showSnackBar(activity, msg, Snackbar.LENGTH_SHORT);
    }
}
