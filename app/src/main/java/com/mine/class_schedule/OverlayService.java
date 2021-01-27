package com.mine.class_schedule;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

public class OverlayService extends Service{//implements View.OnTouchListener, View.OnClickListener {
    private static WindowManager windowManager;
    private final String TAG="OverlayService";
    private static View view;
    private static View root_view;
    private ScreenOffReceiver mReceiver = null;
    private static WindowManager.LayoutParams params;
    private static Point displaySize;
    private MyClass classData;
    public OverlayService() {
        Log.d(TAG, "[Constructor]");
    }

    //

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        Log.d(TAG, "[onStartCommand]");

        if(intent != null) {
            Log.d(TAG,"intent: "+intent);
            classData = (MyClass) intent.getSerializableExtra("ClassData");
        } else {
            Log.d(TAG, "intent is null");
            throw new IllegalStateException();
        }
        if(classData != null){
            Log.d(TAG, "[extract DATA] " +
                    "\nclassPos:   " + TYPE_CLASS.getPeriodString(classData.getClassPos()) + " on " + TYPE_CLASS.getDayString(classData.getClassPos()) +
                    "\nclassName:  " + classData.getClassName() +
                    "\nclassPlace: " + classData.getClassPlace() +
                    "\nOnline URL: " + classData.getOnlineUrl() +
                    "\nisOnline:   " + classData.getOnlineFlag() +
                    "\npreAlertNum:" + classData.getAlertNum() +
                    "\npreAlerts = {" + classData.getAlert1() + ", " + classData.getAlert2() + ", " + classData.getAlert3() + "}");
        } else {
            Log.d(TAG, "classData is null");
        }
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        displaySize = new Point();
        windowManager.getDefaultDisplay().getSize(displaySize);

        // Layoutのparam設定
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // API 26 or later
//                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,         // API 25 or before
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // API 26 or later
                    // API 25 or before
//                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
//                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    ,
                    PixelFormat.TRANSLUCENT);
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        params.gravity = Gravity.END | Gravity.TOP; // 左右逆
//        params.gravity = Gravity.TOP | Gravity.START;
//        params.gravity = Gravity.BOTTOM | Gravity.START; // 上下逆
//        params.gravity = Gravity.END | Gravity.BOTTOM; // 上下左右逆

        root_view = inflater.inflate(R.layout.overlay_layout, null);
        view = root_view.findViewById(R.id.overlay_layout);
        Button overlayButton = (Button) view.findViewById(R.id.button_overlay);
        Log.d("debug", "overlayButton := ("+overlayButton.getBottom()+","+overlayButton.getRight()+")");
        if(overlayButton != null){
            overlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debug", "onClick");
                    { // TODO:実機で確認
                        Log.d(TAG, "Zoom URL:"+classData.getOnlineUrl());
                        Intent toChrome = new Intent(Intent.ACTION_VIEW, Uri.parse(classData.getOnlineUrl()));
                        toChrome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toChrome.setPackage("com.android.chrome");
                        try {
                            getApplicationContext().startActivity(toChrome);
                        } catch (ActivityNotFoundException ex) {
                            toChrome.setPackage(null);
                            getApplicationContext().startActivity(toChrome);
                        }
                    }
//                stopSelf();
                    onDestroy();
                }
            });
        }

        if(!view.isAttachedToWindow()){
            Log.d("debug", "view is not attached");
        }

        root_view.setOnTouchListener( new OnFrameMovingListener() );

//        root_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout grandParent = (RelativeLayout) v.getParent().getParent();
//                grandParent.callOnClick();
//            }
//        });
//        root_view.setOnTouchListener(new OnMovingListener());

        // ViewにTouchListenerを設定する ///////////////
        // API 25 以前の場合，TouchListenerが働いていない．
//        view.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("debug","onTouch");
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    Log.d("debug","ACTION_DOWN");
//                }
//                if(event.getAction() == MotionEvent.ACTION_UP){
//                    Log.d("debug","ACTION_UP");
//
//                    // warning: override performClick()
//                    view.performClick();
//
//                    // Serviceを自ら停止させる
////                    stopSelf();
//                    onDestroy();
//                    return true;
//                }
//                return false;
//            }
////            @Override
////            public void onTouchEvent(){}
//        });
//        view.setOnTouchListener(new OnMovingListener());

        /* receiverの登録　//////////////////////////// */
//        mReceiver = new ScreenOffReceiver(windowManager, view);
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mReceiver, filter);



        // WindowManagerによるViewの追加 ///////////////
        windowManager.addView(root_view, params);

