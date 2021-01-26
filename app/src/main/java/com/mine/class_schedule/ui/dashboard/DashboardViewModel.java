package com.mine.class_schedule.ui.dashboard;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mine.class_schedule.Model.ClassRepository;
import com.mine.class_schedule.Model.MyClass;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private ClassRepository mRepository;
    private LiveData<List<MyClass>> mAllClasses;

    public DashboardViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");

        mRepository = new ClassRepository(application);
        mAllClasses = mRepository.getAllClasses();

    }

    public LiveData<List<MyClass>> getAllClasses(){return this.mAllClasses;};

    public LiveData<String> getText() {
        return mText;
    }
}