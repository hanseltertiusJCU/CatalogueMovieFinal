package com.example.cataloguemoviefinal.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.example.cataloguemoviefinal.LoadFavoriteTvShowCallback;
import com.example.cataloguemoviefinal.async.LoadFavoriteTvShowAsync;

public class FavoriteTvShowDataObserver extends ContentObserver {
	private final Context context;
	public FavoriteTvShowDataObserver(Handler handler, Context context){
		super(handler);
		this.context = context;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		new LoadFavoriteTvShowAsync(context, (LoadFavoriteTvShowCallback) context).execute();
	}
}
