package com.example.cataloguemoviefinal.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.DetailActivity;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.entity.MovieItem;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

/**
 * Implementation of App Widget functionality.
 */

public class FavoriteMovieItemWidget extends AppWidgetProvider{

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
		
		Intent detailActivityIntent = new Intent(context, FavoriteMovieItemWidget.class); // Create intent that goes into self (FavoriteMovieItemWidget)
		detailActivityIntent.setAction(BuildConfig.DETAIL_ACTIVITY_ACTION); // Set action in detail activity intent
		detailActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); // Put app widget id into detail activity intent
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		// Create PendingIntent
		PendingIntent detailActivityPendingIntent = PendingIntent.getBroadcast(context, 0, detailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(R.id.favorite_movie_stack_view, detailActivityPendingIntent);
		
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
			if(intent.getAction().equals(BuildConfig.DETAIL_ACTIVITY_ACTION)){
				// todo: object ga ke pass, mesti dibikin pass karena movieItem = null in this case
				MovieItem selectedFavoriteMovieItem = intent.getExtras().getParcelable(BuildConfig.EXTRA_FAVORITE_MOVIE_ITEM); // Akses parcelable object dari {@link FavoriteMovieStackRemoteViewsFactory} class dengan akses bundle
				// Initiate variable
				int favoriteMovieIdItem = selectedFavoriteMovieItem.getId();
				String favoriteMovieTitleItem = selectedFavoriteMovieItem.getMovieTitle();
				int favoriteMovieBooleanStateItem = selectedFavoriteMovieItem.getFavoriteBooleanState();
				// Tentukan bahwa kita ingin membuka data Movie
				String modeItem = "open_movie_detail";
				// Create URI untuk bawa URI ke data di intent dengan row id value
				// content://com.example.cataloguemoviefinal/favorite_movies/id
				Uri favoriteMovieUriItem = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + favoriteMovieIdItem);
				// Create intent object dengan mengirim ke DetailActivity
				Intent intentWithFavoriteMovieIdData = new Intent(context, DetailActivity.class);
				// Bawa data untuk disampaikan ke {@link DetailActivity}
				intentWithFavoriteMovieIdData.putExtra(BuildConfig.MOVIE_ID_DATA, favoriteMovieIdItem);
				intentWithFavoriteMovieIdData.putExtra(BuildConfig.MOVIE_TITLE_DATA, favoriteMovieTitleItem);
				intentWithFavoriteMovieIdData.putExtra(BuildConfig.MOVIE_BOOLEAN_STATE_DATA, favoriteMovieBooleanStateItem);
				intentWithFavoriteMovieIdData.putExtra(BuildConfig.MODE_INTENT, modeItem);
				// Bawa URI untuk disampaikan ke {@link DetailActivity}
				intentWithFavoriteMovieIdData.setData(favoriteMovieUriItem);
				// Start activity ke {@link DetailActivity}
				context.startActivity(intentWithFavoriteMovieIdData);
			}
		}
	}
}
