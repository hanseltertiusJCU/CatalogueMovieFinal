package com.example.cataloguemoviefinal.database;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

// Kelas ini berguna untuk membangun struktur tabel dari database
class FavoriteDatabaseContract {
	
	// Create variables for URI components
	public static final String URI_AUTHORITY = "com.example.cataloguemoviefinal";
	private static final String URI_SCHEME = "content";
	
	// Class tsb berguna untuk membuat nama tabel serta columnnya dan
	// tidak perlu initiate _ID krn ud otomatis dr sananya (buat table nama "favorite_movies")
	static final class FavoriteMovieItemColumns implements BaseColumns {
		// Nama tabel dari database
		static String MOVIE_TABLE_NAME = "favorite_movies";
		// Nama columns dari database
		static String TITLE_COLUMN = "title";
		static String RATINGS_COLUMN = "ratings";
		static String RELEASE_DATE_COLUMN = "release_date";
		static String ORIGINAL_LANGUAGE_COLUMN = "original_language";
		static String FILE_PATH_COLUMN = "file_path";
		static String DATE_ADDED_COLUMN = "date_added";
		static String FAVORITE_COLUMN = "favorite";
		// Build an URI for Favorite Movie
		public static final Uri MOVIE_FAVORITE_CONTENT_URI = new Uri.Builder().scheme(URI_SCHEME)
			.authority(URI_AUTHORITY)
			.appendPath(MOVIE_TABLE_NAME)
			.build();
	}
	
	// Class tsb berguna utk membuat nama tabel "favorite_tv_shows"
	static final class FavoriteTvShowItemColumns implements BaseColumns {
		// Nama tabel dari database
		static String TV_SHOW_TABLE_NAME = "favorite_tv_shows";
		// Nama columns dari database
		static String NAME_COLUMN = "name";
		static String RATINGS_COLUMN = "ratings";
		static String FIRST_AIR_DATE_COLUMN = "first_air_date";
		static String ORIGINAL_LANGUAGE_COLUMN = "original_language";
		static String FILE_PATH_COLUMN = "file_path";
		static String DATE_ADDED_COLUMN = "date_added";
		static String FAVORITE_COLUMN = "favorite";
	}
	
	// Buat methods untuk variable value untuk Custom Class item yg membawa Cursor sebagai parameter
	public static String getColumnString(Cursor cursor, String columnName){
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	
	public static int getColumnInt(Cursor cursor, String columnName){
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}
	
	public static long getColumnLong(Cursor cursor, String columnName){
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}
	
}
