package com.example.cataloguemoviefinal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

// Class ini berguna untuk mengimplementasikan definisi dari database (Data Definition Language)
public class FavoriteDatabaseHelper extends SQLiteOpenHelper {

    // Name of database file
    private static String DATABASE_NAME = "favoriteitem";

    // Version of database, guna untuk handle change in schema database
    private static final int DATABASE_VERSION = 1;

    // Constructor from DB Helper
    FavoriteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table statement
    private static final String SQL_CREATE_FAVOURITE_MOVIE_STATEMENT = String.format("CREATE TABLE %s"
            + " (%s INTEGER PRIMARY KEY,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s INTEGER NOT NULL DEFAULT 0)",
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME ,
            FavoriteDatabaseContract.FavoriteMovieItemColumns._ID,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.TITLE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.RATINGS_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.RELEASE_DATE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.ORIGINAL_LANGUAGE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.FILE_PATH_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.DATE_ADDED_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.FAVORITE_COLUMN
    );
    
    // Create table statement
    private static final String SQL_CREATE_FAVORITE_TV_SHOW_STATEMENT = String.format("CREATE TABLE %s"
            + " (%s INTEGER PRIMARY KEY,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s INTEGER NOT NULL DEFAULT 0)",
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME ,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns._ID,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.NAME_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.RATINGS_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.FIRST_AIR_DATE_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.ORIGINAL_LANGUAGE_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.FILE_PATH_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.DATE_ADDED_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.FAVORITE_COLUMN
    );

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute create table statements
        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_STATEMENT);
        db.execSQL(SQL_CREATE_FAVORITE_TV_SHOW_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table untuk delete table jika ada perubahan dari schema
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME);
        // Create table schema baru jika ada perubahan dari schema
        onCreate(db);
    }
}
