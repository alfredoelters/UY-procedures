package uy.edu.ucu.android.tramitesuy.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.activities.MainActivity;
import uy.edu.ucu.android.tramitesuy.constants.PreferencesConstants;

public class LoadFinishedBroadcastReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;

    public LoadFinishedBroadcastReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case LoadProceedingsService.NOTIFICATION_FINISHED:
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int resultCode = bundle.getInt(LoadProceedingsService.RESULT);
                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setContentTitle(context.getString(R.string.load_notification_tittle))
                            .setSmallIcon(R.mipmap.ic_launcher);
                    if (resultCode == Activity.RESULT_OK) {
                        mBuilder.setContentText(context.getString(R.string.load_notification_text_success));
                        context.getSharedPreferences(PreferencesConstants.PREF_NAME,
                                Context.MODE_PRIVATE).edit()
                                .putBoolean(PreferencesConstants.PREF_LOADED_PROCEEDINGS, true);
                    } else {
                        mBuilder.setContentText(context.getString(R.string.load_notification_text_failure));
                    }
                    mBuilder.setAutoCancel(true);
                    Intent notificationIntent = new Intent(context, MainActivity.class);

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                            notificationIntent, 0);
                    mBuilder.setContentIntent(pendingIntent);
                    notificationManager.notify(NOTIFICATION_ID,mBuilder.build());
                }
                break;
        }
    }
}
