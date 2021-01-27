package com.mine.class_schedule.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mine.class_schedule.Model.Alarm.Alarm;
import com.mine.class_schedule.Model.Alarm.AlarmRepository;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.Model.MyClass.ClassRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final String TAG = "HomeViewModel";

    private ClassRepository classRepository;
    private AlarmRepository alarmRepository;

    private LiveData<List<Alarm>> mAllAlarms;
    private LiveData<List<MyClass>> mAllClasses;

    private MutableLiveData<String> mText;

    public HomeViewModel(Application application) {
        super(application);
        Log.d(TAG, "[Constructor]");
        classRepository = new ClassRepository(application);
        alarmRepository = new AlarmRepository(application);

        mAllClasses = classRepository.getAllClasses();
        mAllAlarms = alarmRepository.getAllAlarms();

        if(mAllClasses.equals(null)){
            Log.d(TAG, "mAllClasses is null");
        }
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    LiveData<List<MyClass>> getAllClasses() { return mAllClasses; }
    public void insert (MyClass mClass){ classRepository.insert(mClass);}

    LiveData<List<Alarm>> getAllAlarms() { return mAllAlarms; }
    public void insert (Alarm alarm){ alarmRepository.insert(alarm); }

    public LiveData<String> getText() {
        return mText;
    }
//    public MyClass getMyClass(int posId){ return mRepository.getMyClass(posId); }

}