package com.mine.class_schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReplacedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        // 他のアプリ更新時は対象外とする
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            if (!intent.getDataString().equals(
                    "package:" + context.getPackageName())) {
                return;
            }
        }

        // AlarmManager を開始する
        AlertService.startAlarm(context);
    }
}