package com.example.cataloguemoviefinal.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.cataloguemoviefinal.BuildConfig;
import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.entity.MovieItem;
import com.squareup.picasso.Picasso;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;

public class FavoriteMovieStackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	
	private final Context context;
	private Cursor cursor;
	private int appWidgetId;
	
	public FavoriteMovieStackRemoteViewsFactory(Context context, Intent intent) {
		this.context = context;
		this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	
	
	@Override
	public void onCreate() {
	
	}
	
	// Method tsb berguna untuk melakukan refresh saat terjadi perubahan data
	@Override
	public void onDataSetChanged() {
		if(cursor != null){
			cursor.close();
		}
		
		final long identityToken = Binder.clearCallingIdentity();
		
		cursor = context.getContentResolver().query(MOVIE_FAVORITE_CONTENT_URI, null, null, null, null);
		
		Binder.restoreCallingIdentity(identityToken);
		
	}
	
	@Override
	public void onDestroy() {
		if(cursor != null){
			cursor.close();
		}
	}
	
	// Method ini berguna untuk return jumlah isi dari data dan jika return 0, maka tampilkan empty view
	@Override
	public int getCount() {
		if(cursor != null){
			return cursor.getCount(); // get item count when cursor is not null
		} else {
			return 0;
		}
		
	}
	
	// Method tsb berguna untuk membuat widget item dan menampilkan semua widget item ke widget
	@Override
	public RemoteViews getViewAt(int position) {
		// cek jika cursor yg di return itu ada data, jika tidak, maka kita akan return views dengan empty text
		if(getCount() > 0){
			// Load image jika ada poster path
			// Gunakan BuildConfig untuk menjaga credential
			String baseImageUrl = BuildConfig.POSTER_IMAGE_ITEM_URL;

			MovieItem movieItem = getSpecificMovieItem(position);

			RemoteViews favoriteMovieItemRemoteViews = new RemoteViews(context.getPackageName(), R.layout.favorite_movie_widget_item);
			favoriteMovieItemRemoteViews.setTextViewText(R.id.movie_title_widget_item, movieItem.getMovieTitle()); // Set value title ke text view
			try {
				Bitmap favoriteMovieItemBitmap = Picasso.get().load(baseImageUrl + movieItem.getMoviePosterPath()).get(); // Get bitmap from Picasso 3rd Party app
				favoriteMovieItemRemoteViews.setImageViewBitmap(R.id.movie_image_widget_item, favoriteMovieItemBitmap); // Set image bitmap based on Picasso result
			} catch(Exception e){
				e.printStackTrace();
			}

			Bundle extras = new Bundle();
			extras.putInt(FavoriteMovieItemWidget.EXTRA_FAVORITE_MOVIE_ITEM, position);
			Intent fillIntent = new Intent();
			fillIntent.putExtras(extras);

			favoriteMovieItemRemoteViews.setOnClickFillInIntent(R.id.movie_widget_item, fillIntent);

			return favoriteMovieItemRemoteViews;
		} else {
			// return remote views object yang menandakan bahwa datanya itu habis
			return new RemoteViews(context.getPackageName(), R.layout.favorite_movie_widget_item);
		}


	}
	
	@Override
	public RemoteViews getLoadingView() {
		return null;
	}
	
	// Method ini berguna utk return brapa layout yg ada
	@Override
	public int getViewTypeCount() {
		return 1;
	}
	
	@Override
	public long getItemId(int position) {
		return cursor.moveToPosition(position) ? cursor.getLong(0) : position;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	private MovieItem getSpecificMovieItem(int position){

		if(cursor.moveToPosition(position)){
			return new MovieItem(cursor);
		} else {
			throw new IllegalStateException("The position is invalid!");
		}

	}
}