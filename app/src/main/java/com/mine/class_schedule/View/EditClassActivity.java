package com.mine.class_schedule.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mine.class_schedule.EditClassViewModel;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Arrays;
import java.util.Objects;

public class EditClassActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.mine.class_schedule.REPLY";
    public static final String DATA_NAME = "_CLASS_NAME";
    public static final String DATA_PLACE = "_CLASS_PLACE";
    public static final String DATA_FLAG = "_IS_ONLINE";
    public static final String DATA_URL = "_ONLINE_URL";
    public static final String DATA_POS = "_CLASS_POS";

    private final String HINT_NAME = "講義名";
    private final String HINT_PLACE = "講義名を入力...";
    private final String HINT_URL = "オンライン講義のURL...";

    private final String TAG="EditClassActivity";
    private CoordinatorLayout root;
    private Toolbar toolbar;
    private InputMethodManager inputMethodManager;
    private EditClassViewModel editClassViewModel;
    private TextInputEditText editClassPlace, editClassName, editOnlineUrl;
    private TextInputLayout layoutEditName, layoutEditPlace, layoutEditOnlineUrl;
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

    private String  preClassName;
    private String  preClassPlace;
    private String  preOnlineUrl;
    private int     preAlertNum;
    private long[]  preAlertTime;
    private byte    classPos;
    private MyClass classData;
    private boolean classNameChangedFlag = false;
    private boolean classPlaceChangedFlag = false;
    private boolean classOnlineUrlChangedFlag = false;
    private boolean alertNumChangedFlag = false;
    private boolean[] ignoredValue;

    private boolean isOnline = false;


    private EditClassActivity getPointer(){return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViews();
        initialize();



        // Receive MyClass //////////////////////
        Intent intent = getIntent();
        classData = (MyClass) intent.getSerializableExtra("ClassData");

        this.extractData();
        /////////////////////////////////////////



        // TODO: putExtraした後追加
//            preClassPlace = classPlace;
//            preOnlineUrl = classOnlineUrl;



    /** Settings of Show or Hide Alert //////////////////*/
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
                    // 初期化
                    numPicker[finalI].setValue(0);
                    timeTypeSpinner[finalI].setSelection(0);

                    activeAlertPicker[finalI] = false;
                }
            });

            timeTypeSpinner[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "[spinner onItemSelected]: on "+ finalI);
                    Spinner spinner = (Spinner) parent;
                    if(!ignoredValue[finalI]) {
                        numPicker[finalI].setValue(0);
                        setNumberPickerRange(finalI, position);
                    }
                    else {
                        ignoredValue[finalI] = false;
//                        setNumberPickerRange(finalI, timeTypeSpinner[finalI].getSelectedItemPosition());
                    }
//                    Log.d(TAG, spinner+", "+view+", "+position+", "+id);
//                    Toast.makeText(getApplicationContext(), spinner+", "+view+", "+position+", "+id, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "finalI "+finalI+" is "+numPicker[finalI].getValue());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

        ifOnlineBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchingOnline( isChecked, true);
            }
        });




    /** input Text set Listener //////////////////////*/

        editClassName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!TextUtils.isEmpty(editClassName.getText()) || hasFocus) layoutEditName.setHint("");
                else layoutEditName.setHint(HINT_NAME);
                // もし変更があれば
                classNameChangedFlag = TextUtils.equals(preClassName, editClassName.getText());
            }
        });

        editClassPlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!TextUtils.isEmpty(editClassPlace.getText()) || hasFocus) layoutEditPlace.setHint("");
                else layoutEditPlace.setHint(HINT_PLACE);

                classPlaceChangedFlag = TextUtils.equals(preClassPlace, editClassPlace.getText());
            }
        });


        editOnlineUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!TextUtils.isEmpty(editOnlineUrl.getText()) || hasFocus) layoutEditOnlineUrl.setHint("");
                else layoutEditOnlineUrl.setHint(HINT_URL);
                classOnlineUrlChangedFlag = TextUtils.equals(preOnlineUrl, editOnlineUrl.getText());
            }
        });





    /** Alert Settings ///////////////////////////////*/

        addAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "small InActive Alert: "+ smallInactiveAlert()+", alertNum:"+alertNum);
                Log.d(TAG, "active array:"+ activeAlertPicker[0]+","+activeAlertPicker[1]+","+activeAlertPicker[2]);
