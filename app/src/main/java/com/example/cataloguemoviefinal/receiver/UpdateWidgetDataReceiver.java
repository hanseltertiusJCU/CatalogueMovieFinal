package com.example.cataloguemoviefinal.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.widget.FavoriteMovieItemWidget;

/**
 * Kelas ini berguna untuk menerima pesan dari app FavoriteFilmApp dan
 * berguna untuk update widget dari FavoriteFilmApp bahkan dalam kondisi ketika app ini tidak dibuka
 * (applied to API lower than 26)
 */
public class UpdateWidgetDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null) {
            // Cek jika actionnya itu sama dengan action yang ada di Intent di app FavoriteFilmApp
            if (intent.getAction().equals("com.example.cataloguemoviefinal.ACTION_UPDATE_WIDGET_DATA")) {
                // Update widget content
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, FavoriteMovieItemWidget.class));

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favorite_movie_stack_view);
            }

        }
    }
}
