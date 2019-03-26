package com.example.cataloguemoviefinal.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.cataloguemoviefinal.LoadFavoriteMoviesCallback;
import com.example.cataloguemoviefinal.async.LoadFavoriteMoviesAsync;

public class FavoriteMovieDataObserver extends ContentObserver {
	private final Context context;
	public FavoriteMovieDataObserver(Handler handler, Context context){
		super(handler);
		this.context = context;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		new LoadFavoriteMoviesAsync(context, (LoadFavoriteMoviesCallback) context).execute();
	}
}
