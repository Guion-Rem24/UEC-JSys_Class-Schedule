package com.mine.class_schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class LockScreenStateReceiver extends BroadcastReceiver {
    private final String TAG = "LockScreenStateReceiver";

    private WindowManager windowManager;
    private View view;
    private TextView textView;
    private WindowManager.LayoutParams params;

    private LayoutInflater inflater;

    public LockScreenStateReceiver(WindowManager wm, View v, WindowManager.LayoutParams ps){
        windowManager = wm; view = v; params = ps;
    }
    public LockScreenStateReceiver(WindowManager wm, View vm, TextView tview){
        windowManager = wm; view = vm; textView = tview;
    }
    public LockScreenStateReceiver(WindowManager wm, View v, LayoutInflater inf){
        windowManager = wm; view = v; inflater = inf;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //if screen is turn off show the textview
            if (!LayerService.isShowing) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
//                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                ,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                        PixelFormat.TRANSLUCENT
                );
                params.gravity = Gravity.BOTTOM;

                windowManager.addView(textView, params);
                LayerService.isShowing = true;
            }
            Log.v(TAG, "---- on Receive ----" + intent.getAction());
        }

        else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //Handle resuming events if user is present/screen is unlocked remove the textview immediately
            if (LayerService.isShowing) {
//                    windowManager.removeViewImmediate(view);
                windowManager.removeView(textView);
                LayerService.isShowing = false;
            }
            Log.v(TAG, "---- on Receive ----" + intent.getAction());
        }

    }
}
