package com.mine.class_schedule.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.mine.class_schedule.R;
import com.mine.class_schedule.TimePreference;
import com.mine.class_schedule.View.TimeDialongPrefFragCompat;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";
    public static SettingsFragment newInstance(String rootKey){
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "[onCreatePreferences] rootKey:"+rootKey);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        switch(rootKey){
            case "preference_appearance":
                onCreateAppearancePreferences();
                break;
            case "preference_other" :
                break;
            case "preference_root":
                onCreatePreferenceRoot();
        }
    }

    private void onCreatePreferenceRoot(){
        Log.d(TAG, "[onCreatePreferenceRoot]");

        TimePreference[] periodTimePref = new TimePreference[5];
        for(int i=0;i<5;i++){
            String periodKey = "period"+(i+1);
            periodTimePref[i] = findPreference(periodKey);
            if(periodTimePref[i] != null){
                Log.d(TAG, i+" getParent: "+periodTimePref[i].getParent().getClass().getName()
                        +" getContext: "+periodTimePref[i].getContext().getClass().getName() );

            }
        }

    }

    private void onCreateAppearancePreferences() {
        Log.d(TAG, "[onCreateAppearancePreferences]");
        // テーマ設定の現在の値をSummaryに表示
        ListPreference themePreference = findPreference("preference_theme");
        Objects.requireNonNull(themePreference).setSummary(themePreference.getEntry());
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int indexOfValue = themePreference.findIndexOfValue(String.valueOf(newValue));
                themePreference.setSummary(indexOfValue >= 0 ? themePreference.getEntries()[indexOfValue] : null);
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // ActionBarのタイトルに現在表示中のPreferenceScreenのタイトルをセット
        String rootKey = requireArguments().getString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT);
        requireActivity().setTitle(findPreference(rootKey).getTitle());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference pref){
        if (pref instanceof TimePreference){
            DialogFragment dialogFragment = TimeDialongPrefFragCompat.newInstance(pref.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), null);
        } else super.onDisplayPreferenceDialog(pref);
    }
}