package com.example.cataloguemoviefinal.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;

import java.util.Calendar;

/**
 * Class ini berguna untuk:
 * - Mengatur daily reminder alarm
 * - Mengaktifkan alarm setiap jam 7 pagi dengan interval per hari
 */
public class DailyReminderAlarmReceiver extends BroadcastReceiver {

    // Request code pending intent
    private int REQUEST_CODE = 1;

    /**
     * Method ini di triggered ketika time di device matched dengan
     * time di setDailyReminderAlarm() method, lalu memanggil {@link Notification} ke device
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        showDailyReminderNotification(context); // Call notification method untuk DailyReminder
    }

    /**
     * Method ini di triggered ketika user mengaktifkan daily reminder alarm dari
     * {@link com.example.cataloguemoviefinal.fragment.AlarmPreferenceFragment} dan
     * set alarm setiap jam 7 pagi dan launch MainActivity kembali jika di click
     *
     * @param context activity yang ada di AlarmPreferenceFragment, yaitu
     *                {@link com.example.cataloguemoviefinal.SettingsActivity}
     */
    @SuppressLint("ObsoleteSdkInt")
    public void setDailyReminderAlarm(Context context) {
        // Buat alarm manager object
        AlarmManager dailyReminderAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create intent object untuk memuat pending intent
        Intent dailyReminderIntent = new Intent(context, DailyReminderAlarmReceiver.class);

        // Set time to 7AM
        Calendar dailyReminderCalendarClock = Calendar.getInstance();
        dailyReminderCalendarClock.set(Calendar.HOUR_OF_DAY, 7);
        dailyReminderCalendarClock.set(Calendar.MINUTE, 0);
        dailyReminderCalendarClock.set(Calendar.SECOND, 0);

        // Create pending intent yang berisi intent agar dapat trigger broadcast
        PendingIntent dailyReminderPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, dailyReminderIntent, 0);

        // Cek jika alarm manager exist
        if (dailyReminderAlarmManager != null) {
            // Line ini berguna untuk set alarm sesuai dengan versi device Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Cek jika varsion codes dari device lebih dari/ sama dengan SDK 23
                // Alarm tersebut di triggered pas device sedang dalam kondisi idle
                // dan tetap melakukan nya pada saat batre low
                dailyReminderAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dailyReminderCalendarClock.getTimeInMillis(), dailyReminderPendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // Cek jika version codes dari device lebih dari/ sama dengan SDK 19 namun kurang dari SDK 23
                // Set alarm dengan interval per hari dan alarm tsb walaupun
                // tidak precise namun battery efficient, cara kerjanya adalah
                // pertama kali alarm diaktifkan, langsung d triggered,
                // lalu pas repeat = aktifin setiap 24h
                dailyReminderAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dailyReminderCalendarClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, dailyReminderPendingIntent);
            } else { // Code jika version codes dari device kurang dari SDK 19
                // Set alarm sesuai dengan schedule yang ditentukan
                dailyReminderAlarmManager.set(AlarmManager.RTC_WAKEUP, dailyReminderCalendarClock.getTimeInMillis(), dailyReminderPendingIntent);
            }
        }
    }

    /**
     * Method ini di triggered ketika user menonaktifkan daily reminder alarm dari
     * {@link com.example.cataloguemoviefinal.fragment.AlarmPreferenceFragment} dan
     * batalkan existing alarm manager
     *
     * @param context activity yang ada di AlarmPreferenceFragment, yaitu
     *                {@link com.example.cataloguemoviefinal.SettingsActivity}
     */
    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); // Initiate alarm manager
        Intent intent = new Intent(context, DailyReminderAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0); // Set pending intent yang berisi intent untuk mentrigger broadcast
        pendingIntent.cancel(); // Cancel pending intent

        // Cek jika alarm manager itu exist
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // Cancel alarm manager
        }
    }

    /**
     * Method ini merupakan hasil panggilan dari onReceive dan launch {@link MainActivity}
     * ketika {@link Notification} item di click
     *
     * @param context
     */
    private void showDailyReminderNotification(Context context) {
        // Bikin channel for Daily alarm reminder
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "DailyReminder channel";
        // Bikin id for notif and request code for pending intent
        int NOTIFICATION_ID = 100;
        int REQUEST_CODE_ACTIVITY = 0;

        // Bangun notification manager
        NotificationManager dailyReminderNotificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri dailyReminderAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Buat intent object yg berguna untuk launch main activity lalu object tsb berguna untuk dipasang ke PendingIntent
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Set flags ke Intent object
        // Buat pending intent object yg berguna untuk dipasang ke Notif Builder
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context,
                REQUEST_CODE_ACTIVITY,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Bangun notification builder
        NotificationCompat.Builder dailyReminderNotificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm) // Set small icon yg wajib utk ada
                .setContentTitle(context.getString(R.string.daily_reminder_notif_title)) // Set content title yg wajib untuk ada
                .setContentText(context.getString(R.string.daily_reminder_notif_text)) // Set content text yg wajib untuk ada
                .setContentIntent(mainActivityPendingIntent) // Set pending intent
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setLights(Color.GREEN, 500, 500)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(dailyReminderAlarmSound)
                .setAutoCancel(true) // Hilangkan notification begitu di click
                ;

        // Code ini hanya berguna untuk Android OS Oreo+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Buat notification channel
            NotificationChannel dailyReminderChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            dailyReminderChannel.enableLights(true); // Enable notif lights
            dailyReminderChannel.setLightColor(Color.GREEN); // Set light color into green
            dailyReminderChannel.enableVibration(true); // Enable vibration on notification channel
            dailyReminderChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000}); // Set vibration on notification channel

            dailyReminderNotificationBuilder.setChannelId(CHANNEL_ID); // Set channel id ke Notification Builder

            // Cek jika notification manager itu exist
            if (dailyReminderNotificationManagerCompat != null) {
                dailyReminderNotificationManagerCompat.createNotificationChannel(dailyReminderChannel); // Buat notification channel ke notification manager
            }
        }

        // Buat notification object bedasarkan notification builder build method
        Notification dailyReminderNotification = dailyReminderNotificationBuilder.build();

        // Cek jika notification manager itu exist
        if (dailyReminderNotificationManagerCompat != null) {
            dailyReminderNotificationManagerCompat.notify(NOTIFICATION_ID, dailyReminderNotification); // Berikan notifikasi
        }
    }

}
