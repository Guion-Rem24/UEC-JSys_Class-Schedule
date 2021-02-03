package com.mine.class_schedule.ui.notifications;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.Alarm.AlarmRepository;
import com.mine.class_schedule.Model.MyClass.ClassRepository;
import com.mine.class_schedule.Model.MyClass.MyClass;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    private static final String TAG = "NotificationsViewModel";
    private AlarmRepository alarmRepository;
    private ClassRepository classRepository;
    private LiveData<List<Alarm>> mAllAlarms;
    private LiveData<List<MyClass>> mAllClasses;

    private MutableLiveData<String> mText;

    public NotificationsViewModel(Application application) {
        super(application);
        Log.d(TAG, "[Constructor]");
        mText = new MutableLiveData<>();
//        mText.setValue("This is notifications fragment");

        classRepository = new ClassRepository(application);
        mAllClasses = classRepository.getAllClasses();
        alarmRepository = new AlarmRepository(application);
        mAllAlarms = alarmRepository.getAllAlarms();

    }

    public LiveData<String> getText() {
        return mText;
    }

    LiveData<List<MyClass>> getAllClasses() {return mAllClasses;}
    public void insert (MyClass class_) { classRepository.insert(class_); }

    LiveData<List<Alarm>> getAllAlarms() { return mAllAlarms; }
    public void insert (Alarm alarm){ alarmRepository.insert(alarm); }
    public void deleteAlarmOf(int alarmNumber) { alarmRepository.deleteAlarmOf(alarmNumber);}
    public void deleteAllAlarm() { alarmRepository.deleteAll(); }
}