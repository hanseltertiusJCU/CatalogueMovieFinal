package com.example.cataloguemoviefinal.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cataloguemoviefinal.R;
import com.example.cataloguemoviefinal.widget.FavoriteMovieItemWidget;

public class UpdateWidgetDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("Action intent", intent.getAction());

        if(intent.getAction() != null){
            if(intent.getAction().equals("com.example.cataloguemoviefinal.ACTION_UPDATE_WIDGET_DATA")){
                // Log message
                Log.d("Testing receiver", "I got ur updates");

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, FavoriteMovieItemWidget.class));

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favorite_movie_stack_view);
            }

        }
    }
}
