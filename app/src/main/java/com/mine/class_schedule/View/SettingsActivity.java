package com.mine.class_schedule.View;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements  PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        /*if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }*/
        this.initializeToolbar(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // レイアウトルートの背景をテーマ設定の値によって変更
        RelativeLayout root = findViewById(R.id.relative_settings);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (defaultSharedPreferences.getString("preference_theme", getString(R.string.default_value_preference_theme))) {
            case "light":
                root.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case "dark":
                root.setBackgroundColor(Color.parseColor("#000000"));
                break;
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        // Fragmentの切り替えと、addToBackStackで戻るボタンを押した時に前のFragmentに戻るようにする
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance("preference_root")) //Preference_Fragment.newInstance("preference_root"))
                .addToBackStack(null)
                .commit();
        return true;
    }

    private void initializeToolbar(Bundle savedInstanceState){
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar actionbar = this.getSupportActionBar();
        if(actionbar == null){
            return;
        }
        if (savedInstanceState == null) {
            // トップ画面のFragmentを表示
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, SettingsFragment.newInstance("preference_root")) //Preference_Fragment.newInstance("preference_root"))
                    .commit();
        }
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}