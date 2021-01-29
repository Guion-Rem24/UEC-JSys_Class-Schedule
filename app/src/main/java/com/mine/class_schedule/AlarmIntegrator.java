package com.mine.class_schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.Model.Alarm.TYPE_ALARM;
import com.mine.class_schedule.Receiver.AlarmBroadcastReceiver;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Calendar;

public class AlarmIntegrator {
    private AlarmManager alarmManager;
    private Context context;
    private PendingIntent pendingIntent;
    private AlarmBroadcastReceiver mReceiver;
    private IntentFilter filter;

    private static final String TAG = "AlarmIntegrator";
    public static final String ACTION_ALARM = "ACTION_CLASS_ALARM";
    public AlarmIntegrator(Context mContext){
        context = mContext;
//        mReceiver = new AlarmBroadcastReceiver();
//        filter = new IntentFilter("FILTER_ACTION_CLASS_ALARM");
//        mContext.registerReceiver(mReceiver, filter);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "[Constructor]");
    }


    public void addAlarm(MyClass mClassData, int alarmNumber){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TYPE_ALARM.getTimeMillisOf(mClassData, alarmNumber));
        pendingIntent = this.getPendingIntent(mClassData, alarmNumber);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.v(TAG, "Set Alarm: " + calendar.get(Calendar.HOUR_OF_DAY) + ":"+calendar.get(Calendar.MINUTE)+
                "\n on "+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"("+calendar.get(Calendar.DAY_OF_WEEK)+")");
    }

    private PendingIntent getPendingIntent(MyClass classData, int alarmNumber){
//        Intent intent = new Intent(context, OverlayService.class);
//        return PendingIntent
//                .getService(context,
//                PendingIntent.FLAG_ONE_SHOT,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        if(classData != null) {
            intent.putExtra("ReceivedClassData", classData);
        }else{
            Log.d(TAG,"classData is null");
            throw new NullPointerException();
        }
        intent.setAction(ACTION_ALARM);
        return PendingIntent
                .getBroadcast(context,
                generateRequestCode(classData, alarmNumber),
                intent,
                0);
    }

    private int generateRequestCode(MyClass classData, int number){
        return ( ((int) classData.getClassPos()) + TYPE_ALARM.getAlarm(number) );
    }
    private int generateAlarmNumber(MyClass classData, int number){
        int num = (classData.getClassPos() + TYPE_ALARM.getAlarm(number));
        Log.d(TAG, "generate Num: " +
                ", int→" + num +
                "\n getAlarm(number): " + TYPE_ALARM.getAlarm(number) +
                "\n classId: "+TYPE_CLASS.castToString(classData.getClassPos()));
        return num;
    }

    public Alarm createNewAlarm(MyClass classData, int alarmNumber){
        this.addAlarm(classData, alarmNumber);

        return new Alarm(generateAlarmNumber(classData, alarmNumber),
                TYPE_ALARM.getTimeMillisOf(classData, alarmNumber),
                classData.getClassPos(),
                classData.getClassName(),
                true);
    }

    public void cancelAlarmOf(MyClass classData, int alarmNumber){
        pendingIntent = getPendingIntent(classData, alarmNumber);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

}
