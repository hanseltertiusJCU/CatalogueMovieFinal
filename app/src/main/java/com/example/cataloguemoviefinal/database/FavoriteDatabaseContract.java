package com.example.cataloguemoviefinal.database;

import android.content.ContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Kelas ini berguna untuk:
 * - Membangun struktur tabel untuk TV Show dan Movie
 * - Mendapatkan value dari column melalui method getColumnString() dan getColumnInt()
 * - Membuat URI sebagai komponen penting untuk {@link ContentProvider} yang representasikan data
 */
public class FavoriteDatabaseContract {
	
	// Create variables for URI components
	public static final String URI_AUTHORITY = "com.example.cataloguemoviefinal";
	private static final String URI_SCHEME = "content";

	/**
	 * Class tsb berguna untuk membuat nama tabel "favorite_movies" serta columnnya dan
	 * tidak perlu initiate _ID krn ud otomatis dr sananya
	 */
	public static final class FavoriteMovieItemColumns implements BaseColumns {
		// Nama tabel dari database
		public static String MOVIE_TABLE_NAME = "favorite_movies";
		// Nama columns dari database
		public static String MOVIE_TITLE_COLUMN = "title";
		public static String MOVIE_RATINGS_COLUMN = "ratings";
		public static String MOVIE_RELEASE_DATE_COLUMN = "release_date";
		public static String MOVIE_ORIGINAL_LANGUAGE_COLUMN = "original_language";
		public static String MOVIE_FILE_PATH_COLUMN = "file_path";
		public static String MOVIE_DATE_ADDED_FAVORITE_COLUMN = "date_added";
		public static String MOVIE_FAVORITE_COLUMN = "favorite";
		// Build an URI for Favorite Movie
		public static final Uri MOVIE_FAVORITE_CONTENT_URI = new Uri.Builder().scheme(URI_SCHEME)
			.authority(URI_AUTHORITY)
			.appendPath(MOVIE_TABLE_NAME)
			.build();
	}

	/**
	 * Class tsb berguna untuk membuat nama tabel "favorite_tv_shows" serta columnnya dan
	 * tidak perlu initiate _ID krn ud otomatis dr sananya
	 */
	public static final class FavoriteTvShowItemColumns implements BaseColumns {
		// Nama tabel dari database
		public static String TV_SHOW_TABLE_NAME = "favorite_tv_shows";
		// Nama columns dari database
		public static String TV_SHOW_NAME_COLUMN = "name";
		public static String TV_SHOW_RATINGS_COLUMN = "ratings";
		public static String TV_SHOW_FIRST_AIR_DATE_COLUMN = "first_air_date";
		public static String TV_SHOW_ORIGINAL_LANGUAGE_COLUMN = "original_language";
		public static String TV_SHOW_FILE_PATH_COLUMN = "file_path";
		public static String TV_SHOW_DATE_ADDED_COLUMN = "date_added";
		public static String TV_SHOW_FAVORITE_COLUMN = "favorite";
		// Build an URI for Favorite TV Show
		public static final Uri TV_SHOW_FAVORITE_CONTENT_URI = new Uri.Builder().scheme(URI_SCHEME)
			.authority(URI_AUTHORITY)
			.appendPath(TV_SHOW_TABLE_NAME)
			.build();
	}

	/**
	 * Method ini berguna untuk mendapatkan value bertipe String
	 * @param cursor {@link Cursor} object
	 * @param columnName nama column dari Table
	 * @return String values berupa variable di
	 * {@link com.example.cataloguemoviefinal.entity.MovieItem} atau
	 * {@link com.example.cataloguemoviefinal.entity.TvShowItem}
	 */
	public static String getColumnString(Cursor cursor, String columnName){
		return cursor.getString(cursor.getColumnIndex(columnName));
	}

	/**
	 * Method ini berguna untuk mendapatkan value bertipe int
	 * @param cursor {@link Cursor} object
	 * @param columnName nama column dari Table
	 * @return int values berupa variable di
	 * {@link com.example.cataloguemoviefinal.entity.MovieItem} atau
	 * {@link com.example.cataloguemoviefinal.entity.TvShowItem}
	 */
	public static int getColumnInt(Cursor cursor, String columnName){
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}
	
}
