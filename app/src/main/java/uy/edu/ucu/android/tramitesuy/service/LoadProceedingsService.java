package uy.edu.ucu.android.tramitesuy.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.activities.HomeActivity;
import uy.edu.ucu.android.tramitesuy.constants.PreferencesConstants;
import uy.edu.ucu.android.tramitesuy.data.ProceedingsOpenHelper;
import uy.edu.ucu.android.tramitesuy.util.Utils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class LoadProceedingsService extends IntentService {
    public static final String NOTIFICATION_PROGRESS = "uy.edu.ucu.android.tramitesuy.service.receiver.PROGRESS";
    public static final String NOTIFICATION_FINISHED = "uy.edu.ucu.android.tramitesuy.service.receiver.FINISHED";
    public static final String PROGRESS = "PROGRESS";
    public static final String RESULT = "RESULT";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_LOAD = "uy.edu.ucu.android.tramitesuy.service.action.Load";
    private static final int NOTIFICATION_ID = 1;

    public LoadProceedingsService() {
        super("LoadProceedingsService");
    }

    /**
     * Starts this service to perform action Load. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionLoad(Context context) {
        Intent intent = new Intent(context, LoadProceedingsService.class);
        intent.setAction(ACTION_LOAD);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                handleActionLoad();
            }
        }
    }

    /**
     * Handle action Load in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLoad() {
        int result;
        try {
            Utils.loadProceedings(this);
            result = Activity.RESULT_OK;
        } catch (Exception e) {
            e.printStackTrace();
            result = Activity.RESULT_CANCELED;
        }
        Intent intent = new Intent(NOTIFICATION_FINISHED);
        intent.putExtra(RESULT,result);
        sendBroadcast(intent);
    }
}
