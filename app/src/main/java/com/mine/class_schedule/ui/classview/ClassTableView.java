package com.mine.class_schedule.ui.classview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;

import java.util.List;
//import com.mine.class_schedule.databinding.ClasstableLayoutBinding;

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
        inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.classtable_layout, this, true);

        for(int p=0;p<5;p++){
//            ClasstableLayoutBinding root = DataBindingUtil.inflate(inflater, R.layout.classtable_layout, this, true);
            int id = getResources().getIdentifier("period_"+(p+1), "id", context.getPackageName());
            if(id != 0) tableRow[p] = (ClassRowView) root.findViewById(id);
            for(int day=0;day<6;day++){
                int dayId = getResources().getIdentifier("day_"+day, "id", context.getPackageName());
                if(dayId != 0 && tableRow[p] != null) {
                    classes[p][day] = (ClassView) tableRow[p].findViewById(dayId);
                    classes[p][day].setNameView(classes[p][day].findViewById(R.id.text_classname));
                    classes[p][day].setPlaceView(classes[p][day].findViewById(R.id.text_classplace));
//                    Log.d(TAG, p+","+day+"="+dayId);
                }
                String class_text = "class "+p+" on "+day;
                classes[p][day].setName(class_text);
                classes[p][day].setClassData(day,p);
//                Log.d(TAG, "id:"+classes[p][day].getPosId());
            }
        }

    }

    public String getClassText(int day, int time){
        return classes[day][time].getName();
    }

    public ClassView getClass(int day, int period){
        return classes[period][day];
    }


    public void updateClassesUI(List<MyClass> myClasses){
        for(MyClass c : myClasses){
            int[] pos = TYPE_CLASS.getClassPos(c.getClassPos()); // day, period
            String name = c.getClassName();
            if(TextUtils.isEmpty(name)) name = "class "+pos[0]+" on "+pos[1];
            classes[pos[1]][pos[0]].setName(name);
            String place = c.getClassPlace();
            if(TextUtils.isEmpty(place)) place = "place";
            else {
                if (c.getOnlineFlag()) place = "Online";
                else place = "offline";
            }
            if(place.equals("place")){
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.disableColor));
                } else {
                    classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.disableColor, null));
                }
            } else {
                if (c.getOnlineFlag()) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.red));
                    } else {
                        classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.red, null));
                    }
                } else {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                        classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.teal_200));
                    } else {
                        classes[pos[1]][pos[0]].setPlace(place, getResources().getColor(R.color.teal_200, null));
                    }
                }
            }
        }
    }

    public void setClassData(boolean setDataFlag){
        for(ClassView[] c_ : classes){
            for(ClassView c : c_){
                c.setClass(setDataFlag);
            }
        }
    }

    public void setLayoutSize(int width, int height){
        for(ClassView[] c_ :classes){
            for(ClassView c : c_){
                c.setViewSize(width/6, height/5);
            }
        }
    }

    public void updateOnClickListeners(View.OnClickListener listener){
        for(ClassView[] c_ :classes){
            for(ClassView c : c_){
                c.updateOnClickListener(listener);
            }
        }
    }


}
