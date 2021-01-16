package com.mine.class_schedule.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

public class EditClassActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.mine.class_schedule.REPLY";
    public static final String DATA_NAME = "_CLASS_NAME";
    public static final String DATA_PLACE = "_CLASS_PLACE";
    public static final String DATA_FLAG = "_IS_ONLINE";
    public static final String DATA_URL = "_ONLINE_URL";
    public static final String DATA_POS = "_CLASS_POS";

    private final String TAG="EditClassActivity";
    private ConstraintLayout root;
    private TextInputEditText editClassPlace, editClassName, editOnlineUrl;
    private TextInputLayout layoutEditPlace, layoutEditOnlineUrl;
    private CheckBox ifOnlineBox;
    private Animation inAnimation;
    private Animation outAnimation;
    private ImageButton addAlertButton;
    private ConstraintLayout[] alertLayout;
    private ImageButton[] deleteAlertButton;
    private Spinner[] timeTypeSpinner;
    private NumberPicker[] numPicker;
    private boolean[] activeAlertPicker;
    private int alertNum=0;
    private FloatingActionButton compEditFab;
    private final int ID_ONLINE_EDIT_TEXT = 1212;

    private String preClassName;
    private String preClassPlace;
    private String preOnlineUrl;
    private byte classPos;
    private boolean classNameChangedFlag = false;
    private boolean classPlaceChangedFlag = false;
    private boolean classOnlineUrlChangedFlag = false;

    private boolean isOnline = false;


    private EditClassActivity getPointer(){return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        findViews();

        for(int i=0;i<3;i++){
            int finalI = i;
            deleteAlertButton[i] = alertLayout[i].findViewById(R.id.button_delete);
            timeTypeSpinner[i] = alertLayout[i].findViewById(R.id.spinner_type_time);
            numPicker[i] = alertLayout[i].findViewById(R.id.numpick_time);
            activeAlertPicker[i] = false;

            deleteAlertButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(alertNum==3) addAlertButton.setClickable(true);
                    alertNum--;
//                    alertLayout[finalI].startAnimation(outAnimation);
                    alertLayout[finalI].setVisibility(View.GONE);
                    activeAlertPicker[finalI] = false;
                }
            });

            timeTypeSpinner[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Spinner spinner = (Spinner) parent;
//                    Log.d(TAG, spinner+", "+view+", "+position+", "+id);
//                    Toast.makeText(getApplicationContext(), spinner+", "+view+", "+position+", "+id, Toast.LENGTH_LONG).show();
                    setNumberPickerRange(finalI, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }


        Intent intent = getIntent();
        String className = intent.getStringExtra("ClassName");
//        Toast.makeText(getApplicationContext(), "put Value:"+className, Toast.LENGTH_LONG).show();
        Log.d(TAG, "put Value:"+className);
        if(className != null){
            editClassName.setText(className);
            preClassName = className;
        }
        classPos = intent.getByteExtra("ClassPos", (byte)0xFF);
        Log.d(TAG, "classPos: "+TYPE_CLASS.castToString(classPos));
        // TODO: putExtraした後追加
//            preClassPlace = classPlace;
//            preOnlineUrl = classOnlineUrl;

        ifOnlineBox = (CheckBox) findViewById(R.id.checkBox_ifOnline);
        ifOnlineBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editClassPlace.setEnabled(false);
                    layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.material_on_primary_disabled,null));
                    layoutEditOnlineUrl.startAnimation(inAnimation);
                    layoutEditOnlineUrl.setVisibility(View.VISIBLE);
                    isOnline = true;
                    classOnlineUrlChangedFlag = true;
                } else {
                    editClassPlace.setEnabled(true);
                    layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.white,null));
//                    layoutEditOnlineUrl.setVisibility(View.INVISIBLE);
                    layoutEditOnlineUrl.startAnimation(outAnimation);
                    layoutEditOnlineUrl.setVisibility(View.GONE);
                    isOnline = false;
                    classOnlineUrlChangedFlag = true;
                }
            }
        });

        editClassName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // もし変更があれば
                classNameChangedFlag = TextUtils.equals(preClassName, editClassName.getText());
            }
        });

        addAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "alertNum: "+ alertNum);
