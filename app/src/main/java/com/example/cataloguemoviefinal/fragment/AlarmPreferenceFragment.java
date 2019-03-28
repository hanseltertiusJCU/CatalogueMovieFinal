package com.example.cataloguemoviefinal.fragment;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.alarm.DailyReminderAlarmReceiver;
import com.example.cataloguemoviefinal.alarm.ReleaseTodayReminderAlarmReceiver;

/**
 * Class ini berguna untuk:
 * - Mengatur daily alarm reminder apakah itu aktif atau tidak
 * - Mengatur release date today movie alarm reminder alarm apakah itu aktif atau tidak
 */
public class AlarmPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    // Key
    private String DAILY_REMINDER;
    private String TODAY_RELEASE_DATE_MOVIE_REMINDER;
    // Toast message variable
    private Toast toastMessage;

    /**
     * Method ini di trigger ketika AlarmPreferenceFragment dibuat
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set values from key in SwitchPreference items
        DAILY_REMINDER = getResources().getString(R.string.daily_reminder_key);
        TODAY_RELEASE_DATE_MOVIE_REMINDER = getResources().getString(R.string.release_movie_reminder_key);

        // Find SwitchPreference object based on key, which is useful for displaying Preference in
        // preference screen that leads to storing boolean into SharedPreferences
        SwitchPreference dailyReminderPreference = (SwitchPreference) findPreference(DAILY_REMINDER);
        dailyReminderPreference.setOnPreferenceChangeListener(this); // Set listener untuk menghandle event change buat SwitchPreference

        SwitchPreference movieReleaseTodayReminderPreference = (SwitchPreference) findPreference(TODAY_RELEASE_DATE_MOVIE_REMINDER);
        movieReleaseTodayReminderPreference.setOnPreferenceChangeListener(this);

    }


    /**
     * Method tsb berguna untuk membuat xml yang berisi PreferenceScreen ke Preference
     *
     * @param bundle
     * @param s
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.setting_reminder_preferences); // Add xml ke class AlarmPreferenceFragment
    }

    /**
     * Method tsb berguna ketika value dari Preference itu diganti, secara bersamaan
     * method tsb berguna untuk:
     * - Set daily alarm
     * - Set release date today alarm
     * - Cancel daily alarm
     * - Cancel release date today alarm
     *
     * @param preference
     * @param object
     * @return boolean untuk update state value dari preference object
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object object) {

        // Dapatin key value dari preference object
        String preferenceKey = preference.getKey();

        // Dapatin state dari object dengan cast Object ke boolean
        boolean objectState = (boolean) object;

        // Buat daily alarm receiver object
        DailyReminderAlarmReceiver dailyReminderAlarmReceiver = new DailyReminderAlarmReceiver();

        // Buat release today alarm receiver object
        ReleaseTodayReminderAlarmReceiver releaseTodayReminderAlarmReceiver = new ReleaseTodayReminderAlarmReceiver();

        // Cek jika preference key itu sama dengan reminder
        if (preferenceKey.equals(DAILY_REMINDER)) {
            if (objectState) {
                dailyReminderAlarmReceiver.setDailyReminderAlarm(getActivity()); // Set alarm dari object daily alarm receiver
                // Check if there is existing toast message
                if (toastMessage != null) {
                    toastMessage.cancel(); // Cancel existing toast message
                }
                toastMessage = Toast.makeText(getContext(), getString(R.string.add_daily_reminder), Toast.LENGTH_SHORT); // Toast message untuk notify bahwa daily reminder alarm ditambahkan
                toastMessage.show(); // Show toast message
            } else {
                dailyReminderAlarmReceiver.cancelAlarm(getActivity()); // Cancel alarm dari object daily alarm receiver
                // Check if there is existing toast message
                if (toastMessage != null) {
                    toastMessage.cancel(); // Cancel existing toast message
                }
                toastMessage = Toast.makeText(getContext(), getString(R.string.cancel_daily_reminder), Toast.LENGTH_SHORT); // Toast message untuk notify bahwa daily reminder alarm cancel
                toastMessage.show(); // Show toast message
            }
        }

        if (preferenceKey.equals(TODAY_RELEASE_DATE_MOVIE_REMINDER)) {
            if (objectState) {
                releaseTodayReminderAlarmReceiver.setReleaseDateTodayReminderAlarm(getActivity()); // Set alarm dari object release today alarm receiver
                // Check if there is existing toast message
                if (toastMessage != null) {
                    toastMessage.cancel(); // Cancel existing toast message
                }
                toastMessage = Toast.makeText(getContext(), getString(R.string.add_release_today_reminder), Toast.LENGTH_SHORT);
                toastMessage.show(); // Show toast message
            } else {
                releaseTodayReminderAlarmReceiver.cancelReleaseDateTodayAlarm(getActivity()); // Cancel alarm dari object release today alarm receiver
                // Check if there is existing toast message
                if (toastMessage != null) {
                    toastMessage.cancel(); // Cancel existing toast message
                }
                toastMessage = Toast.makeText(getContext(), getString(R.string.cancel_release_today_reminder), Toast.LENGTH_SHORT);
                toastMessage.show(); // Show toast message
            }
        }

        return true; // Return true agar update value state dari Preference
    }
}
