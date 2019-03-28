package com.example.cataloguemoviefinal.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;

/**
 * Kelas tsb berguna sebagai observer dari {@link android.content.ContentResolver} dan
 * berguna untuk menghandle perubahan data dari {@link android.content.ContentResolver}
 * yang menampung Favorite {@link com.example.cataloguemoviefinal.entity.MovieItem}
 */
public class FavoriteMovieDataObserver extends ContentObserver {
	private final Context context;

	/**
	 * Constructor untuk membuat object {@link FavoriteMovieDataObserver} bedasarkan
	 * {@link Handler} dari {@link android.os.HandlerThread} getLooper() method
	 * dan {@link Context} dari activity
	 * panggilan
	 * @param handler
	 * @param context
	 */
	public FavoriteMovieDataObserver(Handler handler, Context context){
		super(handler);
		this.context = context;
	}

	/**
	 * Method ini triggered ketika ada perubahan data di {@link android.content.ContentResolver}
	 * melalui notifyChange() method dan bertugas untuk load
	 * {@link com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync} kembali
	 * @param selfChange
	 */
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		new LoadFavoriteMoviesAsync(context, (LoadFavoriteMoviesCallback) context).execute();
	}
}
