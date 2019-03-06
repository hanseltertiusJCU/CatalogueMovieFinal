package com.example.cataloguemoviefinal.async;

import android.content.Context;
import android.os.AsyncTask;


import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.MovieItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Class tsb berguna untuk membaca data dari Database, specifically table movies, lalu mendisplay data yg ada di sana
public class LoadFavoriteMoviesAsync extends AsyncTask<Void, Void, ArrayList<MovieItem>> {
	// WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
	// Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
	// dapat mencegah memory leak
	private final WeakReference<FavoriteItemsHelper> weakFavoriteMovieItemsHelper;
	private final WeakReference<Context> weakContext;
	private final WeakReference<LoadFavoriteMoviesCallback> weakCallback;
	
	public LoadFavoriteMoviesAsync(Context context, LoadFavoriteMoviesCallback callback) {
		weakContext = new WeakReference<>(context);
		weakCallback = new WeakReference<>(callback);
	}
	
	public LoadFavoriteMoviesAsync(FavoriteItemsHelper favoriteItemsHelper, LoadFavoriteMoviesCallback callback) {
		weakFavoriteMovieItemsHelper = new WeakReference<>(favoriteItemsHelper);
		weakCallback = new WeakReference<>(callback);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		weakCallback.get().preExecute(); // memanggil method preExecute di interface {@link LoadFavoriteMoviesCallback}
	}
	
	@Override
	protected ArrayList<MovieItem> doInBackground(Void... voids) {
		return weakFavoriteMovieItemsHelper.get().getAllFavoriteMovieItems(); // Memanggil query method dari {@link FavoriteItemsHelper}
	}
	
	@Override
	protected void onPostExecute(ArrayList<MovieItem> movieItems) {
		super.onPostExecute(movieItems);
		weakCallback.get().postExecute(movieItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
	}
}