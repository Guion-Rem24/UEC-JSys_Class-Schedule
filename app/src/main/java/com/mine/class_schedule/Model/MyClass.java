package com.mine.class_schedule.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "class_table")
public class MyClass implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "classPos")
    private byte mClassPos;

    @ColumnInfo(name = "className")
    private String mClassName;

    @ColumnInfo(name = "classPlace")
    private String mClassPlace;

    @ColumnInfo(name = "isOnline")
    private boolean mOnlineFlag = false;

    @ColumnInfo(name = "onlineUrl")
    private String mOnlineUrl;

    @ColumnInfo(name = "alertNum")
    private int mAlertNum;

    @ColumnInfo(name = "alert1")
    private long mAlert1;

    @ColumnInfo(name = "alert2")
    private long mAlert2;

    @ColumnInfo(name = "alert3")
    private long mAlert3;

    public MyClass(){
    }

    public MyClass(byte pos, @NonNull String name){
        mClassPos = pos;
        mClassName = name;
    }

    public MyClass(byte pos){
        mClassPos = pos;
        mClassName = "";
        mClassPlace = "";
        mOnlineUrl = "";
    }

    public byte getClassPos()       { return this.mClassPos;  }
    public String getClassName()    { return this.mClassName; }
    public boolean getOnlineFlag()  { return this.mOnlineFlag;  }
    public String getClassPlace()   { return this.mClassPlace;}
    public String getOnlineUrl()    { return this.mOnlineUrl; }
    public int getAlertNum()        { return this.mAlertNum; }
    public long getAlert(int i)     {
        switch(i){
            case 0:
                return mAlert1;
            case 1:
                return mAlert2;
            case 2:
                return mAlert3;
            default:
                return -1;
        }
    }
    public long getAlert1()         { return this.mAlert1; }
    public long getAlert2()         { return this.mAlert2; }
    public long getAlert3()         { return this.mAlert3; }

    public long[] getAlerts()       { return new long[]{this.mAlert1, this.mAlert2, this.mAlert3}; }

    public void setClassPos(byte pos)       { this.mClassPos = pos; }
    public void setClassName(String name)   { this.mClassName = name; }
    public void setClassPlace(String place) { this.mClassPlace = place; }
    public void setOnlineUrl(String url)    { this.mOnlineUrl = url; }
    public void setOnlineFlag(boolean flag) { this.mOnlineFlag = flag; }
    public void setAlert1(long alert)       { this.mAlert1 = alert; }
    public void setAlert2(long alert)       { this.mAlert2 = alert; }
    public void setAlert3(long alert)       { this.mAlert3 = alert; }
    public void setAlertNum(int num)        { this.mAlertNum = num; }
    public void setAlert(int pos, long time){
        switch(pos){
            case 0:
                mAlert1 = time;
                break;
            case 1:
                mAlert2 = time;
                break;
            case 2:
                mAlert3 = time;
                break;
        }
    }
    public void setAlerts(long[] alerts){
        int n = alerts.length;
        for(int i=0;i<n;i++){
            setAlert(i, alerts[i]);
        }
    }

}
