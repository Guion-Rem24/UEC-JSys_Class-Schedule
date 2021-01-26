package com.mine.class_schedule.View;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.preference.PreferenceDialogFragmentCompat;

public class TimeDialongPrefFragCompat extends PreferenceDialogFragmentCompat {

    private NumberPicker picker;

    public static TimeDialongPrefFragCompat newInstance(String Key){
        final TimeDialongPrefFragCompat fragment = new TimeDialongPrefFragCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, Key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            // do something
        }
    }

//    @Override
//    protected View onCreateDialogView() {
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER;
//
//        picker = new NumberPicker(getContext());
//        picker.setLayoutParams(layoutParams);
//
//        FrameLayout dialogView = new FrameLayout(getContext());
//        dialogView.addView(picker);
//
//        return dialogView;
//    }
//
//    @Override
//    protected void onBindDialogView(View view) {
//        super.onBindDialogView(view);
//        picker.setMinValue(0);
//        picker.setMaxValue(23);
//        picker.setWrapSelectorWheel(true);
//        picker.setValue(9);
//    }
}
