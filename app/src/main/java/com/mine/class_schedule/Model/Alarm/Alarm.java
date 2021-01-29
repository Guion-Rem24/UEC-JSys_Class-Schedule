package com.mine.class_schedule.Model.Alarm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "alarmNumber")
    private int mAlarmNum;

    @ColumnInfo(name = "alarmTime")
    private long mAlarmTime;

    @ColumnInfo(name = "alarmOfClass")
    private byte mAlarmOfClass;

    @ColumnInfo(name = "className")
    private String mClassName;

    @ColumnInfo(name = "isActive")
    private boolean mActiveFlag;

    public Alarm(){}

    public Alarm(byte num){
        mAlarmNum = num;
    }
    public Alarm(int alarmNumber, long alarmTime, byte classId, String className, boolean activeFlag){
        mAlarmNum = alarmNumber;
        mAlarmTime = alarmTime;
        mAlarmOfClass = classId;
        mClassName = className;
        mActiveFlag = activeFlag;
    }

    public int getAlarmNum() { return mAlarmNum; }
    public byte getAlarmOfClass() { return mAlarmOfClass; }
    public String getClassName() { return mClassName; }
    public long getAlarmTime() { return mAlarmTime; }
    public boolean getActiveFlag() { return mActiveFlag; }

    public void setAlarmOfClass(byte classId) { this.mAlarmOfClass = classId; }
    public void setAlarmNum(int number){ this.mAlarmNum = number; }
    public void setActiveFlag(boolean flag){ this.mActiveFlag = flag; }
    public void setAlarmTime(long time){ this.mAlarmTime = time; }
    public void setClassName(String name){ this.mClassName = name; }
}
