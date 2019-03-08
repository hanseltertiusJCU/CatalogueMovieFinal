package com.example.cataloguemoviefinal.helper;

import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.MovieItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN;

// Kelas ini berguna untuk convert Cursor ke ArrayList since
// query method utk ContentProvider return Cursor instead of ArrayList
public class FavoriteMovieMappingHelper {
	public static ArrayList<MovieItem> mapCursorToFavoriteMovieArrayList(Cursor movieItemCursor){
		ArrayList<MovieItem> movieItemsList = new ArrayList<>();
		
		while(movieItemCursor.moveToNext()){
			// Get column values
			int movieId = movieItemCursor.getInt(movieItemCursor.getColumnIndexOrThrow(_ID));
			String movieTitle = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_TITLE_COLUMN));
			String movieRatings = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_RATINGS_COLUMN));
			String movieOriginalLanguage = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_ORIGINAL_LANGUAGE_COLUMN));
			String movieReleaseDate = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_RELEASE_DATE_COLUMN));
			String moviePosterPath = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_FILE_PATH_COLUMN));
			String movieDateAddedFavorite = movieItemCursor.getString(movieItemCursor.getColumnIndexOrThrow(MOVIE_DATE_ADDED_FAVORITE_COLUMN));
			int movieBooleanState = movieItemCursor.getInt(movieItemCursor.getColumnIndexOrThrow(MOVIE_FAVORITE_COLUMN));
			// Add MovieItem to arraylist with using constructor that contain variables
			movieItemsList.add(new MovieItem(movieId, movieTitle, movieRatings, movieOriginalLanguage, movieReleaseDate, moviePosterPath, movieDateAddedFavorite, movieBooleanState));
		}
		// Return arraylist that contain MovieItem object
		return movieItemsList;
	}
}
