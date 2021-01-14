package com.mine.class_schedule.ui.classview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableRow;

import com.mine.class_schedule.R;

public class ClassRowView extends TableRow {
    public ClassRowView(Context context) {
        super(context);
        init(context);
    }

    public ClassRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.classtable_row, this, true);

    }

}
