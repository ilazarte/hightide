package com.blm.hightide.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;

public class TickService extends IntentService {

    @SuppressWarnings("unused")
    private static final String TAG = TickService.class.getSimpleName();

    public static Intent newIntent(Context context) {
        return new Intent(context, TickService.class);
    }

    /* cache files 5 minutes */
    private static final int POLL_INTERVAL = 1000 * 60 * 5;

    private StockService stockService = new StockService();

    public static void setServiceAlarm(Context context, boolean on) {

        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (on) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public TickService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!online()) {
            return;
        }

        stockService.cacheTickSources();
    }

    private boolean online() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