//        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
        Log.d(TAG, "[onStartCommand]");
        return super.onStartCommand(intent, flag, startId);
    }




    @Override
    public void onDestroy(){
        // Viewの削除 /////////////////////////////////
        if(root_view != null) {
            windowManager.removeView(root_view);
            root_view = null;
        }

        // Receiverの登録解除 //////////////////////////
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * behind layerのonClickや，onTouchを生かす場合には，OnFrameMovingListenerを使う．
     */
    private static
    class OnFrameMovingListener implements View.OnTouchListener {

        private Point mWindowPos = new Point(0, 0); //最初に表示される位置
        private Point mTouchPoint = new Point();  //タッチされる位置
        private boolean End = false;
        private boolean Bottom = false;

        public OnFrameMovingListener(){
//            final int layoutDirection = v.getLayoutDirection();
//            final int absoluteGravity = Gravity.getAbsoluteGravity(params.gravity, layoutDirection);
            final int horizontalGravity = params.gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
            final int verticalGravity = params.gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (horizontalGravity) {
                case Gravity.END:
                {
                    End = true;
                    Log.d("test", "end");
                    break;
                }
                case Gravity.CENTER_HORIZONTAL:
                {
                    Log.d("test", "center_horizontal");
                    break;
                }
                case Gravity.START:
                {
                    Log.d("test", "start");
                    break;
                }
                default:
//                throw new IllegalStateException("Unexpected value: " + horizontalGravity);
            }

            switch (verticalGravity) {
                case Gravity.TOP:
                    Log.d("test", "top");
                    break;
                case Gravity.CENTER_VERTICAL:
                    Log.d("test", "center_vertical");
                    break;
                case Gravity.BOTTOM:
                    Bottom = true;
                    Log.d("test", "bottom");
                    break;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // x,y 位置取得
            int x = (int)event.getRawX();
            int y = (int)event.getRawY();

            switch (event.getAction()) {
                // タッチダウンでdragされた
                case MotionEvent.ACTION_MOVE:
                    // ACTION_MOVEでの位置
                    // performCheckを入れろと警告が出るので
                    v.performClick();
                    WindowManager.LayoutParams[] p = new WindowManager.LayoutParams[1];
                    p[0] = params;
                    //タッチ位置から描画位置の算出
                    mWindowPos.x = x - mTouchPoint.x;
                    mWindowPos.y = y - mTouchPoint.y;
                    p[0].x = mWindowPos.x;
                    p[0].y = mWindowPos.y;
                    if(End) p[0].x *= -1;       // 軸の向き反転
                    if(Bottom) p[0].y *= -1;    // 軸の向き反転
                    windowManager.updateViewLayout(root_view, p[0]);
                    Log.d("debug", "("+p[0].x+","+x+") → ("+p[0].y+","+y+")");
                    break;
                case MotionEvent.ACTION_DOWN:
                    //描画開始位置（疑似window の左上）を (0,0) としたときのタッチの位置を保存
                    mTouchPoint.set(x - mWindowPos.x, y - mWindowPos.y);
                    break;
                case MotionEvent.ACTION_UP:
                    // nothing to do
                    break;
                default:
                    break;
            }

            return true;
        }
    }


    /**
     * FrameLayoutが全体を覆ってしまっても良い場合，OnMovingListenerを使う
     */
    // moving listener
    // https://akira-watson.com/android/imageview-drag.html
    private static
    class OnMovingListener implements View.OnTouchListener {


        private int preDx, preDy;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // x,y 位置取得
            int newDx = (int)event.getRawX();
            int newDy = (int)event.getRawY();

            switch (event.getAction()) {
                // タッチダウンでdragされた
                case MotionEvent.ACTION_MOVE:
                    // ACTION_MOVEでの位置
                    // performCheckを入れろと警告が出るので
                    v.performClick();
                    int dx = v.getLeft() + (newDx - preDx);
                    int dy = v.getTop() + (newDy - preDy);
                    int imgW = dx + v.getWidth();
                    int imgH = dy + v.getHeight();

                    // 画像の位置を設定する
                    v.layout(dx, dy, imgW, imgH);

                    String str = "dx="+dx+"\ndy="+dy;
//                    v.setText(str);
                    Log.d("onTouch","ACTION_MOVE: dx="+dx+", dy="+dy);
                    break;
                case MotionEvent.ACTION_DOWN:
                    // nothing to do
                    break;
                case MotionEvent.ACTION_UP:
                    // nothing to do
                    break;
                default:
                    break;
            }

            // タッチした位置を古い位置とする
            preDx = newDx;
            preDy = newDy;

            return true;
        }

    }
/*
    private static void convertCoordinateFrom(WindowManager.LayoutParams[] params, MotionEvent event, int dx, int dy){
        final int[] viewWidth = new int[1];
        final int[] viewHeight = new int[1];
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        int X = params[0].x;
        int Y = params[0].y;

        params[0].x = x - ( - dx + X);
        params[0].y = y - ( - dy + Y);

    }
 */
}