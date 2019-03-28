package com.example.cataloguemoviefinal.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Class ini berguna untuk mengatur remote views serta
 * memanggil {@link FavoriteMovieStackRemoteViewsFactory} class yang berguna
 * untuk membuat stack widget item
 */
public class FavoriteMovieStackWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoriteMovieStackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
