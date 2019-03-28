package com.example.cataloguemoviefinal.helper;

import android.database.Cursor;

import com.example.cataloguemoviefinal.entity.TvShowItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN;

/**
 * Kelas ini berguna untuk convert {@link Cursor} ke {@link ArrayList<TvShowItem>} untuk bisa
 * diolah datanya. Hal ini terjadi karena query method untuk
 * {@link android.content.ContentProvider} return {@link Cursor}, bukan {@link ArrayList<TvShowItem>}
 */
public class FavoriteTvShowMappingHelper {
	public static ArrayList<TvShowItem> mapCursorToFavoriteTvShowArrayList(Cursor tvShowItemCursor){
		// Initiate arraylist that contain TvShowItem object
		ArrayList<TvShowItem> tvShowItemsList = new ArrayList<>();

		if(tvShowItemCursor != null){
			while(tvShowItemCursor.moveToNext()){
				// Get column values
				int tvShowId = tvShowItemCursor.getInt(tvShowItemCursor.getColumnIndexOrThrow(_ID));
				String tvShowName = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_NAME_COLUMN));
				String tvShowRatings = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_RATINGS_COLUMN));
				String tvShowOriginalLanguage = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_ORIGINAL_LANGUAGE_COLUMN));
				String tvShowFirstAirDate = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_FIRST_AIR_DATE_COLUMN));
				String tvShowPosterPath = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_FILE_PATH_COLUMN));
				String tvShowDateAddedFavorite = tvShowItemCursor.getString(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_DATE_ADDED_COLUMN));
				int tvShowBooleanState = tvShowItemCursor.getInt(tvShowItemCursor.getColumnIndexOrThrow(TV_SHOW_FAVORITE_COLUMN));
				// Add TvShow item to array list with using constructor that contain variables
				// from column values
				tvShowItemsList.add(new TvShowItem(tvShowId, tvShowName, tvShowRatings, tvShowOriginalLanguage, tvShowFirstAirDate, tvShowPosterPath, tvShowDateAddedFavorite, tvShowBooleanState));
			}
		}

		// Return arraylist that contain TvShowItem object
		return tvShowItemsList;
	}
}
