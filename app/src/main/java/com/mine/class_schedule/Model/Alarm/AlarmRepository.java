package com.mine.class_schedule.Model.Alarm;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mine.class_schedule.Model.MyClass.ClassDao;
import com.mine.class_schedule.Model.MyClass.MyClass;

import java.util.List;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private AlarmDao mAlarmDao;
    private LiveData<List<Alarm>> mAllAlarms;

    public AlarmRepository(Application app){
        Log.d(TAG, "[Constructor]");

        AlarmRoomDatabase db = AlarmRoomDatabase.getInstance(app);
        mAlarmDao = db.alarmDao();
        mAllAlarms = mAlarmDao.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms(){ return mAllAlarms; }
    public void insert(Alarm alarm) { new insertAsyncTask(mAlarmDao).execute(alarm); }
    public void deleteAll() { new deleteAllAsyncTask(mAlarmDao).execute(); }

    public void deleteAlarmOf(int alarmNumber) { new deleteAsyncTask(mAlarmDao).execute(alarmNumber); }

    private static class insertAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncTaskDao;
        insertAsyncTask(AlarmDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Alarm... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private AlarmDao mAsyncDao;
        deleteAllAsyncTask(AlarmDao dao){ mAsyncDao = dao; }
        @Override
        protected Void doInBackground(final Void... params){
            mAsyncDao.deleteAll();
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private AlarmDao mAsyncDao;
        deleteAsyncTask(AlarmDao dao){ mAsyncDao = dao; }
        @Override
        protected Void doInBackground(final Integer... params){
            mAsyncDao.deleteAlarmOf(params[0]);
            return null;
        }
    }
}
