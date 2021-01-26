package com.mine.class_schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mine.class_schedule.Model.MyClass;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "[onReceive] ACTION: "+intent.getAction() +", Context:"+context.getClass().toString());
//        MyClass classData = (MyClass) intent.getSerializableExtra("ReceivedClassData");

        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle == null) throw new NullPointerException();

        MyClass classData = (MyClass) bundle.getSerializable("ClassData");
        if(classData == null) throw new NullPointerException();

        if(classData != null){
            Log.d(TAG, "[extract DATA] " +
                    "\nclassPos:   " + TYPE_CLASS.getPeriodString(classData.getClassPos()) + " on " + TYPE_CLASS.getDayString(classData.getClassPos()) +
                    "\nclassName:  " + classData.getClassName() +
                    "\nclassPlace: " + classData.getClassPlace() +
                    "\nOnline URL: " + classData.getOnlineUrl() +
                    "\nisOnline:   " + classData.getOnlineFlag() +
                    "\npreAlertNum:" + classData.getAlertNum() +
                    "\npreAlerts = {" + classData.getAlert1() + ", " + classData.getAlert2() + ", " + classData.getAlert3() + "}");
        }

        Intent serviceIntent = new Intent(context, OverlayService.class);
        if(classData != null) {
            Log.d(TAG, "put Extra of classData");
            intent.putExtra("ClassData", classData);
        }
        // TODO: 先にServiceが呼ばれてputExtraできていない
        context.startService(serviceIntent);
    }
}
