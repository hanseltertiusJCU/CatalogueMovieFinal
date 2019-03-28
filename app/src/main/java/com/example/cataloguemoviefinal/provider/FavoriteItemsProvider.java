package com.example.cataloguemoviefinal.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.observer.FavoriteMovieDataObserver;
import com.example.cataloguemoviefinal.observer.FavoriteTvShowDataObserver;

import java.util.Objects;

import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_CONTENT_URI;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.URI_AUTHORITY;

/**
 * Kelas ini berguna untuk:
 * - mengatur data yang ada di {@link android.database.sqlite.SQLiteDatabase}
 * dengan memanggil {@link FavoriteItemsHelper} di dalam implementation CRUD dari
 * {@link ContentProvider} methods
 */
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

	/**
	 * Method ini di triggered ketika Content Provider di buat dari AndroidManifest.xml.
	 * Method ini berguna untuk membuat instance dari DB dengan memanggil static method
	 * getInstance()
	 * @return boolean yang menandakan bahwa ContentProvider telah dibuat
	 */
	@Override
	public boolean onCreate() {
		favoriteItemsHelper = FavoriteItemsHelper.getInstance(getContext());
		return true;
	}

	/**
	 * Query method berguna untuk mendapatkan data (bisa 1 column atau seluruh data)
	 * bedasarkan URI yang ada di static{} declaration dengan memanggil
	 * {@link android.content.ContentResolver} query method
	 * @param uri URI sebagai input lalu di match ke {@link UriMatcher}
	 * @param projection table column yg di display
	 * @param selection where
	 * @param selectionArgs where clause
	 * @param sortOrder sort by
	 * @return Cursor object (berupa tabel)
	 */
	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
		favoriteItemsHelper.open();
		Cursor cursor;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM:
				cursor = favoriteItemsHelper.queryFavoriteMovieProvider();
				cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri); // Set notification URI agar force load data ketika ada perubahan
				break;
			case FAVORITE_MOVIE_ITEM_ID:
				cursor = favoriteItemsHelper.queryFavoriteMovieProviderById(uri.getLastPathSegment()); // String value parameter merepresentasikan last path dari URI
				cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri); // Set notification URI agar force load data ketika ada perubahan
				break;
			case FAVORITE_TV_SHOW_ITEM:
				cursor = favoriteItemsHelper.queryFavoriteTvShowProvider();
				cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri); // Set notification URI agar force load data ketika ada perubahan
				break;
			case FAVORITE_TV_SHOW_ITEM_ID:
				cursor = favoriteItemsHelper.queryFavoriteTvShowProviderById(uri.getLastPathSegment()); // String value parameter merepresentasikan last path dari URI
				cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri); // Set notification URI agar force load data ketika ada perubahan
				break;
			default:
				cursor = null;
				break;
		}

		return cursor;
	}
	
	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	/**
	 * Insert method berguna untuk memasukkan data dari parameter {@link Uri} dan {@link ContentValues}
	 * melalui {@link android.content.ContentResolver} insert method
	 * @param uri yang menyediakan seluruh data (karena kita tidak tahu id yang ada)
	 * @param values ContentValues dari column untuk
	 * {@link com.example.cataloguemoviefinal.entity.MovieItem} maupun
	 * {@link com.example.cataloguemoviefinal.entity.TvShowItem}
	 * @return URI values dengan id sebagai last part of the URI segment
	 */
	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		favoriteItemsHelper.open();
		// initiate id
		long idFavoriteItemAdded;
		// initiate URI
		Uri favoriteItemUri = null;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM:
				idFavoriteItemAdded = favoriteItemsHelper.insertFavoriteMovieProvider(values);
				// Call handler with getMainLooper method as the activity detail from
				// FavoriteMovieApp and CatalogueMovieFinal are both run on a main thread
				// (myLooper == getMainLooper)
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(getContext().getMainLooper()), getContext())); // Notify change ke {@link FavoriteMovieDataObserver} atau class yg extend ContentObserver
				favoriteItemUri = Uri.parse(MOVIE_FAVORITE_CONTENT_URI + "/" + idFavoriteItemAdded);
				break;
			case FAVORITE_TV_SHOW_ITEM:
				idFavoriteItemAdded = favoriteItemsHelper.insertFavoriteTvShowProvider(values);
				// Call handler with getMainLooper method as the activity detail from
				// FavoriteMovieApp and CatalogueMovieFinal are both run on a main thread
				// (myLooper == getMainLooper)
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(getContext().getMainLooper()), getContext())); // Notify change ke {@link FavoriteTvShowDataObserver} atau class yg extend ContentObserver
				favoriteItemUri = Uri.parse(TV_SHOW_FAVORITE_CONTENT_URI + "/" + idFavoriteItemAdded);
				break;
			default:
				idFavoriteItemAdded = 0;
				break;
		}
		return favoriteItemUri;
	}

	/**
	 * Delete method berguna untuk hapus data berupa {@link Uri} dan where statements
	 * melalui {@link android.content.ContentResolver} delete method
	 * @param uri object {@link Uri} yang mewakili 1 column (URI untuk di delete)
	 * @param selection where clause
	 * @param selectionArgs where statement
	 * @return how many column deleted
	 */
	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
		favoriteItemsHelper.open();
		int rowDeleted;
		switch(sFavoriteItemUriMatcher.match(uri)){
			case FAVORITE_MOVIE_ITEM_ID:
				rowDeleted = favoriteItemsHelper.deleteFavoriteMovieProvider(uri.getLastPathSegment());
				// Call handler with getMainLooper method as the activity detail from FavoriteMovieApp and CatalogueMovieFinal are both run on a main thread (myLooper == getMainLooper)
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(MOVIE_FAVORITE_CONTENT_URI, new FavoriteMovieDataObserver(new Handler(getContext().getMainLooper()), getContext()));
				break;
			case FAVORITE_TV_SHOW_ITEM_ID:
				rowDeleted = favoriteItemsHelper.deleteFavoriteTvShowProvider(uri.getLastPathSegment());
				// Call handler with getMainLooper method as the activity detail from FavoriteMovieApp and CatalogueMovieFinal are both run on a main thread (myLooper == getMainLooper)
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(TV_SHOW_FAVORITE_CONTENT_URI, new FavoriteTvShowDataObserver(new Handler(getContext().getMainLooper()), getContext()));
				break;
			default:
				rowDeleted = 0;
				break;
		}
		return rowDeleted;
	}
	
	// Unused method, hanya ada karena ContentProvider merupakan abstract class dan
	// harus implement semua method di class yang extend ContentProvider
	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
		return 0;
	}
}
