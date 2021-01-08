package com.mine.class_schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ClassView extends androidx.appcompat.widget.AppCompatTextView implements View.OnClickListener{
    private final String TAG = "ClassView-"+this.getText().toString();

    public ClassView(Context context) {
        super(context);
        init(context);
    }

    public ClassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
//        LayoutInflater inflater = LayoutInflater.from(context);
//        inflater.inflate(R.layout.class_layout, , true);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(getA, TAG+" [onClick]");
        Log.d(TAG, "[onCLick] : "+getText().toString());

    }


}
