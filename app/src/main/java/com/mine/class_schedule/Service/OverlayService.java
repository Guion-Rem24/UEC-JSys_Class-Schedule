package com.mine.class_schedule.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.Receiver.ScreenOffReceiver;
import com.mine.class_schedule.View.MainActivity;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;


// TODO: Serviceが終了した時に，来週のalarmをセット
public class OverlayService extends Service{//implements View.OnTouchListener, View.OnClickListener {
    private final String TAG="OverlayService";

    private static WindowManager windowManager;
    private static View view;
    private static View root_view;
    private TextView classNameView;
    private LayoutInflater inflater;
    private Button requestButton;
    private ImageButton cancelButton;
    private Button shareButton;

    private MediaPlayer mediaPlayer;

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

        // 初期化
        initialize();
        // viewの取得
        findViews();

        classNameView.setText(classData.getClassName());

        displaySize = new Point();
        windowManager.getDefaultDisplay().getSize(displaySize);

        // Layoutのparam設定
        setLayoutParams();

        // 各種Listenerのセッティング
        setListeners();

        /* receiverの登録　//////////////////////////// */
//        mReceiver = new ScreenOffReceiver(windowManager, view);
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(mReceiver, filter);



        // WindowManagerによるViewの追加 ///////////////
        windowManager.addView(root_view, params);
        audioSetup(); audioPlay();

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

        audioStop();
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
    private void initialize(){
        mediaPlayer = new MediaPlayer();
        inflater = LayoutInflater.from(this);
    }
    private void findViews(){
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        root_view = inflater.inflate(R.layout.overlay_layout, null);
        view = root_view.findViewById(R.id.overlay_layout);
        classNameView = view.findViewById(R.id.text_overlay);
        requestButton = view.findViewById(R.id.request_button_overlay);
        cancelButton = view.findViewById(R.id.cancel_button_overlay);
        shareButton = view.findViewById(R.id.share_button_overlay);
    }

    private void setLayoutParams(){
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

//        params.gravity = Gravity.END | Gravity.TOP; // 左右逆
//        params.gravity = Gravity.TOP | Gravity.START;
//        params.gravity = Gravity.BOTTOM | Gravity.START; // 上下逆
//        params.gravity = Gravity.END | Gravity.BOTTOM; // 上下左右逆
    }

    private void setListeners(){
        root_view.setOnTouchListener( new OnFrameMovingListener() );

        if(requestButton != null){
            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debug", "onClick");
                    {
                        Log.d(TAG, "Zoom URL:"+classData.getOnlineUrl());
                        if(classData.getOnlineUrl().equals("") || classData.getOnlineUrl() ==null){
                            Toast.makeText(getApplicationContext(), "この講義にはURLが保存されていません。", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(!classData.getOnlineFlag()){
                            Toast.makeText(getApplicationContext(), "この講義はオンライン講義ではありません。", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent toChrome = new Intent(Intent.ACTION_VIEW, Uri.parse(classData.getOnlineUrl()));
                        toChrome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toChrome.setPackage("com.android.chrome");
                        try {
                            getApplicationContext().startActivity(toChrome);
                        } catch (ActivityNotFoundException ex) {
                            try{
                                toChrome.setPackage(null);
                                getApplicationContext().startActivity(toChrome);
                            } catch (ActivityNotFoundException e){
                                Toast.makeText(getApplicationContext(), "講義URLが不適切です。終了します。", Toast.LENGTH_LONG).show();
                                onDestroy();
                            }
                        }
                    }
//                stopSelf();
                    onDestroy();
                }
            });
        }

        if(cancelButton != null){
            cancelButton.setOnClickListener(v -> {
                onDestroy();
            });
        }

        if(shareButton != null){
            shareButton.setOnClickListener(v -> {
                Log.d(TAG,"[onClick] shareButton");
                windowManager.removeView(root_view);
                audioStop();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, classData.getOnlineUrl());
                shareIntent.setType("text/html");
                Intent sendIntent = Intent.createChooser(shareIntent, "URLの共有...");
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sendIntent);
            });
        }

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
    }
    private void audioSetup(){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_test);
//        MainActivity.this.setVolumeControlStream(AudioManager.STREAM_ALARM);
    }

    private void audioPlay(){
        if (mediaPlayer == null) {
//            // audio ファイルを読出し
//            if (audioSetup()){
//                Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
//            }
//            else{
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
//            }
        }
        else{
            // 繰り返し再生する場合
//            mediaPlayer.stop();
//            mediaPlayer.reset();
            // リソースの解放
//            mediaPlayer.release();
        }
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "[audio Completion]");
            mediaPlayer.start();
        });
    }

    private void audioStop(){
        if(mediaPlayer == null) return;
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}