package com.example.cataloguemoviefinal.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.cataloguemoviefinal.R;

/**
 * Implementation of App Widget functionality.
 */
public class FavoriteMovieItemWidget extends AppWidgetProvider{
	
	private static final String TOAST_ACTION = "com.example.cataloguemoviefinal.TOAST_ACTION";
	public static final String EXTRA_FAVORITE_MOVIE_ITEM = "com.example.cataloguemoviefinal.EXTRA_FAVORITE_MOVIE_ITEM";
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
								int appWidgetId) {
		
		// Baris diatas berguna untuk pasang Intent ke FavoriteMovieStackViewService yg berhubungan
		// ke RemoteAdapter
		Intent intent = new Intent(context, FavoriteMovieStackWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_movie_item_widget);
		views.setRemoteAdapter(R.id.favorite_movie_stack_view, intent);
		views.setEmptyView(R.id.favorite_movie_stack_view, R.id.favorite_movie_item_empty_view);
		
		Intent toastIntent = new Intent(context, FavoriteMovieItemWidget.class); // Create intent that goes into self (FavoriteMovieItemWidget)
		toastIntent.setAction(FavoriteMovieItemWidget.TOAST_ACTION); // Set action in toast intent
		toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); // Put app widget id into toast intent
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		// Create PendingIntent
		PendingIntent pendingToastIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(R.id.favorite_movie_stack_view, pendingToastIntent);
		
		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		// There may be multiple widgets active, so update all of them
		for(int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}
	
	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}
	
	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// Cek jika action exists
		if(intent.getAction() != null){
			// Cek jika action dari intent itu sama dengan TOAST_ACTION (bawaan dari Intent yg di plant ke PendingIntent)
			if(intent.getAction().equals(TOAST_ACTION)){
				int viewIndex = intent.getIntExtra(EXTRA_FAVORITE_MOVIE_ITEM, 0);
				// todo: bikin widget buka activity lain dan bs update data gt
				Toast.makeText(context, "Touched view : " + viewIndex, Toast.LENGTH_SHORT).show(); // Make Toast message bedasarkan viewindex (item position)
			}
		}
	}
}
