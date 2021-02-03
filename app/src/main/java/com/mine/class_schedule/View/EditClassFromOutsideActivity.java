package com.mine.class_schedule.View;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mine.class_schedule.AlarmIntegrator;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.ViewModel.EditClassFromOutsideViewModel;
import com.mine.class_schedule.ViewModel.EditClassViewModel;
import com.mine.class_schedule.ui.classview.PopupClassTableView;
import com.mine.class_schedule.ui.classview.TYPE_CLASS;

import java.util.Arrays;
import java.util.Objects;

public class EditClassFromOutsideActivity extends AppCompatActivity {

    private final String HINT_NAME = "講義名";
    private final String HINT_PLACE = "講義名を入力...";
    private final String HINT_URL = "オンライン講義のURL...";

    public static final int REQUEST_CODE_SEND_CLASS_POS = -1;

    private final String TAG="FromOutsideActivity";
    private CoordinatorLayout root;
    private Toolbar toolbar;
    private TextView selectedClassName;
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
    private AlarmIntegrator alarmIntegrator;

    private String  preClassName;
    private String  preClassPlace;
    private String  preOnlineUrl;
    private int     preAlertNum;
    private long[]  preAlarmTime;
    private long[]  alertTime;
    private byte    classPos;
    private MyClass classData;
    private boolean classNameChangedFlag = false;
    private boolean classPlaceChangedFlag = false;
    private boolean classOnlineUrlChangedFlag = false;
    private boolean alertNumChangedFlag = false;
    private boolean[] ignoredValue;

    private boolean isOnline = false;


    private EditClassFromOutsideViewModel viewModel;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private View layout;
    private PopupClassTableView table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class_from_outside);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        switch(intent.getAction()){
            case Intent.ACTION_SEND:
                Log.d(TAG, "[getAction] ACTION_SEND");
                preOnlineUrl = intent.getCharSequenceExtra(Intent.EXTRA_TEXT).toString();
                break;
            case Intent.ACTION_VIEW:
                Log.d(TAG, "[getAction] ACTION_VIEW");
                preOnlineUrl = intent.getCharSequenceExtra(Intent.EXTRA_TEXT).toString();
                break;
            case Intent.ACTION_PROCESS_TEXT: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "[getAction] ACTION_PROCESS_TEXT");
                    preOnlineUrl = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
                }
            }
            default:
                break;
        }

        findViews();
        initialize();

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
                Intent replyIntent = new Intent(getApplicationContext(), MainActivity.class);
                if(classPos == (byte) 0xFF){
                    setDialogBuilder();
                    dialog = builder.create();
                    dialog.show();
                    return;
                }
                classData.setClassPos(classPos);
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
                AlertDialog.Builder onlineStartBuilder = new AlertDialog.Builder(EditClassFromOutsideActivity.this);
                onlineStartBuilder.setMessage("今すぐ講義へ接続しますか？")
                        .setIcon(R.drawable.ic_baseline_connect_without_contact_24)
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(replyIntent);
                            }
                        });
                AlertDialog onlineDialog = onlineStartBuilder.create();

                new AlertDialog.Builder(EditClassFromOutsideActivity.this)
                        .setTitle("注意")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setMessage("すでにデータが存在する場合、上書きされますがよろしいですか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.insert(classData);
                                if(classData.getOnlineFlag() && !classData.getOnlineUrl().equals("")){
                                    onlineDialog.show();
                                } else {
                                    startActivity(replyIntent);
                                }

                            }
                        })
                        .setNegativeButton("いいえ", null)
                        .create()
                        .show();
            }
        });

        setDisplayedValues();

        /** test */
        ImageButton sampleButton = findViewById(R.id.button_sample);
        sampleButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setDialogBuilder();
                dialog = builder.create();
                dialog.show();
            }
        });

