package com.mine.class_schedule.ui.classview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.mine.class_schedule.R;
import com.mine.class_schedule.ViewModel.EditClassFromOutsideViewModel;

public class PopupClassTableView extends TableLayout {

    private final String TAG = "PopupClassTableView";

    public ClassView[][] classes;
    private ClassRowView[] tableRow;
    private LayoutInflater inflater;
    LayoutParams classViewParams;
    TableRow.LayoutParams rowParams;

    public PopupClassTableView(Context context) {
        super(context);
        init(context);
    }

    public PopupClassTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        classes = new ClassView[5][6];
        tableRow = new ClassRowView[5];
        inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.popup_classtable_layout, this, true);

        for(int p=0;p<5;p++){
            int id = getResources().getIdentifier("period_"+(p+1), "id", context.getPackageName());
            if(id != 0) tableRow[p] = (ClassRowView) root.findViewById(id);
            for(int day=0;day<6;day++){
                int dayId = getResources().getIdentifier("day_"+day, "id", context.getPackageName());
                if(dayId != 0 && tableRow[p] != null) {
                    classes[p][day] = (ClassView) tableRow[p].findViewById(dayId);
                    classes[p][day].setNameView(classes[p][day].findViewById(R.id.text_classname));
                    classes[p][day].setPlaceView(classes[p][day].findViewById(R.id.text_classplace));
                }
                classes[p][day].setClassData(day,p);
                String class_text = String.format("%s\n%s",TYPE_CLASS.getDayString(classes[p][day].getPosId()),TYPE_CLASS.getPeriodString(classes[p][day].getPosId()));
                classes[p][day].setName(class_text);
            }
        }
    }

    public void updateOnClickListeners(View.OnClickListener listener){
        Log.d(TAG,"[updateOnClickListener]");
        for(ClassView[] c_ :classes){
            for(ClassView c : c_){
                c.updateOnClickListener(listener);

            }
        }
    }

    public void setViewModel(EditClassFromOutsideViewModel viewModel){
        Log.d(TAG,"[setViewModel]");
        for(ClassView[] c_ :classes){
            for(ClassView c : c_){
                c.setViewModel(viewModel);

            }
        }
    }
}
