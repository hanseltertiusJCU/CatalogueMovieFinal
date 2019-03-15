package com.example.cataloguemoviefinal.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.cataloguemoviefinal.MainActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.entity.MovieItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Class ini berguna utk mengatur daily reminder alarm
public class DailyReminderAlarmReceiver extends BroadcastReceiver {
	
	// Request code pending intent
	private int REQUEST_CODE = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		showDailyReminderNotification(context); // Call notification method untuk DailyReminder
		
	}
	
	// Set daily reminder alarm that goes into application Catalogue Movie (trigger every 7AM)
	public void setDailyReminderAlarm(Context context){
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
		if(dailyReminderAlarmManager != null){
			dailyReminderAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, dailyReminderCalendarClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY, dailyReminderPendingIntent); // Set alarm dengan interval per hari
		}
		
		Toast.makeText(context, "Add daily reminder alarm", Toast.LENGTH_SHORT).show(); // Toast message untuk notify bahwa daily reminder alarm ditambahkan
	}
	
	// Method ini berguna untuk cancel alarm yg ada di AlarmManager
	public void cancelAlarm(Context context){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, DailyReminderAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0); // Set pending intent yang berisi intent untuk mentrigger broadcast
		pendingIntent.cancel(); // Cancel pending intent
		
		if(alarmManager != null){
			alarmManager.cancel(pendingIntent);
		}
		
		Toast.makeText(context, "Cancel daily reminder alarm", Toast.LENGTH_SHORT).show(); // Toast message untuk notify bahwa daily reminder alarm cancel
	}
	
	// Method ini berguna untuk notification di Daily Reminder
	private void showDailyReminderNotification(Context context){
		String CHANNEL_ID = "Channel_1";
		String CHANNEL_NAME = "DailyReminder channel";
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
			.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
			.setSound(dailyReminderAlarmSound)
			.setAutoCancel(true) // Hilangkan notification begitu di click
			;
		
		// Code ini hanya berguna untuk Android OS Oreo+
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			// Buat notification channel
			NotificationChannel dailyReminderChannel = new NotificationChannel(CHANNEL_ID,
				CHANNEL_NAME,
				NotificationManager.IMPORTANCE_DEFAULT);
			
			dailyReminderChannel.enableVibration(true);
			dailyReminderChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
			
			dailyReminderNotificationBuilder.setChannelId(CHANNEL_ID); // Set channel id ke Notification Builder
			
			if(dailyReminderNotificationManagerCompat != null){
				dailyReminderNotificationManagerCompat.createNotificationChannel(dailyReminderChannel); // Buat notification channel ke notification manager
			}
		}
		
		// Buat notification object bedasarkan notification builder build method
		Notification dailyReminderNotification = dailyReminderNotificationBuilder.build();
		
		if(dailyReminderNotificationManagerCompat != null){
			
			dailyReminderNotificationManagerCompat.notify(NOTIFICATION_ID, dailyReminderNotification); // Berikan notifikasi
		}
	}
	
}