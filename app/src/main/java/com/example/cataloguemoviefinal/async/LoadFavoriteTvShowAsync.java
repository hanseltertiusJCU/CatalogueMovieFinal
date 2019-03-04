package com.example.cataloguemoviefinal.async;

import android.os.AsyncTask;


import com.example.cataloguemoviefinal.LoadFavoriteTvShowCallback;
import com.example.cataloguemoviefinal.database.FavoriteItemsHelper;
import com.example.cataloguemoviefinal.entity.TvShowItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Class tsb berguna untuk membaca data dari Database, specifically table tv shows, lalu mendisplay data yg ada di sana
public class LoadFavoriteTvShowAsync extends AsyncTask<Void, Void, ArrayList<TvShowItem>> {
	// WeakReference digunakan karena AsyncTask akan dibuat dan dieksekusi scr bersamaan di method onCreate().
	// Selain itu, ketika Activity destroyed, Activity tsb dapat dikumpulkan oleh GarbageCollector, sehingga
	// dapat mencegah memory leak
	private final WeakReference<FavoriteItemsHelper> weakFavoriteTvShowItemsHelper;
	private final WeakReference<LoadFavoriteTvShowCallback> weakCallback;
	
	public LoadFavoriteTvShowAsync(FavoriteItemsHelper favoriteItemsHelper, LoadFavoriteTvShowCallback callback) {
		weakFavoriteTvShowItemsHelper = new WeakReference<>(favoriteItemsHelper);
		weakCallback = new WeakReference<>(callback);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		weakCallback.get().preExecute(); // memanggil method preExecute di interface {@link LoadFavoriteMoviesCallback}
	}
	
	@Override
	protected ArrayList<TvShowItem> doInBackground(Void... voids) {
		return weakFavoriteTvShowItemsHelper.get().getAllFavoriteTvShowItems(); // Memanggil query method dari {@link FavoriteItemsHelper}
	}
	
	@Override
	protected void onPostExecute(ArrayList<TvShowItem> tvShowItems) {
		super.onPostExecute(tvShowItems);
		weakCallback.get().postExecute(tvShowItems); // memanggil method postExecute di interface {@link LoadFavoriteMoviesCallback}
	}
}
