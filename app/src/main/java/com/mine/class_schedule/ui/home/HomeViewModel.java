package com.mine.class_schedule.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mine.class_schedule.Model.MyClass;
import com.mine.class_schedule.Model.ClassRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private ClassRepository mRepository;
    private LiveData<List<MyClass>> mAllClasses;

    private MutableLiveData<String> mText;

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new ClassRepository(application);
        mAllClasses = mRepository.getAllClasses();
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    LiveData<List<MyClass>> getAllClasses() { return mAllClasses; }
    public void insert (MyClass mClass){ mRepository.insert(mClass);}

    public LiveData<String> getText() {
        return mText;
    }
}