package com.example.cataloguemoviefinal.fragment;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.cataloguemoviefinal.R;

public class AlarmPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
	
	// Key
	private String DAILY_REMINDER;
	private String TODAY_RELEASE_DATE_MOVIE_REMINDER;
	// Preference in preference screen, which is useful for storing boolean into SharedPreferences
	private SwitchPreference dailyReminderPreference;
	private SwitchPreference movieReleaseTodayReminderPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set values from key in SwitchPreference items
		DAILY_REMINDER = getResources().getString(R.string.daily_reminder_key);
		TODAY_RELEASE_DATE_MOVIE_REMINDER = getResources().getString(R.string.release_movie_reminder_key);
		
		// find SwitchPreference object based on key
		dailyReminderPreference = (SwitchPreference) findPreference(DAILY_REMINDER);
		dailyReminderPreference.setOnPreferenceChangeListener(this); // Set listener untuk menghandle event change buat SwitchPreference
		movieReleaseTodayReminderPreference = (SwitchPreference) findPreference(TODAY_RELEASE_DATE_MOVIE_REMINDER);
		movieReleaseTodayReminderPreference.setOnPreferenceChangeListener(this);
		
	}
	
	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.setting_reminder_preferences); // Add xml ke class AlarmPreferenceFragment
	}
	
	
	// Method tsb berguna untuk mengganti mode key true atau false
	@Override
	public boolean onPreferenceChange(Preference preference, Object object) {
		return true;
	}
}