//                alertLayout[alertNum].startAnimation(inAnimation);
                alertLayout[smallInactiveAlert()].setVisibility(View.VISIBLE);
                // 値の移動
                if(smallInactiveAlert() != alertNum){
                    int i=smallInactiveAlert();
                    int k=smallInactiveAlert()+1;
                    while(i<3 && k<3){
                        if(!activeAlertPicker[k]) {
                            k++;
                            continue;
                        }
                        timeTypeSpinner[i].setSelection(timeTypeSpinner[k].getSelectedItemPosition());
                        setNumberPickerRange(i, timeTypeSpinner[i].getSelectedItemPosition());
                        numPicker[i].setValue(numPicker[k].getValue());
                        ignoredValue[i] = true;

                        Log.d(TAG, "i→k: "+i+"→"+k+", "+numPicker[i].getValue()+","+timeTypeSpinner[i].getSelectedItemPosition());
                        i = k;
                        k++;
                    }
                }
                numPicker[alertNum].setValue(0);
                timeTypeSpinner[alertNum].setSelection(0);
                activeAlertPicker[smallInactiveAlert()] = true;
                if(++alertNum==3) addAlertButton.setClickable(false);

                Log.d(TAG, "[addAlertButton onClick]");
            }
        });


        /**
         * 編集完了ボタン押下時
         */
        compEditFab.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                classNameChangedFlag= true;
                classPlaceChangedFlag = true;
                classOnlineUrlChangedFlag = true;
                Log.d(TAG, "Fab [on Click]");
                Intent replyIntent = new Intent();
                if(classNameChangedFlag){
                    if( !TextUtils.isEmpty(editClassName.getText()) ){
                        classData.setClassName(Objects.requireNonNull(editClassName.getText()).toString());
                    } else {
                        classData.setClassName("");
                    }
                }
                if(classPlaceChangedFlag){
                    if( !TextUtils.isEmpty(editClassPlace.getText()) ){

                        classData.setClassPlace(editClassPlace.getText().toString());

                    } else {
                        classData.setClassPlace("");
                    }
                }
                if(isOnline){
                    if (classOnlineUrlChangedFlag) {
                        if ( !TextUtils.isEmpty(editOnlineUrl.getText()) ) {
                            String url = editOnlineUrl.getText().toString();
                            url = url.replaceAll(" ", "");
                            classData.setOnlineUrl(url);
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
                            classData.setOnlineUrl("");
                        }
                    }
                }
                {
                    classData.setAlertNum(alertNum);
                    classData.setAlerts(getAlerts());
                }
                classData.setOnlineFlag(isOnline);
                replyIntent.putExtra("ClassData", classData);
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });

        setDisplayedValues();

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
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // hide keyboard when touching background
        inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        root.requestFocus();
        return true;
    }

    // TODO: onBackPressed()でisDataChanged()で確認popup

    private void findViews(){
        root = findViewById(R.id.activity_edit_class);
        toolbar = findViewById(R.id.editclass_toolbar);
//        editClassViewModel = new ViewModelProvider(this).get(EditClassViewModel.class);
        editClassName = findViewById(R.id.editext_classname);
        editClassPlace = findViewById(R.id.edittext_place);
        editOnlineUrl = findViewById(R.id.edittext_online_url);
        layoutEditName = findViewById(R.id.editext_classname_layout);
        layoutEditPlace = findViewById(R.id.edittext_place_layout);
        layoutEditOnlineUrl = findViewById(R.id.edittext_online_url_layout);
        compEditFab = findViewById(R.id.fab_finish_editing);
        addAlertButton = findViewById(R.id.button_add_alert);
        ifOnlineBox = (CheckBox) findViewById(R.id.checkBox_ifOnline);
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
    private void initialize(){
        ignoredValue = new boolean[3];
        preAlertTime = new long[3];
    }
    private boolean isDataChanged(){
        return (classNameChangedFlag || classPlaceChangedFlag || classOnlineUrlChangedFlag);
    }

    private int smallInactiveAlert(){
        if(alertNum==3) return -1;
        int result=0;
        for(int i=0;i<3;i++){
            if(!activeAlertPicker[i]){
                result = i;
                break;
            }
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

    private void extractData(){
        if(classData == null){
            throw new NullPointerException();
        }
        preClassName = classData.getClassName();
        preClassPlace = classData.getClassPlace();
        preOnlineUrl = classData.getOnlineUrl();
        isOnline = classData.getOnlineFlag();
        preAlertNum = classData.getAlertNum();
        alertNum = preAlertNum;
//        if (preAlertNum >= 0) System.arraycopy(preAlertTime, 0, preAlertTime, 0, preAlertNum);
//      ->
        preAlertTime = classData.getAlerts();

        classPos = classData.getClassPos();
        Log.d(TAG, "[extract DATA] " +
                         "\nclassPos:   " + classPos +
                         "\nclassName:  " + preClassName +
                         "\nclassPlace: " + preClassPlace+
                         "\nOnline URL: " + preOnlineUrl +
                         "\nisOnline:   " + isOnline +
                         "\npreAlertNum:" + preAlertNum +
                         "\npreAlerts = {"+preAlertTime[0]+", "+preAlertTime[1]+", "+preAlertTime[2]+"}");
        // others also get here;
    }
    private void setDisplayedValues(){
        toolbar.setTitle(String.format("%s %s",TYPE_CLASS.getDayString(classPos), TYPE_CLASS.getPeriodString(classPos)));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editClassName.setText(preClassName);
        editClassPlace.setText(preClassPlace);
        if(isOnline) switchingOnline(true,false);
        editOnlineUrl.setText(preOnlineUrl);
        for(int i=0;i<preAlertNum; i++){
            alertLayout[i].setVisibility(View.VISIBLE);
            activeAlertPicker[i]=true;
            int spinnerPos;
            int time;
            if(preAlertTime[i]<60) {//分前
                spinnerPos = 1;
                time = (int)preAlertTime[i];
            } else {
                spinnerPos = 0;
                time = (int)(preAlertTime[i]/60);
            }
            setNumberPickerRange(i,spinnerPos);
            timeTypeSpinner[i].setSelection(spinnerPos);
            numPicker[i].setValue(time);
            ignoredValue[i] = true;
        }
        if(preAlertNum == 3){
            addAlertButton.setClickable(false);
        }

    }
    private void switchingOnline(boolean flag, boolean animFlag){
        if(flag){
            ifOnlineBox.setChecked(true);
            editClassPlace.setEnabled(false);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.disableColor, null));
            } else {
                layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.disableColor));
            }
            if(animFlag) layoutEditOnlineUrl.startAnimation(inAnimation);
            layoutEditOnlineUrl.setVisibility(View.VISIBLE);
            editClassPlace.setText("オンライン講義");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editClassPlace.setTextColor(getResources().getColor(R.color.black, null));
            } else {
                editClassPlace.setTextColor(getResources().getColor(R.color.black));
            }
            isOnline = true;
            classOnlineUrlChangedFlag = true;
        } else {
            ifOnlineBox.setChecked(false);
            editClassPlace.setEnabled(true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.white,null));
            } else {
                layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.white));
            }