//        viewModel.getTmpClass().observe(this, new Observer<MyClass>() {
//            @Override
//            public void onChanged(MyClass myClass) {
//                if(myClass == null) return;
//                if(!myClass.getClassName().equals("")){
//                    new AlertDialog.Builder(EditClassFromOutsideActivity.this)
//                                .setTitle("注意")
//                                .setIcon(R.drawable.ic_baseline_warning_24)
//                                .setMessage("すでにデータが存在します。上書きされますがよろしいですか？")
//                                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String classStr = String.format("%s%s",TYPE_CLASS.getDayString(myClass.getClassPos()),TYPE_CLASS.getPeriodString(myClass.getClassPos()));
//                                        selectedClassName.setText(classStr);
//                                        selectedClassName.setTextColor(getResources().getColor(R.color.black));
//                                        classPos = myClass.getClassPos();
//                                    }
//                                })
//                                .setNegativeButton("いいえ", null)
//                                .create()
//                                .show();
//                }
//            }
//        });

        /** ClassView からの変更を検知，Textを変更*/
        viewModel.getClassPos().observe(this, new Observer<Byte>() {
            @Override
            public void onChanged(Byte aByte) {
                Log.d(TAG, "[onChanged] get id:"+TYPE_CLASS.castToString(aByte));
                if(dialog != null){
                    dialog.cancel();
                    dialog = null;
                }
                if(aByte == (byte)0xFF){
                    selectedClassName.setText("講義が選択されていません");
                    selectedClassName.setTextColor(getResources().getColor(R.color.disableColor));
                    classPos = (byte) 0xFF;
                } else {
                    // すでに記録されている場合

//                    viewModel.signalToGetMyClass(aByte);
//                    if(!viewModel.getMyClass(aByte).getClassName().equals("")){
//                        new AlertDialog.Builder(EditClassFromOutsideActivity.this)
//                                .setTitle("注意")
//                                .setIcon(R.drawable.ic_baseline_warning_24)
//                                .setMessage("すでにデータが存在します。上書きされますがよろしいですか？")
//                                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String classStr = String.format("%s%s",TYPE_CLASS.getDayString(aByte),TYPE_CLASS.getPeriodString(aByte));
//                                        selectedClassName.setText(classStr);
//                                        selectedClassName.setTextColor(getResources().getColor(R.color.black));
//                                        classPos = aByte;
//                                    }
//                                })
//                                .setNegativeButton("いいえ", null)
//                                .create()
//                                .show();
//                    }
                    String classStr = String.format("%s%s",TYPE_CLASS.getDayString(aByte),TYPE_CLASS.getPeriodString(aByte));
                    selectedClassName.setText(classStr);
                    selectedClassName.setTextColor(getResources().getColor(R.color.black));
                    classPos = aByte;
                }
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
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // hide keyboard when touching background
        inputMethodManager.hideSoftInputFromWindow(root.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        root.requestFocus();
        return true;
    }
    private void findViews(){
        root = findViewById(R.id.activity_edit_class_outside);
        toolbar = findViewById(R.id.editclass_outside_toolbar);
//        editClassViewModel = new ViewModelProvider(this).get(EditClassViewModel.class);
        selectedClassName = findViewById(R.id.text_selected_class);
        editClassName = findViewById(R.id.editext_outside_classname);
        editClassPlace = findViewById(R.id.edittext_outside_place);
        editOnlineUrl = findViewById(R.id.edittext_outside_online_url);
        layoutEditName = findViewById(R.id.editext_outside_classname_layout);
        layoutEditPlace = findViewById(R.id.edittext_outside_place_layout);
        layoutEditOnlineUrl = findViewById(R.id.edittext_outside_online_url_layout);
        compEditFab = findViewById(R.id.fab_outside_finish_editing);
        addAlertButton = findViewById(R.id.button_outside_add_alert);
        ifOnlineBox = (CheckBox) findViewById(R.id.checkBox_outside_ifOnline);
        alertLayout = new ConstraintLayout[3];
        deleteAlertButton = new ImageButton[3];
        numPicker = new NumberPicker[3];
        timeTypeSpinner = new Spinner[3];
        activeAlertPicker = new boolean[3];
        alertLayout[0] = findViewById(R.id.edit_outside_alert1);
        alertLayout[1] = findViewById(R.id.edit_outside_alert2);
        alertLayout[2] = findViewById(R.id.edit_outside_alert3);
        inAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.in_animation);
        outAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.out_animation);
    }
    private void initialize(){
        classData = new MyClass();
        ignoredValue = new boolean[3];
        alertTime = new long[3];
        preAlarmTime = new long[3];
        alarmIntegrator = new AlarmIntegrator(this);
        viewModel = new ViewModelProvider(this).get(EditClassFromOutsideViewModel.class);
        builder = new AlertDialog.Builder(EditClassFromOutsideActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.popup_layout,null);
            table = layout.findViewById(R.id.popup_classtable_view);
            table.setViewModel(viewModel);
            table.updateOnClickListeners(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.d(TAG,"View: "+v.getClass().toString());
                }
            });
    }

    private void setDialogBuilder(){
        builder.setView(layout)
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
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
        }
        classOnlineUrlChangedFlag = true;
    }

    private void setDisplayedValues(){
//        toolbar.setTitle(String.format("%s %s", TYPE_CLASS.getDayString(classPos), TYPE_CLASS.getPeriodString(classPos)));
        toolbar.setTitle("新規の講義を設定");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switchingOnline(true,false);
        editOnlineUrl.setText(preOnlineUrl);
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
        if(alertNum < preAlertNum){
            for(int i=alertNum;i<preAlertNum;i++){
                Log.d(TAG, String.format("i:%d canceled", i));
                alarmIntegrator.cancelAlarmOf(classData, i);
            }
        }

        return alerts;
    }

    @Override
    public void onBackPressed(){
        if(isDataChanged()){
            new AlertDialog.Builder(EditClassFromOutsideActivity.this)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle("注意")
                    .setMessage("データが変更されています。\n保存されませんがよろしいですか？")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            EditClassFromOutsideActivity.this.finish();
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