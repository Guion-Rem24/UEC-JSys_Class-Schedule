package com.mine.class_schedule.Model.Alarm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "alarmNumber")
    private byte mAlarmNum;

    @ColumnInfo(name = "alarmTime")
    private long mAlarmTime;

    @ColumnInfo(name = "alarmOfClass")
    private byte mAlarmOfClass;

    @ColumnInfo(name = "isActive")
    private boolean mActiveFlag;

    public Alarm(byte num){
        mAlarmNum = num;
    }
}