//                    layoutEditOnlineUrl.setVisibility(View.INVISIBLE);
            if(animFlag) layoutEditOnlineUrl.startAnimation(outAnimation);
            layoutEditOnlineUrl.setVisibility(View.GONE);
            editClassPlace.setText("");
            isOnline = false;
            classOnlineUrlChangedFlag = true;
        }
    }

    private long[] getAlerts(){
        long[] alerts = new long[3];
        int ctr = 0;
        for(int i=0;i<3;i++){
            if(alertLayout[i].getVisibility() == View.GONE) continue;
            if(timeTypeSpinner[i].getSelectedItemPosition() == 0) { // 時間前
                alerts[ctr] = 60 * numPicker[i].getValue();
            } else { // 分前
                alerts[ctr] = numPicker[i].getValue();
            }
            ctr++;
        }
        for(int i=ctr;i<3;i++){
            alerts[i] = 99;
        }
        Arrays.sort(alerts);
        return alerts;
    }

    @Override
    public void onBackPressed(){
        if(isDataChanged()){
            new AlertDialog.Builder(EditClassActivity.this)
                .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle("注意")
                    .setMessage("データが変更されています。\n保存されませんがよろしいですか？")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            EditClassActivity.this.finish();
                        }
                    })
                    .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
//            new AlertDialog.Builder(EditClassActivity.this)
////                                    .setIcon(R. )
//                    .setTitle("注意")
//                    .setMessage("オンライン講義のURLがありません．\n対面講義として扱われますが，\n続行しますか？")
//                    .setPositiveButton("はい", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            replyIntent.putExtra(EXTRA_REPLY + DATA_URL, "");
//                        }
//                    })
//                    .setNegativeButton("いいえ", null)
////                                    .create()
//                    .show();

        }
        else {
            super.onBackPressed();
        }
        Log.d(TAG,"[onBackPressed]");
    }

}

/**
 * MinemuraKohei 峯村晃平さんがあなたを予約されたZoomミーティングに招待しています。
 *
 * トピック: MinemuraKohei 峯村晃平のパーソナルミーティングルーム
 *
 * Zoomミーティングに参加する
 * https://uec-tokyo.zoom.us/j/9513830171?pwd=UUE5K3IrZWMwd0Fqdk44MytFeTkwZz09
 *
 * ミーティングID: 951 383 0171
 * パスコード: 0836126968
 */