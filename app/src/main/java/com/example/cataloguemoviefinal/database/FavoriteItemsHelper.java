package com.example.cataloguemoviefinal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import static android.provider.BaseColumns._ID;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_TABLE_NAME;
import static com.example.cataloguemoviefinal.database.FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_TABLE_NAME;

// Class ini berguna untuk memanipulasi value dari database (Data Manipulation Language)
public class FavoriteItemsHelper {
	private static final String DATABASE_MOVIE_TABLE = MOVIE_TABLE_NAME;
	private static final String DATABASE_TV_SHOW_TABLE = TV_SHOW_TABLE_NAME;
	private static FavoriteDatabaseHelper favoriteDatabaseHelper;
	private static FavoriteItemsHelper INSTANCE;
	
	private static SQLiteDatabase favoriteDatabase;
	
	// Constructor untuk FavoriteItemsHelper
	private FavoriteItemsHelper(Context context) {
		favoriteDatabaseHelper = new FavoriteDatabaseHelper(context);
	}
	
	// Method tsb berguna untuk menginisiasi database
	public static FavoriteItemsHelper getInstance(Context context) {
		if(INSTANCE == null) {
			synchronized(SQLiteOpenHelper.class) {
				if(INSTANCE == null) {
					INSTANCE = new FavoriteItemsHelper(context);
				}
			}
		}
		return INSTANCE;
	}
	
	// Open connection to database
	public void open() throws SQLException {
		favoriteDatabase = favoriteDatabaseHelper.getWritableDatabase();
	}
	
	// Close connection from database
	public void close() {
		favoriteDatabaseHelper.close();
		
		// Cek jika database sedang connected, jika iya maka disconnect
		if(favoriteDatabase.isOpen())
			favoriteDatabase.close();
	}
	
	// Method untuk read seluruh data dari DB dengan menggunakan SQLiteDatabase query method (table movie item)
	public Cursor queryFavoriteMovieProvider(){
		return favoriteDatabase.query(DATABASE_MOVIE_TABLE,
			null,
			null,
			null,
			null,
			null,
			FavoriteDatabaseContract.FavoriteMovieItemColumns.MOVIE_DATE_ADDED_FAVORITE_COLUMN + " DESC");
	}
	
	// Method untuk read satu column data dari DB dengan menggunakan SQLiteDatabase query method (table movie item)
	public Cursor queryFavoriteMovieProviderById(String id){
		return favoriteDatabase.query(DATABASE_MOVIE_TABLE,
			null,
			_ID + " = ?",
			new String[]{id},
			null,
			null,
			null);
	}
	
	// Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method (table movie item)
	public long insertFavoriteMovieProvider(ContentValues values){
		return favoriteDatabase.insert(DATABASE_MOVIE_TABLE, null, values);
	}
	
	// Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method (table movie item)
	public int deleteFavoriteMovieProvider(String id) {
		return favoriteDatabase.delete(DATABASE_MOVIE_TABLE, _ID + " = ?", new String[]{id});
	}
	
	// Method untuk read data dari DB dengan menggunakan SQLiteDatabase query method (table tv show item)
	public Cursor queryFavoriteTvShowProvider(){
		return favoriteDatabase.query(DATABASE_TV_SHOW_TABLE,
			null,
			null,
			null,
			null,
			null,
			FavoriteDatabaseContract.FavoriteTvShowItemColumns.TV_SHOW_DATE_ADDED_COLUMN + " DESC");
	}
	
	// Method untuk read satu column data dari DB dengan menggunakan SQLiteDatabase query method (table movie item)
	public Cursor queryFavoriteTvShowProviderById(String id){
		return favoriteDatabase.query(DATABASE_TV_SHOW_TABLE,
			null,
			_ID + " = ?",
			new String[]{id},
			null,
			null,
			null);
	}
	
	// Method untuk insert data ke DB dengan menggunakan SQLiteDatabase insert method (table tv show item)
	public long insertFavoriteTvShowProvider(ContentValues values){
		return favoriteDatabase.insert(DATABASE_TV_SHOW_TABLE, null, values);
	}
	
	// Method untuk delete data dari DB dengan menggunakan SQLiteDatabase delete method (table tv show item)
	public int deleteFavoriteTvShowProvider(String id){
		return favoriteDatabase.delete(DATABASE_TV_SHOW_TABLE, _ID + " = ?", new String[]{id});
	}
	
}
