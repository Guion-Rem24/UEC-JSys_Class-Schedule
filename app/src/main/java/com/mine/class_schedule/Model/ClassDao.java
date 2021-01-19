package com.mine.class_schedule.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MyClass mClass);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void firstInsert(MyClass mClass);

    @Query("DELETE FROM class_table")
    void deleteAll();

    @Query("SELECT * FROM class_table ORDER BY classPos ASC")
    LiveData<List<MyClass>> getAllClasses();

    @Query("SELECT * FROM class_table WHERE classPos = :posId")
    MyClass getMyClass(int posId);
    
//    @Query("SELECT classPos FROM class_table")
//    MyClass getMyClass(int pos);

}
