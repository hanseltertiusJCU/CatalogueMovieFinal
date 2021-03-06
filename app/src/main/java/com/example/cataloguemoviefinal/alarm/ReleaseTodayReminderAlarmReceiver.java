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
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.async.LoadMoviesDataAsync;
import com.example.cataloguemoviefinal.entity.MovieItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Class ini berguna untuk mengatur release today movie reminder alarm

/**
 * Class ini berguna untuk:
 * - Mengatur release today reminder alarm
 * - Mengaktifkan alarm setiap jam 8 pagi dengan interval per hari dan
 * menampilkan notif hanya ketika date dari system match dengan release date today di AsyncTask
 */
public class ReleaseTodayReminderAlarmReceiver extends BroadcastReceiver {

    // Request code pending intent
    private int RELEASE_TODAY_REQUEST_CODE = 2;

    /**
     * Method ini di triggered ketika time di device matched dengan
     * time di setReleaseTodayReminderAlarm() method, lalu memanggil {@link Notification} ke device
     * dengan ketentuan date dari device harus sama dengan release date hasil dari
     * {@link LoadMoviesDataAsync}
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        try { // Try code to face exception made by AsyncTask get result method

            LoadMoviesDataAsync loadMoviesDataAsync = new LoadMoviesDataAsync(); // Create async task
            AsyncTask<Void, Void, ArrayList<MovieItem>> task = loadMoviesDataAsync.execute(); // Execute async task

            ArrayList<MovieItem> movieItemArrayList = task.get(); // Dapatin result bedasarkan asynctask

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Create new Simple date format object and format the date
            Date date = new Date(); // Create new Date object
            String todayDate = dateFormat.format(date); // Create string based on date object

            // Cek jika array list tidak null dan juga array list itu ada datanya
            if (movieItemArrayList != null) {
                if (movieItemArrayList.size() > 0) {
                    for (MovieItem movieItem : movieItemArrayList) { // Iterate array list
                        if (movieItem.getMovieReleaseDate().equals(todayDate)) { // Compare if release date = today date
                            showReleaseTodayReminderNotification(context, movieItem.getId(), movieItem.getMovieTitle()); // Set notif, notif itu yg membedakan itu adalah notif id, sehingga bs kirim banyak notif jika ada.
                        }
                    }
                }
            }


        } catch (Exception e) { // Catch Exception cus Exception carries all types of exception
            e.printStackTrace();
        }

    }

    /**
     * Method ini di triggered ketika user mengaktifkan release today reminder alarm dari
     * {@link com.example.cataloguemoviefinal.fragment.AlarmPreferenceFragment} dan
     * set release today alarm setiap jam 8 pagi
     *
     * @param context
     */
    @SuppressLint("ObsoleteSdkInt")
    public void setReleaseDateTodayReminderAlarm(Context context) {
        // Buat alarm manager object
        AlarmManager releaseTodayAlarmReminder = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent releaseTodayReminderIntent = new Intent(context, ReleaseTodayReminderAlarmReceiver.class);

        // Set time to 8AM
        Calendar releaseTodayReminderClock = Calendar.getInstance();
        releaseTodayReminderClock.set(Calendar.HOUR_OF_DAY, 8);
        releaseTodayReminderClock.set(Calendar.MINUTE, 0);
        releaseTodayReminderClock.set(Calendar.SECOND, 0);

        // Create pending intent dengan membawa object Intent yg disebut agar dapat trigger broadcast
        PendingIntent releaseTodayPendingIntent = PendingIntent.getBroadcast(context, RELEASE_TODAY_REQUEST_CODE, releaseTodayReminderIntent, 0);

        // Cek jika alarm manager object exist
        if (releaseTodayAlarmReminder != null) {
            // Line ini berguna untuk set alarm sesuai dengan versi device Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                releaseTodayAlarmReminder.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, releaseTodayReminderClock.getTimeInMillis(), releaseTodayPendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                releaseTodayAlarmReminder.setRepeating(AlarmManager.RTC_WAKEUP, releaseTodayReminderClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, releaseTodayPendingIntent);
            } else {
                releaseTodayAlarmReminder.set(AlarmManager.RTC_WAKEUP, releaseTodayReminderClock.getTimeInMillis(), releaseTodayPendingIntent);
            }
        }
    }

    /**
     * Method ini di triggered ketika user menonaktifkan release today reminder alarm dari
     * {@link com.example.cataloguemoviefinal.fragment.AlarmPreferenceFragment} dan
     * batalkan existing alarm manager
     *
     * @param context
     */
    public void cancelReleaseDateTodayAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReleaseTodayReminderAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RELEASE_TODAY_REQUEST_CODE, intent, 0);
        pendingIntent.cancel();

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Method ini merupakan hasil panggilan dari onReceive dan menampilkan movie yang release pada
     * hari yang sama dengan device
     *
     * @param context
     * @param notifId id dari movie item yang ditriggered
     * @param title   title dari movie item yang ditriggered
     */
    private void showReleaseTodayReminderNotification(Context context, int notifId, String title) {
        String CHANNEL_ID = "Channel_2";
        String CHANNEL_NAME = "ReleaseTodayReminder channel";

        // Bangun notification manager
        NotificationManager releaseTodayNotificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri releaseTodayAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Bangun notification builder
        NotificationCompat.Builder releaseTodayReminderNotificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_release_today) // Set small icon yg wajib utk ada
                .setContentTitle(title) // Set content title yg wajib untuk ada
                .setContentText(title + " " + context.getString(R.string.release_today_reminder_notif_text_placeholder)) // Set content text yg wajib untuk ada
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setLights(Color.CYAN, 1000, 1000) // Set light color as well as time taken to turn on light and turn off light
                .setVibrate(new long[]{500, 500, 500, 500, 500}) // Set vibration pattern untuk notification
                .setSound(releaseTodayAlarmSound);

        // Code ini hanya berguna untuk Android OS Oreo+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel releaseTodayNotificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            releaseTodayNotificationChannel.enableLights(true);
            releaseTodayNotificationChannel.setLightColor(Color.CYAN);
            releaseTodayNotificationChannel.enableVibration(true);
            releaseTodayNotificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});

            releaseTodayReminderNotificationBuilder.setChannelId(CHANNEL_ID);

            if (releaseTodayNotificationManagerCompat != null) {
                releaseTodayNotificationManagerCompat.createNotificationChannel(releaseTodayNotificationChannel);
            }
        }

        Notification releaseTodayReminderNotification = releaseTodayReminderNotificationBuilder.build(); // Buat notification

        if (releaseTodayNotificationManagerCompat != null) {
            releaseTodayNotificationManagerCompat.notify(notifId, releaseTodayReminderNotification); // Memberi notification kepada
        }
    }
}
