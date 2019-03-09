package com.example.cataloguemoviefinal.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class FavoriteMovieStackWidgetService extends RemoteViewsService {
	
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new FavoriteMovieStackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}
