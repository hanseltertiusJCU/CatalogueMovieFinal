package com.example.cataloguemoviefinal.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.observer.FavoriteMovieDataObserver;
import com.example.cataloguemoviefinal.observer.FavoriteTvShowDataObserver;

import java.util.Objects;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.URI_AUTHORITY;

public class FavoriteItemsProvider extends ContentProvider {
	
	// Int constant untuk semua favorite movie item data dan 1 favorite movie item data ID number
	private static final int FAVORITE_MOVIE_ITEM = 1;
	private static final int FAVORITE_MOVIE_ITEM_ID = 2;
	// Int constant untuk semua favorite tv show item data dan 1 favorite tv show item data ID number
	private static final int FAVORITE_TV_SHOW_ITEM = 3;
	private static final int FAVORITE_TV_SHOW_ITEM_ID = 4;
	// Create URI matcher object untuk match URI bedasarkan ID
	private static final UriMatcher sFavoriteItemUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	// Create FavoriteItemHelper object
	private FavoriteItemsHelper favoriteItemsHelper;
	// Uri global variable for handling values in different cases
	private Uri favoriteItemUri = null;
	
	static {
		// content://com.example.cataloguemoviefinal/favorite_movies
		sFavoriteItemUriMatcher.addURI(URI_AUTHORITY, MOVIE_TABLE_NAME, FAVORITE_MOVIE_ITEM);
		
		// content://com.example.cataloguemoviefinal/favorite_movies/id
		sFavoriteItemUriMatcher.addURI(URI_AUTHORITY, MOVIE_TABLE_NAME + "/#", FAVORITE_MOVIE_ITEM_ID);
		
		// content://com.example.cataloguemoviefinal/favorite_tv_shows
		sFavoriteItemUriMatcher.addURI(URI_AUTHORITY, TV_SHOW_TABLE_NAME, FAVORITE_TV_SHOW_ITEM);
		
		// content://com.example.cataloguemoviefinal/favorite_tv_shows/id
		sFavoriteItemUriMatcher.addURI(URI_AUTHORITY, TV_SHOW_TABLE_NAME + "/#", FAVORITE_TV_SHOW_ITEM_ID);
	}
	
	// Method on create Content Provider class
	@Override
	public boolean onCreate() {
		favoriteItemsHelper = FavoriteItemsHelper.getInstance(getContext());
		return true;
	}
	
	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
		favoriteItemsHelper.open();
		Cursor cursor;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM:
				cursor = favoriteItemsHelper.queryFavoriteMovieProvider();
				break;
			case FAVORITE_MOVIE_ITEM_ID:
				cursor = favoriteItemsHelper.queryFavoriteMovieProviderById(uri.getLastPathSegment()); // String value parameter merepresentasikan last path dari URI
				break;
			case FAVORITE_TV_SHOW_ITEM:
				cursor = favoriteItemsHelper.queryFavoriteTvShowProvider();
				break;
			case FAVORITE_TV_SHOW_ITEM_ID:
				cursor = favoriteItemsHelper.queryFavoriteTvShowProviderById(uri.getLastPathSegment()); // String value parameter merepresentasikan last path dari URI
				break;
			default:
				cursor = null;
				break;
		}
		// Cek jika cursor nya itu tidak null atau exists
		if(cursor != null){
			cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri); // Set notification uri
		}
		return cursor;
	}
	
	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}
	
	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		favoriteItemsHelper.open();
		long idFavoriteItemAdded;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM:
				idFavoriteItemAdded = favoriteItemsHelper.insertFavoriteMovieProvider(values);
				// yang anehnya itu disini, soalnya ga ad handler = ga bs di notify
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(), getContext())); // Notify change ke {@link FavoriteMovieDataObserver} atau class yg extend ContentObserver
				Log.d("data change", "data change notified");
				favoriteItemUri = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + idFavoriteItemAdded);
				break;
			case FAVORITE_TV_SHOW_ITEM:
				idFavoriteItemAdded = favoriteItemsHelper.insertFavoriteTvShowProvider(values);
				// yang anehnya itu disini, soalnya ga ad handler = ga bs di notify
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(), getContext())); // Notify change ke {@link FavoriteTvShowDataObserver} atau class yg extend ContentObserver
				Log.d("data change", "data change notified");
				favoriteItemUri = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + idFavoriteItemAdded);
				break;
			default:
				idFavoriteItemAdded = 0;
				break;
		}
		return favoriteItemUri;
	}
	
	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
		favoriteItemsHelper.open();
		int rowDeleted;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM_ID:
				rowDeleted = favoriteItemsHelper.deleteFavoriteMovieProvider(uri.getLastPathSegment());
				// yang anehnya itu disini, soalnya ga ad handler = ga bs di notify
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(), getContext()));
				Log.d("data change", "data change notified");
				break;
			case FAVORITE_TV_SHOW_ITEM_ID:
				rowDeleted = favoriteItemsHelper.deleteFavoriteTvShowProvider(uri.getLastPathSegment());
				// yang anehnya itu disini, soalnya ga ad handler = ga bs di notify
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(), getContext()));
				Log.d("data change", "data change notified"); // log bwt test
				break;
			default:
				rowDeleted = 0;
				break;
		}
		return rowDeleted;
	}
	
	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
		return 0;
	}
}
