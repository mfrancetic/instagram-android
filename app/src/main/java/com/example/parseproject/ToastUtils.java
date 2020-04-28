package com.example.parseproject;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void showToast(Context context, String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
}