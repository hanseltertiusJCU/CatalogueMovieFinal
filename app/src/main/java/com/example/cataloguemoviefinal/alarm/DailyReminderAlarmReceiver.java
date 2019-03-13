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

import java.util.Calendar;

public class DailyReminderAlarmReceiver extends BroadcastReceiver {
	
	// Constant yg berguna untuk value untuk type
	public static final String TYPE_DAILY_REMINDER = "TypeDailyReminder";
	public static final String TYPE_RELEASE_TODAY_REMINDER = "TypeReleaseTodayReminder";

	// Constant untuk key yg berguna untuk menampung type value
	public static final String EXTRA_TYPE = "type";

	// Notif id
	private final int ID_DAILY_REMINDER = 100;
	private final int ID_RELEASE_TODAY_REMINDER = 101;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		// todo: mungkin pake if condition bwt handle beberapa notification
		// Get type of alarm
		String type = intent.getStringExtra(EXTRA_TYPE);
		
		int notifId = type.equalsIgnoreCase(TYPE_DAILY_REMINDER) ? ID_DAILY_REMINDER : ID_RELEASE_TODAY_REMINDER; // Tentukan notif id bedasarkan tipe bawaan parameter
		
		// Cek jika tipenya itu berada di Type Daily Reminder
		if(type.equals(TYPE_DAILY_REMINDER)){
			showDailyReminderNotification(context, notifId); // Call notification method untuk DailyReminder
		}
		
	}
	
	// Set daily reminder alarm that goes into application Catalogue Movie (trigger every 7AM)
	public void setDailyReminderAlarm(Context context, String type){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, DailyReminderAlarmReceiver.class);
		intent.putExtra(EXTRA_TYPE, type);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 7);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_DAILY_REMINDER, intent, 0);
		
		if(alarmManager != null){
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent); // Set alarm dengan interval per hari
		}
		
		// todo: bikin toast message/snack bar untuk notify
		Toast.makeText(context, "Add daily reminder alarm", Toast.LENGTH_SHORT).show(); // Toast message untuk notify bahwa daily reminder alarm ditambahkan
	}
	
	// Method ini berguna untuk cancel alarm yg ada di AlarmManager
	public void cancelAlarm(Context context, String type){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, DailyReminderAlarmReceiver.class);
		int requestCode = type.equalsIgnoreCase(TYPE_DAILY_REMINDER) ? ID_DAILY_REMINDER : ID_RELEASE_TODAY_REMINDER;
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
		pendingIntent.cancel();
		
		if(alarmManager != null){
			alarmManager.cancel(pendingIntent);
		}
		
		// todo: bikin toast message/snack bar untuk notify
		Toast.makeText(context, "Cancel daily reminder alarm", Toast.LENGTH_SHORT).show(); // Toast message untuk notify bahwa daily reminder alarm cancel
	}
	
	// Method ini berguna untuk notification di Daily Reminder
	private void showDailyReminderNotification(Context context, int notifId){
		String CHANNEL_ID = "Channel_1";
		String CHANNEL_NAME = "DailyReminder channel";
		
		// Bangun notification manager
		NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		// Buat intent object yg berguna untuk launch main activity lalu object tsb berguna untuk dipasang ke PendingIntent
		Intent mainActivityIntent = new Intent(context, MainActivity.class);
		mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Set flags ke Intent object
		// Buat pending intent object yg berguna untuk dipasang ke Notif Builder
		PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context,
			0,
			mainActivityIntent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Bangun notification builder
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_alarm) // Set small icon yg wajib utk ada
			.setContentTitle(context.getString(R.string.daily_reminder_notif_title)) // Set content title yg wajib untuk ada
			.setContentText(context.getString(R.string.daily_reminder_notif_text)) // Set content text yg wajib untuk ada
			.setContentIntent(mainActivityPendingIntent) // Set pending intent
			.setColor(ContextCompat.getColor(context, android.R.color.transparent))
			.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
			.setSound(alarmSound)
			.setAutoCancel(true) // Hilangkan notification begitu di click
			;
		
		// Code ini hanya berguna untuk Android OS Oreo+
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			// Buat notification channel
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
				CHANNEL_NAME,
				NotificationManager.IMPORTANCE_DEFAULT);
			
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
			
			notificationBuilder.setChannelId(CHANNEL_ID); // Set channel id ke Notification Builder
			
			if(notificationManagerCompat != null){
				notificationManagerCompat.createNotificationChannel(channel); // Buat notification channel ke notification manager
			}
		}
		
		// Buat notification object bedasarkan notification builder build method
		Notification notification = notificationBuilder.build();
		
		if(notificationManagerCompat != null){
			notificationManagerCompat.notify(notifId, notification); // Berikan notifikasi
		}
	}
}
