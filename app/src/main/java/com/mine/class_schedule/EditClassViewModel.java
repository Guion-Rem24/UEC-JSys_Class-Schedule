package com.mine.class_schedule;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mine.class_schedule.Model.ClassRepository;
import com.mine.class_schedule.Model.MyClass;

import java.util.List;

public class EditClassViewModel extends AndroidViewModel {
    private final static String TAG = "EditClassViewModel";

    private ClassRepository mRepository;
    private LiveData<List<MyClass>> mAllClasses;

    public EditClassViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "[Constructor]");
        mRepository = new ClassRepository(application);
        mAllClasses = mRepository.getAllClasses();
    }

    public LiveData<List<MyClass>> getAllClasses() { return mAllClasses; }
}
