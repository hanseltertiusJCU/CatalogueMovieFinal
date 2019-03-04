package com.example.cataloguemoviefinal.database;

import android.provider.BaseColumns;

// Kelas ini berguna untuk membangun struktur tabel dari database
class FavoriteDatabaseContract {
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
	
}
