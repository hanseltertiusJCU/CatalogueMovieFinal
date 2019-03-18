package com.example.cataloguemoviefinal.fragment;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.alarm.DailyReminderAlarmReceiver;
import com.example.cataloguemoviefinal.alarm.ReleaseTodayReminderAlarmReceiver;


public class AlarmPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
	
	// Key
	private String DAILY_REMINDER;
	private String TODAY_RELEASE_DATE_MOVIE_REMINDER;
	// Preference in preference screen, which is useful for storing boolean into SharedPreferences
	private SwitchPreference dailyReminderPreference;
	private SwitchPreference movieReleaseTodayReminderPreference;
	// Alarm receiver object
	private DailyReminderAlarmReceiver dailyReminderAlarmReceiver;
	private ReleaseTodayReminderAlarmReceiver releaseTodayReminderAlarmReceiver;
	
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
	
	
	// Method tsb berguna ketika value dari Preference itu diganti
	@Override
	public boolean onPreferenceChange(Preference preference, Object object) {
		
		// Dapatin key value dari preference object
		String preferenceKey = preference.getKey();
		
		// Dapatin state dari object
		boolean objectState = (boolean) object;
		
		// Buat daily alarm receiver
		dailyReminderAlarmReceiver = new DailyReminderAlarmReceiver();
		
		// Buat release today alarm receiver
		releaseTodayReminderAlarmReceiver = new ReleaseTodayReminderAlarmReceiver();
		
		// Cek jika preference key itu sama dengan reminder
		if(preferenceKey.equals(DAILY_REMINDER)){
			if(objectState){
				dailyReminderAlarmReceiver.setDailyReminderAlarm(getActivity()); // Set alarm dari object daily alarm receiver
			} else {
				dailyReminderAlarmReceiver.cancelAlarm(getActivity()); // Cancel alarm dari object daily alarm receiver
			}
		}
		
		if(preferenceKey.equals(TODAY_RELEASE_DATE_MOVIE_REMINDER)) {
			if(objectState){
				releaseTodayReminderAlarmReceiver.setReleaseDateTodayReminderAlarm(getActivity()); // Set alarm dari object release today alarm receiver
			} else {
				releaseTodayReminderAlarmReceiver.cancelReleaseDateTodayAlarm(getActivity()); // Cancel alarm dari object release today alarm receiver
			}
		}
		
		return true; // Return true agar update value state dari Preference
	}
}