//                alertLayout[alertNum].startAnimation(inAnimation);
                alertLayout[smallInactiveAlert()].setVisibility(View.VISIBLE);
                activeAlertPicker[smallInactiveAlert()] = true;
                if(++alertNum==3) addAlertButton.setClickable(false);
            }
        });


        /**
         * 編集完了ボタン押下時
         */
        compEditFab.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Fab [on Click]");
                Intent replyIntent = new Intent();
                if(classNameChangedFlag){
                    if( !TextUtils.isEmpty(editClassName.getText()) ){
                        replyIntent.putExtra(EXTRA_REPLY+DATA_NAME, editClassName.getText().toString());
                    } else {
                        replyIntent.putExtra(EXTRA_REPLY+DATA_NAME, "");
                    }
                }
                if(classPlaceChangedFlag){
                    if( !TextUtils.isEmpty(editClassPlace.getText()) ){
                        replyIntent.putExtra(EXTRA_REPLY+DATA_PLACE, editClassPlace.getText().toString());
                    } else {
                        replyIntent.putExtra(EXTRA_REPLY+DATA_PLACE, "");
                    }
                }
                if(isOnline){
                    if (classOnlineUrlChangedFlag) {
                        if ( !TextUtils.isEmpty(editOnlineUrl.getText()) ) {
                            replyIntent.putExtra(EXTRA_REPLY + DATA_URL, editOnlineUrl.getText().toString());
                        } else {
                            /* TODO: show()した後，すぐにfinish()されてしまう．
                            new AlertDialog.Builder(EditClassActivity.this)
//                                    .setIcon(R. )
                                    .setTitle("注意")
                                    .setMessage("オンライン講義のURLがありません．\n対面講義として扱われますが，\n続行しますか？")
                                    .setPositiveButton("はい", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            replyIntent.putExtra(EXTRA_REPLY + DATA_URL, "");
                                        }
                                    })
                                    .setNegativeButton("いいえ", null)
//                                    .create()
                                    .show();

                             */
                            replyIntent.putExtra(EXTRA_REPLY + DATA_URL, "");
                        }
                    }
                }
                replyIntent.putExtra(EXTRA_REPLY+DATA_FLAG, isOnline);
                replyIntent.putExtra(EXTRA_REPLY+DATA_POS, classPos);
                Log.d(TAG, "classPos: "+TYPE_CLASS.castToString(classPos));
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });

        Log.v(TAG, "[onCreate]");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v(TAG, "[onResume]");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.v(TAG,"[onPause]");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.v(TAG,"[onStart]");
    }

    // TODO: onBackPressed()でisDataChanged()で確認popup

    private void findViews(){
        root = findViewById(R.id.activity_edit_class);
        editClassName = findViewById(R.id.editext_classname);
        editClassPlace = findViewById(R.id.edittext_place);
        editOnlineUrl = findViewById(R.id.edittext_online_url);
        layoutEditPlace = findViewById(R.id.edittext_place_layout);
        layoutEditOnlineUrl = findViewById(R.id.edittext_online_url_layout);
        compEditFab = findViewById(R.id.fab_finish_editing);
        addAlertButton = findViewById(R.id.button_add_alert);
        alertLayout = new ConstraintLayout[3];
        deleteAlertButton = new ImageButton[3];
        numPicker = new NumberPicker[3];
        timeTypeSpinner = new Spinner[3];
        activeAlertPicker = new boolean[3];
        alertLayout[0] = findViewById(R.id.edit_alert1);
        alertLayout[1] = findViewById(R.id.edit_alert2);
        alertLayout[2] = findViewById(R.id.edit_alert3);
        inAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.in_animation);
        outAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.out_animation);
    }
    private boolean isDataChanged(){
        return (classNameChangedFlag && classPlaceChangedFlag && classOnlineUrlChangedFlag);
    }

    private int smallInactiveAlert(){
        if(alertNum==3) return -1;
        int result=0;
        for(int i=0;i<3;i++){
            if(activeAlertPicker[i]) continue;
            result = i;
        }
        return result;
    }

    private void setNumberPickerRange(int alertNumber, int posSpinner){
        int max;
        switch(posSpinner){
            case 0: // 時間前
                max = 24;
                break;
            case 1: // 分前
                max = 59;
                break;
            default:
                throw new IllegalStateException();
        }
        numPicker[alertNumber].setMaxValue(max);
        numPicker[alertNumber].setMinValue(0);
    }
}