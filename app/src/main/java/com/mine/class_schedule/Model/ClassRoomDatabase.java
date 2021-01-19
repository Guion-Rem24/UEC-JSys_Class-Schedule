package com.mine.class_schedule.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mine.class_schedule.ui.classview.TYPE_CLASS;

@Database(entities = {MyClass.class}, version=1, exportSchema = false)
public abstract class ClassRoomDatabase extends RoomDatabase {
    private static final String TAG = "ClassRoomDatabase";
    public abstract ClassDao classDao();

    /**
     * callbackで onOpenをオーバーライドして
     * あげると，起動時に毎回同じ設定になる．
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    public static ClassRoomDatabase INSTANCE;
    public static ClassRoomDatabase getDatabase(final Context context){
        Log.d(TAG, "[getDatabase]");
        // Singleton
        if (INSTANCE == null) {
            synchronized (ClassRoomDatabase.class) {
                if (INSTANCE == null) {
                    // create database
                    Log.d(TAG, "Database created");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ClassRoomDatabase.class, "class_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * Populate the database in the background
     */
    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void>{
        private final ClassDao mDao;
         String[] classNames = {"test1","test2","test3"};

        PopulateDbAsync(ClassRoomDatabase db){ mDao = db.classDao(); }

        @Override
        protected Void doInBackground(final Void... params){
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created

            for(int i=0; i<5; i++){
                for(int j=0; j<6; j++){
                    MyClass class_ = new MyClass((byte) (TYPE_CLASS.getDay(j) | TYPE_CLASS.getPeriod(i)));
                    mDao.firstInsert(class_);
                }
            }

            return null;
        }
    }
}
