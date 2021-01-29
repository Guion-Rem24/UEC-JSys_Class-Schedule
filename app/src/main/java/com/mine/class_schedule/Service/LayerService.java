package com.mine.class_schedule.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mine.class_schedule.Receiver.ButtonClickedReceiver;
import com.mine.class_schedule.Receiver.LockScreenStateReceiver;
import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.dashboard.DashboardFragment;

// https://stackoverflow.com/questions/44626611/show-a-popup-when-user-click-on-overlay-button
// : intentにactionをつけ，BroadcastReceiverでonReceiveで受け取ることで，Listenすることができる（らしい）

public class LayerService extends Service {
//    public LayerService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
    private static final String TAG = "LayoutService";
    public static View view;
    private WindowManager wm;
    private LayoutInflater layoutInflater;
    private ButtonClickedReceiver buttonClickedReceiver;
    private LocalBroadcastManager bcManager;
    public static WindowManager.LayoutParams params;

    private WindowManager windowManager;
    private LockScreenStateReceiver mReceiver;
    public static boolean isShowing = false;

    private LayerService getPointer() { return this; }

    private TextView textView;
    private WindowManager.LayoutParams text_params;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "---- onCreate ----");
//        Toast.makeText(getApplicationContext(), "LayoutService.onCreate...", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        layoutInflater = LayoutInflater.from(this);
        view = layoutInflater.inflate(R.layout.overlay_layout, null);
        bcManager = LocalBroadcastManager.getInstance(this);

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

//        buttonClickedReceiver = new ButtonClickedReceiver();
//        bcManager.registerReceiver(buttonClickedReceiver, new IntentFilter("user.clicked.overlay.button"));

        // Sample on LockScreen //////
        //Register receiver for determining screen off and if user is present

        textView = new TextView(this);
        textView.setText("LockScreen Text");
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }else{
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
        textView.setTextSize(32f);

        text_params = new WindowManager.LayoutParams(
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
        text_params.gravity = Gravity.BOTTOM;


//        mReceiver = new LockScreenStateReceiver(windowManager, view, params);
        mReceiver = new LockScreenStateReceiver(windowManager, view, textView);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);
        //////////////////////////////


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
//                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            ,
                    PixelFormat.TRANSLUCENT);

            int mUIFlag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_VISIBLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            view.setSystemUiVisibility(mUIFlag);
        }

        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        // sample Cancel Button //////////////////////////
//        Button cancelButton = view.findViewById(R.id.sample_cancel_button);
//        if(cancelButton != null) {
//            cancelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "Clicked...", Toast.LENGTH_LONG).show();
//                    onDestroy();
//                }
//            });
//        }

        wm.addView(view, params);

        Log.v(TAG,"---- onStartCommand ----");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "---- onDestroy ----");
        DashboardFragment.activatedlayerService = false;
        wm.removeView(view);
//        bcManager.unregisterReceiver(buttonClickedReceiver);
        if(mReceiver != null) unregisterReceiver(mReceiver);
//        if(isShowing) {
//            wm.removeView(textView);
//            isShowing = false;
//        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Auto-generated method stub
        return null;
    }

//    public class LockScreenStateReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                //if screen is turn off show the textview
//                if (!isShowing) {
//                    windowManager.addView(view, params);
//                    isShowing = true;
//                }
//            }
//
//            else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//                //Handle resuming events if user is present/screen is unlocked remove the textview immediately
//                if (isShowing) {
////                    windowManager.removeViewImmediate(view);
//                    windowManager.removeView(view);
//                    isShowing = false;
//                }
//            }
//        }
//    }
}