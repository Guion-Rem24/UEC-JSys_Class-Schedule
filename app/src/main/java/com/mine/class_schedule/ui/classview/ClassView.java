package com.mine.class_schedule.ui.classview;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.View.EditClassActivity;
import com.mine.class_schedule.View.EditClassFromOutsideActivity;
import com.mine.class_schedule.View.MainActivity;
import com.mine.class_schedule.ViewModel.EditClassFromOutsideViewModel;

public class ClassView extends ConstraintLayout implements View.OnClickListener{

    private final String TAG = "ClassView-";
    private int mDay;
    private int mPeriod;
    private byte posId;
    private Context mContext;
    private TextView nameView;
    private TextView placeView;
    private MyClass classData;

    private final long LONG_PRESSED_TIME = 800; // milli sec

    private Handler handler;
    private Runnable longPressedReceiver;
    private Vibrator vibrator;
    private boolean longPressed = false;

    private int mWidth;
    private int mHeight;

    private boolean outsideActivityFlag = false;

    private EditClassFromOutsideViewModel viewModel;

    public ClassView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public ClassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    private void init(Context context){
        Log.d(TAG, "[init]");
        setOnClickListener(this);
        posId = 0x00;

        handler = new Handler();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // hover animation via clicked
        StateListAnimator stateListAnimator = AnimatorInflater.loadStateListAnimator(mContext, R.drawable.animation_clicked_hover);
        setStateListAnimator(stateListAnimator);

        longPressedReceiver = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "[LongPressed]");
                longPressed = true;
                setTranslationZ(1000f);
//                setOutlineProvider(new ViewOutlineProvider() {
//                    @Override
//                    public void getOutline(View view, Outline outline) {
//                        outline.setRect(0,0,view.getWidth(),view.getHeight());
//                    }
//                });
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // API 26 未満
                    vibrator.vibrate(300);
                } else {
                    VibrationEffect vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.vibrate(vibrationEffect);
                }
                if(classData.getOnlineFlag()){
                    new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.ic_baseline_connect_without_contact_24)
                            .setTitle("オンライン講義へ接続")
                            .setMessage(String.format("%s のオンライン講義に接続しますか？", classData.getClassName()))
                            .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(classData.getOnlineUrl().equals("") || classData.getOnlineUrl() == null){
                                        Toast.makeText(mContext, "オンライン講義のURLを設定してください。",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Intent toChrome = new Intent(Intent.ACTION_VIEW, Uri.parse(classData.getOnlineUrl()));
                                        toChrome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        toChrome.setPackage("com.android.chrome");
                                        try {
                                            mContext.startActivity(toChrome);
                                        } catch (ActivityNotFoundException ex) {
                                            toChrome.setPackage(null);
                                            mContext.startActivity(toChrome);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("いいえ", null)
                            .show();
                } else {
                    Toast.makeText(mContext, "対面講義です", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "[ACTION_DOWN]");
                this.setPressed(true);
//                setOutlineProvider(new Pressed());
                handler.postDelayed(longPressedReceiver, LONG_PRESSED_TIME);
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "[ACTION_UP]");
                this.setPressed(false);
                handler.removeCallbacks(longPressedReceiver);
                if(!longPressed){
//                    setOutlineProvider(null);
                    onClick(this);
                } else {
                    longPressed = false;
                }
                return true;
            case MotionEvent.ACTION_BUTTON_PRESS:
                Log.d(TAG, "ACTION_BUTTON_PRESS");
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "[onClick]");
        if(!outsideActivityFlag){
            Intent intent = new Intent(mContext, EditClassActivity.class);

            intent.putExtra("ClassPos", (byte) posId);
            intent.putExtra("ClassData", classData);

            ((FragmentActivity) mContext).startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_CLASS);
        } else {
            Log.d(TAG,"extra onClick");
            viewModel.setClassPos(posId);
//            Intent intent = new Intent(mContext, EditClassFromOutsideActivity.class);
//            intent.putExtra("ClassPos", (byte)posId);
//            ((FragmentActivity) mContext).startActivityForResult(intent, EditClassFromOutsideActivity.REQUEST_CODE_SEND_CLASS_POS);
        }
    }

    public void setDayId(int day){
        mDay = day;
        posId = (byte) ((byte)(posId & TYPE_CLASS.DAY_CLEAR_MASK) | TYPE_CLASS.getDay(mDay));
    }
    public void setDayId(byte day){
        posId = (byte)((byte)(posId & TYPE_CLASS.DAY_CLEAR_MASK) | day);
    }
    public void setPeriodId(int period){ // period in range[0,6], but TYPE_CLASS [0xF0, 0xA0];
        mPeriod = period;
        posId = (byte) (posId & TYPE_CLASS.PERIOD_CLEAR_MASK | TYPE_CLASS.getPeriod(mPeriod));
    }
    public void setPeriodId(byte period){
        posId = (byte)((byte)(posId & TYPE_CLASS.PERIOD_CLEAR_MASK) | period);
    }
    public void setPosId(byte pos_){ posId = pos_; }

    public void setClassData(int day, int period){
        setDayId(day);
        setPeriodId(period);
    }

    public void setClass(boolean setDataFlag){
        if(setDataFlag){
            classData = (((MainActivity) mContext)).getHomeFragment().getMyClass(posId);
        }
    }

    public int getDayId(){ return mDay; }
    public int getPeriodId(){ return mPeriod; }
    public byte getPosId(){ return posId; }

    public static int[] convertPos(byte p){ // day, period
        int[] pos_ = new int[2];
        pos_[0] = p & 0x0F;
        pos_[1] = (p & 0xF0) >> 4;
        return pos_;
    }

    public void setNameView(TextView nameview){ nameView = nameview; }
    public void setPlaceView(TextView placeview){ placeView = placeview; }

    public void setName(String name){ nameView.setText(name); }
    public void setName(String name, int color){
        nameView.setText(name);
        nameView.setTextColor(color);
    }
    public void setPlace(String place){ placeView.setText(place); }
    public void setPlace(String place, int color){
        placeView.setText(place);
        placeView.setTextColor(color);
    }

    public String getName(){ return nameView.getText().toString(); }
    public String getPlace(){ return placeView.getText().toString(); }


    public void setViewSize(int width, int height){
        Point displaySize = new Point();

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        setMinWidth(width);
        setMinHeight(height);
        mWidth = width;
        mHeight = height;
    }

    private static class Hover extends ViewOutlineProvider{

        private int width;
        private int height;
        Hover(int w, int h){ width = w; height = h; }

        @Override
        public void getOutline(View view, Outline outline) {
//            outline.setRect(0,0, width, height);
            view.setElevation(1000f);
        }
    }
    private static class Pressed extends  ViewOutlineProvider{

        @Override
        public void getOutline(View view, Outline outline) {
            view.setElevation(-1000f);
        }
    }

    public void updateOnClickListener(View.OnClickListener listener){
        Log.d(TAG,"[updateOnClickListener]");
        outsideActivityFlag = true;
        this.setOnClickListener(listener);
    }

    public void setViewModel(EditClassFromOutsideViewModel viewModel){
        Log.d(TAG, "[setViewModel]");
        this.viewModel = viewModel;
    }
}

