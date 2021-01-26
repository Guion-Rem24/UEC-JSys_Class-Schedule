package com.mine.class_schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.mine.class_schedule.Model.MyClass;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Calendar;

public class AlarmIntegrator {
    private AlarmManager alarmManager;
    private Context context;
    private PendingIntent pendingIntent;
    private AlarmBroadcastReceiver mReceiver;
    private IntentFilter filter;
    private MyClass classData;
    private static final String TAG = "AlarmIntegrator";
    public static final String ACTION_ALARM = "ACTION_CLASS_ALARM";
    public AlarmIntegrator(Context mContext){
        context = mContext;
        mReceiver = new AlarmBroadcastReceiver();
        filter = new IntentFilter("FILTER_ACTION_CLASS_ALARM");
        mContext.registerReceiver(mReceiver, filter);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "[Constructor]");
    }




    public void addAlarm(byte posId, long alarm, MyClass mClassData){
        int hour, min;
        int nowDay;
        classData = mClassData;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        nowDay = calendar.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 2=Monday, ..., 7=Saturday;
        int startHour = 0, startMin = 0;
         startHour = TYPE_CLASS.getPeriodStartHour(posId);
         startMin  = TYPE_CLASS.getPeriodStartMin(posId);
        if(alarm > 59){ // 時間前
            hour = (int)alarm/60;
            if(startHour - hour < 0){
                startHour = 24 - (hour - startHour);
            }
            min = 0;
        } else {
            hour = 0;
            min = (int)alarm;
        }
        startHour -= hour;
        startMin -= min;

        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMin);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 指定の曜日になるよう，日付の調整
        int classDay = TYPE_CLASS.getDay(posId) + 2; // calendarのconstに合わせる
        int addDay = 0;
        if(classDay < nowDay){ // 翌週に設定
            addDay = 7 - (nowDay - classDay);
        } else if( classDay > nowDay ) {// 今週に講義曜日がある場合
            addDay = classDay - nowDay;
        } else if( calendar.getTimeInMillis() < System.currentTimeMillis() ){ // 同じ曜日だが、時間は過ぎている場合
            addDay = 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, addDay);

        pendingIntent = this.getPendingIntent();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.v(TAG, "Set Alarm: " + calendar.get(Calendar.HOUR_OF_DAY) + ":"+calendar.get(Calendar.MINUTE)+
                "\n on "+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"("+calendar.get(Calendar.DAY_OF_WEEK)+")");

    }

    private PendingIntent getPendingIntent(){
//        Intent intent = new Intent(context, OverlayService.class);
//        return PendingIntent
//                .getService(context,
//                PendingIntent.FLAG_ONE_SHOT,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        if(classData != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("ClassData", classData);
            intent.putExtra("bundle", bundle);
        }else{
            Log.d(TAG,"classData is null");
            throw new NullPointerException();
        }
        intent.setAction(ACTION_ALARM);
        return PendingIntent
                .getBroadcast(context,
                PendingIntent.FLAG_ONE_SHOT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
