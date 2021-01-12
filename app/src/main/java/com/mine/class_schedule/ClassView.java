package com.mine.class_schedule;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

//import com.mine.class_schedule.TYPE_CLASS;

public class ClassView extends androidx.appcompat.widget.AppCompatTextView implements View.OnClickListener{
    private final String TAG = "ClassView-"+this.getText().toString();
    private int mDay;
    private int mPeriod;
    private byte posId;
    private Context mContext;

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
        setOnClickListener(this);
        posId = 0x00;
//        Log.d(TAG, "id:"+getId());
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(getA, TAG+" [onClick]");
        Log.d(TAG, "[onCLick] : "+mPeriod+":"+mDay+",pos:("+convertPos(posId)[0]+","+convertPos(posId)[1]+")");
        Toast.makeText(mContext, "id:"+getId(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(mContext, EditClassActivity.class);
        intent.putExtra("ClassName", getText().toString());
        mContext.startActivity(intent);
    }

    public void setDayId(int day){
        mDay = day;
//        Log.d(TAG,"mDay byte:"+(byte)mDay);
        posId = (byte) ((byte)(posId & TYPE_CLASS.DAY_CLEAR_MASK) | TYPE_CLASS.getDay(mDay));
        Log.d("mDay:"+mDay, ":"+TYPE_CLASS.castToString(posId));
    }
    public void setDayId(byte day){
        posId = (byte)((byte)(posId & TYPE_CLASS.DAY_CLEAR_MASK) | day);
    }
    public void setPeriodId(int period){ // period in range[0,6], but TYPE_CLASS [0xF0, 0xA0];
        mPeriod = period;
//        Log.d(TAG, "mPeriod:"+mPeriod+", mPeriod byte:"+((byte)(15-mPeriod) << 4)+", mPeriod getPeriod: "+(TYPE_CLASS.getPeriod(mPeriod)));
        posId = (byte) (posId & TYPE_CLASS.PERIOD_CLEAR_MASK | TYPE_CLASS.getPeriod(mPeriod));
        Log.d("mPeriod:"+mPeriod, ":"+(TYPE_CLASS.castToString(posId)));
    }
    public void setPeriodId(byte period){
        posId = (byte)((byte)(posId & TYPE_CLASS.PERIOD_CLEAR_MASK) | period);
    }
    public void setPosId(byte pos_){ posId = pos_; }

    public int getDayId(){ return mDay; }
    public int getPeriodId(){ return mPeriod; }
    public byte getPosId(){ return posId; }

    public static int[] convertPos(byte p){ // day, period
        int[] pos_ = new int[2];
        pos_[0] = p & 0x0F;
        pos_[1] = (p & 0xF0) >> 4;
        return pos_;
    }

}
