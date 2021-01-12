package com.mine.class_schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;

public class ClassTableView extends TableLayout {
    private final int MON=0;
    private final int TUE=1;
    private final int WED=2;
    private final int THU=3;
    private final int FRI=4;
    private final int SAT=5;
    public ClassView[][] classes;
    private ClassRowView[] tableRow;

    private LayoutInflater inflater;

    private final String TAG = "ClassTableView";

    LayoutParams classViewParams;
    TableRow.LayoutParams rowParams;

    public ClassTableView(Context context) {
        super(context);
        init(context);
    }

    public ClassTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        classes = new ClassView[5][6];
        tableRow = new ClassRowView[5];

        for(int p=0;p<5;p++){
            inflater = LayoutInflater.from(context);
            View root = inflater.inflate(R.layout.classtable_layout, this, true);
            int id = getResources().getIdentifier("period_"+(p+1), "id", context.getPackageName());
            if(id != 0) tableRow[p] = (ClassRowView) root.findViewById(id);
            for(int day=0;day<6;day++){
                int dayId = getResources().getIdentifier("day_"+day, "id", context.getPackageName());
                if(dayId != 0 && tableRow[p] != null) {
                    classes[p][day] = (ClassView) tableRow[p].findViewById(dayId);
                    Log.d(TAG, p+","+day+"="+dayId);
                }
                String class_text = "class "+p+" on "+day;
                classes[p][day].setText(class_text);
                classes[p][day].setPeriodId(p);
                classes[p][day].setDayId(day);
                Log.d(TAG, "id:"+classes[p][day].getPosId());
            }
        }

    }

    public String getClassText(int day, int time){
        return classes[day][time].getText().toString();
    }

    public ClassView getClass(int day, int period){
        return classes[period][day];
    }


}
