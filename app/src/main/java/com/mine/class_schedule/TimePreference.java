package com.mine.class_schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.preference.DialogPreference;

public class TimePreference extends DialogPreference {

    private Context mContext;
    private NumberPicker picker;

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TimePreference(Context context) {
        super(context);
        mContext = context;
        init();
    }
//
//    @Override
//    protected View onCreateDialogView(){
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

    private void init(){
        Log.d("TimePreference","[init]");

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.dialog_time_preference,null);
        NumberPicker hourNumPicker = root.findViewById(R.id.numpick_hour);
        NumberPicker minNumPicker = root.findViewById(R.id.numpick_min);
        hourNumPicker.setMaxValue(23);
        minNumPicker.setMaxValue(59);
    }

}
