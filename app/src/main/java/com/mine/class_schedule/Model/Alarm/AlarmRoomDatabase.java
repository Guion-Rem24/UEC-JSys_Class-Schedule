package com.mine.class_schedule.Model.Alarm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.mine.class_schedule.Model.MyClass.ClassRoomDatabase;

@Database(entities = {Alarm.class}, version=1, exportSchema = false)
public abstract class AlarmRoomDatabase extends RoomDatabase {
    private static final String TAG = "AlarmRoomDatabase";
    public abstract AlarmDao alarmDao();

    public static AlarmRoomDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    public static AlarmRoomDatabase getInstance(final Context context){
        Log.d(TAG, "[getDatabase]");
        // Singleton
        if (INSTANCE == null) {
            synchronized (AlarmRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    Log.d(TAG, "Database created");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmRoomDatabase.class, "alarm_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{
        private AlarmDao mAsyncDao;

        public PopulateDbAsync(AlarmRoomDatabase db){
            mAsyncDao = db.alarmDao();
        }

        @Override
        protected Void doInBackground(final Void... params){
            /**
             * 初期化処理
             */
            return null;
        }
    }
}
