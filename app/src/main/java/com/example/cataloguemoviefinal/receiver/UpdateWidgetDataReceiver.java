package com.example.cataloguemoviefinal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateWidgetDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("Action intent", intent.getAction());

        // todo: do log first
        if (intent.getAction().equals("com.example.cataloguemoviefinal.widget.ACTION_UPDATE_WIDGET_DATA")) { // may produce null
            // Log message
            Log.d("Testing receiver", "I got ur updates");
        }
    }
}
