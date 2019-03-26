package com.example.cataloguemoviefinal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * Class ini berguna untuk mengimplementasikan definisi dari database (Data Definition Language)
 */
public class FavoriteDatabaseHelper extends SQLiteOpenHelper {

    // Name of database file
    private static String DATABASE_NAME = "favoriteitem";

    // Version of database, guna untuk handle change in schema database
    private static final int DATABASE_VERSION = 1;

    // Constructor from DB Helper
    FavoriteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create table statement untuk Favorite Movie
     */
    private static final String SQL_CREATE_FAVORITE_MOVIE_STATEMENT = String.format("CREATE TABLE %s"
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
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TITLE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RATINGS_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_RELEASE_DATE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_ORIGINAL_LANGUAGE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FILE_PATH_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN,
            FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_FAVORITE_COLUMN
    );

    /**
     * Create table statement untuk Favorite TV Show
     */
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
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_NAME_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_RATINGS_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FIRST_AIR_DATE_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_ORIGINAL_LANGUAGE_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FILE_PATH_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN,
            FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_FAVORITE_COLUMN
    );


    /**
     * Method ini ditriggered ketika class ini dibuat dan berguna untuk create tables melalui
     * {@link SQLiteDatabase} statement
     * @param db SQLiteDatabase object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute create table statements
        db.execSQL(SQL_CREATE_FAVORITE_MOVIE_STATEMENT);
        db.execSQL(SQL_CREATE_FAVORITE_TV_SHOW_STATEMENT);
    }

    /**
     * Method ini berguna untuk update table structure dengan execute {@link SQLiteDatabase}
     * DROP TABLE statement lalu recreate dengan memanggil kembali onCreate() method
     * dan d triggered ketika ada perubahan data table structure
     * @param db SQLiteDatabase object
     * @param oldVersion old version database
     * @param newVersion new version database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table untuk delete table jika ada perubahan dari schema
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME);
        // Create table schema baru jika ada perubahan dari schema
        onCreate(db);
    }
}
