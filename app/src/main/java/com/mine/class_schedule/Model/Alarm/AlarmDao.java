package com.mine.class_schedule.Model.Alarm;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY alarmTime ASC ")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarm_table WHERE alarmNumber = :alarmNum")
    LiveData<Alarm> getAlarm(byte alarmNum);

    @Query("SELECT * FROM alarm_table WHERE alarmOfClass = :classPos")
    LiveData<List<Alarm>> getAlarmsOfClass(byte classPos);

    @Query(("SELECT * FROM alarm_table WHERE alarmOfClass = :classPos & isActive = :activeFlag"))
    LiveData<List<Alarm>> getAlarmsOfClass(byte classPos, boolean activeFlag);

}
