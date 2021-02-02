package com.mine.class_schedule.Model.MyClass;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ClassRepository {
    private final String TAG = "ClassRepository";

    private ClassDao mClassDao;
    private LiveData<List<MyClass>> mAllClasses;
    private LiveData<MyClass> tmpClass;

    public ClassRepository(Application app){
        Log.d(TAG, "[Constructor]");

        ClassRoomDatabase db = ClassRoomDatabase.getDatabase(app);
        mClassDao = db.classDao();
        tmpClass = new MutableLiveData<>();
        mAllClasses = mClassDao.getAllClasses();
        if(mAllClasses.equals(null)){
            Log.d(TAG, "mAllClasses is null");
        }
    }

    public LiveData<List<MyClass>> getAllClasses(){ return this.mAllClasses; }
    public void insert(MyClass mClass) { new insertAsyncTask(mClassDao).execute(mClass); }
//    public MyClass getMyClass(byte posId) {
//        final MyClass[] classData = new MyClass[1];
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                classData[0] = mClassDao.getMyClass(posId);
//            }
//        }).start();
//        return classData[0];
//    }
    public void signalToGetMyClass(byte pos) {
        new getAsyncTask(mClassDao).execute((int)pos);
    }
    public LiveData<MyClass> getClassData(){ return tmpClass; }
//    public MyClass getMyClass(int posId) { return mClassDao.getMyClass(posId); }

    private static class insertAsyncTask extends AsyncTask<MyClass, Void, Void>{
        private ClassDao mAsyncTaskDao;
        insertAsyncTask(ClassDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final MyClass... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private class getAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ClassDao mAsyncTaskDao;
        getAsyncTask(ClassDao dao) { mAsyncTaskDao = dao; }

        @Override
        protected Void doInBackground(final Integer... params) {
            tmpClass = mAsyncTaskDao.getMyClass(params[0]);
            return null;
        }

//        @Override
//        protected void onPostExecute(MyClass mClass){
//        }
    }
}
