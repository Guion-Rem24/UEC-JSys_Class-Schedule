package com.mine.class_schedule.Model.Alarm;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Calendar;
import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private LayoutInflater inflater;
    private List<Alarm> mAlarms;
    private Context mContext;

    public AlarmListAdapter(Context context){
        inflater = LayoutInflater.from(context);
        mContext = context;
    }


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_alarm, parent,false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        if(mAlarms != null){
            Alarm current = mAlarms.get(position);
            String name = String.format("%s: %s %s", current.getClassName(),
                    TYPE_CLASS.getDayString(current.getAlarmOfClass()),
                    TYPE_CLASS.getPeriodString(current.getAlarmOfClass()));
            holder.classNameView.setText(name);
            if(equalsDateBetween(current.getAlarmTime(), System.currentTimeMillis())){
                holder.alarmDayView.setText("今日");
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    holder.alarmDayView.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    holder.alarmDayView.setTextColor(mContext.getResources().getColor(R.color.red, null));
                }
            } else {
                holder.alarmDayView.setText(TYPE_ALARM.getDayString(current.getAlarmTime()));
            }
            holder.alarmTimeView.setText(TYPE_ALARM.getTimeString(current.getAlarmTime()));
            holder.alarmActiveFlagBox.setChecked(current.getActiveFlag());
        } else {
            holder.classNameView.setText(" --- ");
            holder.alarmTimeView.setText(" -- : --");
            holder.alarmActiveFlagBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        if(mAlarms != null) return mAlarms.size();
        return 0;
    }

    public void setAlarms(List<Alarm> alarms){
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        public TextView classNameView;
        public TextView alarmTimeView;
        public CheckBox alarmActiveFlagBox;
        public TextView alarmDayView;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameView = itemView.findViewById(R.id.recycler_text_classname);
            alarmDayView = itemView.findViewById(R.id.recycler_text_day);
            alarmTimeView = itemView.findViewById(R.id.recycler_text_alarmtime);
            alarmActiveFlagBox = itemView.findViewById(R.id.checkbox_activeflag);
        }
    }

    private boolean equalsDayBetween(long time1, long time2){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1); c2.setTimeInMillis(time2);
        return (c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
    }

    private boolean equalsMonthBetween(long time1, long time2){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(time1); c2.setTimeInMillis(time2);
        return (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    private boolean equalsDateBetween(long time1, long time2){
        return (equalsDayBetween(time1, time2) && equalsMonthBetween(time1, time2));
    }
}
