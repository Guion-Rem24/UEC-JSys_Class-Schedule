package com.mine.class_schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ButtonClickedReceiver extends BroadcastReceiver {
    private final String TAG = "ButtonClickedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Intent toDialog = new Intent(context, MainActivity.class);
        toDialog.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(toDialog);
        Log.v(TAG, "---- on Receive ----");
    }
}