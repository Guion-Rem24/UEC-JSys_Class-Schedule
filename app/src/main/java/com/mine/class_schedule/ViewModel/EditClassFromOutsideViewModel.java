package com.mine.class_schedule.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mine.class_schedule.Model.MyClass.ClassRepository;
import com.mine.class_schedule.Model.MyClass.MyClass;

import java.util.List;

public class EditClassFromOutsideViewModel extends AndroidViewModel {
    private MutableLiveData<Byte> classPos;
    private ClassRepository classRepository;
    private LiveData<MyClass> tmpClass;
    private LiveData<List<MyClass>> mAllClasses;
    public EditClassFromOutsideViewModel(@NonNull Application application) {
        super(application);
        classPos = new MutableLiveData<>();
        tmpClass = new MutableLiveData<>();
        classRepository = new ClassRepository(application);
        mAllClasses = classRepository.getAllClasses();
        classPos.setValue((byte)0xFF);
        tmpClass = classRepository.getClassData();
    }

    public void setClassPos(byte pos){
        classPos.setValue(pos);
    }

    public MutableLiveData<Byte> getClassPos(){ return classPos; }
    public void insert(MyClass mClass){ classRepository.insert(mClass); }
    public void signalToGetMyClass(byte posId) { classRepository.signalToGetMyClass(posId); }
    public LiveData<MyClass> getTmpClass() { return tmpClass; }
}
