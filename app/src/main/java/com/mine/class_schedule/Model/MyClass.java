package com.mine.class_schedule.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO: cannot find setterというエラーを吐く
@Entity(tableName = "class_table")
public class MyClass {
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
    public boolean getOnlineFlag()       { return this.mOnlineFlag;  }
    public String getClassPlace()   { return this.mClassPlace;}
    public String getOnlineUrl()    { return this.mOnlineUrl; }

    public void setClassPos(byte pos) { this.mClassPos = pos; }
    public void setClassName(String name) { this.mClassName = name; }
    public void setClassPlace(String place) { this.mClassPlace = place; }
    public void setOnlineUrl(String url) { this.mOnlineUrl = url; }
    public void setOnlineFlag(boolean flag) { this.mOnlineFlag = flag; }
}
