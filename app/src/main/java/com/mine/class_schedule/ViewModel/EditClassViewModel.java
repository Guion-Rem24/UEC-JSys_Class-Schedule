package com.mine.class_schedule.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.Alarm.AlarmRepository;
import com.mine.class_schedule.Model.MyClass.ClassRepository;
import com.mine.class_schedule.Model.MyClass.MyClass;

import java.util.List;

public class EditClassViewModel extends AndroidViewModel {
    private final static String TAG = "EditClassViewModel";

    private ClassRepository classRepository;
    private LiveData<List<MyClass>> mAllClasses;
    private AlarmRepository alarmRepository;
    private LiveData<List<Alarm>> mAllAlarms;

    public EditClassViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "[Constructor]");
        classRepository = new ClassRepository(application);
        mAllClasses = classRepository.getAllClasses();
        alarmRepository = new AlarmRepository(application);
        mAllAlarms = alarmRepository.getAllAlarms();
    }

    public LiveData<List<MyClass>> getAllClasses() { return mAllClasses; }
    public LiveData<List<Alarm>> getAllAlarm() { return mAllAlarms; }
    public void deleteAlarmOf(int alarmNumber) { alarmRepository.deleteAlarmOf(alarmNumber); }
}
