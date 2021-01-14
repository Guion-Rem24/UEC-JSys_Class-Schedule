package com.mine.class_schedule.Model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ClassRepository {
    private ClassDao mClassDao;
    private LiveData<List<MyClass>> mAllClasses;

    public ClassRepository(Application app){
        ClassRoomDatabase db = ClassRoomDatabase.getDatabase(app);
        mClassDao = db.classDao();
        mAllClasses = mClassDao.getAllClasses();
    }

    public LiveData<List<MyClass>> getAllClasses(){ return this.mAllClasses; }
    public void insert(MyClass mClass) { new insertAsyncTask(mClassDao).execute(mClass); }
//    public MyClass getMyClass(byte pos) {  new getAsyncTask(mClassDao).execute((int)pos); }

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

//    private static class getAsyncTask extends AsyncTask<Integer, Void, MyClass>{
//        private ClassDao mAsyncTaskDao;
//        getAsyncTask(ClassDao dao) { mAsyncTaskDao = dao; }
//
//        @Override
//        protected MyClass doInBackground(final Integer... params) {
//            return mAsyncTaskDao.getMyClass(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(MyClass mClass){}
//    }
}
