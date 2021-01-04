package com.mine.class_schedule.ui;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.mine.class_schedule.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance(String rootKey){
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        switch(rootKey){
            case "preference_appearance":
                onCreateAppearancePreferences();
                break;
            case "preference_other" :
                break;
        }
    }

    private void onCreateAppearancePreferences() {
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
}