package com.mine.class_schedule.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mine.class_schedule.Service.LayerService;

// https://gist.github.com/daichan4649/5352944
// https://stackoverflow.com/questions/35327328/android-overlay-textview-on-lockscreen
// Overlay on LockScreen

// API <= 25 では機能する(TYPE_SYSTEM_OVERLAY)
// API 26以降，TYPE_SYSTEM_OVERLAYはpermission deniedされている

// API 26以降も Overlay on Lock Screenをやりたければ，System Appとすれば良い？
// 参考:
// https://speakerdeck.com/shinjikobayashi/systemapurikai-fa-ru-men?slide=50
// http://exception-think.hatenablog.com/entry/20170530/1496154600
// https://www.3ace-net.co.jp/blog/201505112239.html


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

//    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //if screen is turn off show the textview
            if (!LayerService.isShowing) {
                // API 25 or before
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
//                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
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
                                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat.TRANSLUCENT
                    );
                } else { // API 26 or later
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
                                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat.TRANSLUCENT
                    );
                }
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
