package com.mine.class_schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// TODO:ClassViewのIDが不明
public class EditClassActivity extends AppCompatActivity {
    private final String TAG="EditClassActivity";
    private ConstraintLayout root;
    private TextInputEditText editPlace, editClassName, editOnlineUrl;
    private TextInputLayout layoutEditPlace, layoutEditOnlineUrl;
    private CheckBox ifOnlineBox;
    private Animation inAnimation;
    private Animation outAnimation;
    private final int ID_ONLINE_EDIT_TEXT = 1212;

    private EditClassActivity getPointer(){return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        root = findViewById(R.id.activity_edit_class);
        editClassName = findViewById(R.id.editext_classname);
        editPlace = findViewById(R.id.edittext_place);
        editOnlineUrl = findViewById(R.id.edittext_online_url);
        layoutEditPlace = findViewById(R.id.edittext_place_layout);
        layoutEditOnlineUrl = findViewById(R.id.edittext_online_url_layout);
        inAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.in_animation);
        outAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.out_animation);


        Intent intent = getIntent();
        String className = intent.getStringExtra("ClassName");
//        Toast.makeText(getApplicationContext(), "put Value:"+className, Toast.LENGTH_LONG).show();
        Log.d(TAG, "put Value:"+className);
        if(className != null){
            editClassName.setText(className);
        }

        ifOnlineBox = (CheckBox) findViewById(R.id.checkBox_ifOnline);
        ifOnlineBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editPlace.setEnabled(false);
                    layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.teal_700,null));
                    layoutEditOnlineUrl.startAnimation(inAnimation);
                    layoutEditOnlineUrl.setVisibility(View.VISIBLE);

                } else {
                    editPlace.setEnabled(true);
                    layoutEditPlace.setBackgroundColor(getResources().getColor(R.color.white,null));
//                    layoutEditOnlineUrl.setVisibility(View.INVISIBLE);
                    layoutEditOnlineUrl.startAnimation(outAnimation);
                    layoutEditOnlineUrl.setVisibility(View.GONE);


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
}