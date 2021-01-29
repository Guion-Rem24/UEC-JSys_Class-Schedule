package com.mine.class_schedule.Model.Alarm;

import android.annotation.SuppressLint;
import android.icu.number.Scale;
import android.util.Log;

import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Calendar;

public class TYPE_ALARM {
    private final static String TAG = "TYPE_ALARM";
    public static final int ALARM_1 = 256;
    public static final int ALARM_2 = 512;
    public static final int ALARM_3 = 768;
    public static final byte ALARM_MASK = (byte) 0x0F00;

    public static int getAlarm(int num){
        switch(num){
            case 0:
                return ALARM_1;
            case 1:
                return ALARM_2;
            case 2:
                return ALARM_3;
            default:
                throw new IllegalArgumentException();
        }
    }

    @SuppressLint("DefaultLocale")
    public static String getDayString(long timeMillis){
        return String.format("%d/%d %s", getMonth(timeMillis), getDayOfMonth(timeMillis), getDayOfWeek(timeMillis));
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeString(long timeMillis){
        return String.format("%d:%02d", getHour(timeMillis), getMin(timeMillis));
    }

    private static int getHour(long timeMillis){
        return getNewCalendar(timeMillis).get(Calendar.HOUR_OF_DAY);
    }
    private static int getMin(long timeMillis) {
        return getNewCalendar(timeMillis).get(Calendar.MINUTE);
    }

    public static int getMonth(long timeMillis){
        return getNewCalendar(timeMillis).get(Calendar.MONTH)+1;
    }

    public static int getDayOfMonth(long timeMillis){
        return getNewCalendar(timeMillis).get(Calendar.DAY_OF_MONTH);
    }

    private static String getDayOfWeek(long timeMillis){
        switch (getNewCalendar(timeMillis).get(Calendar.DAY_OF_WEEK)){
//            case 1:
//                return "(日)";
            case 2:
                return "(月)";
            case 3:
                return "(火)";
            case 4:
                return "(水)";
            case 5:
                return "(木)";
            case 6:
                return "(金)";
            case 7:
                return "(土)";
            default:
                throw new IllegalArgumentException();
        }
    }
    private static Calendar getNewCalendar(long timeMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar;
    }

    public static long getTimeMillisOf(MyClass classData, int alarmNumber){
        int hour, min;
        int nowDay;
        byte posId = classData.getClassPos();
        long alarm = classData.getAlert(alarmNumber);

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
        return calendar.getTimeInMillis();
    }

}
