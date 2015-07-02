package uy.edu.ucu.android.tramitesuy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.constants.PreferencesConstants;
import uy.edu.ucu.android.tramitesuy.service.LoadProceedingsService;

/**
 * Created by alfredo on 30/06/15.
 */
public class SplashScreenActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_splash_screen);
        if(getSharedPreferences(PreferencesConstants.PREF_NAME, MODE_PRIVATE).getBoolean(PreferencesConstants.PREF_LOADED_PROCEEDINGS,false)){
            startHomeActivity();
        }else{
            LoadProceedingsService.startActionLoad(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        switch (intent.getAction()){
            case LoadProceedingsService.NOTIFICATION_FINISHED:
                startHomeActivity();
                break;
        }
    }

    /**
     * Method to start the homeActivity and finish the splashScreenActivity
     */
    private void startHomeActivity() {
//        mConnectionTimeoutTimer.cancel();
        // Start the next activity
        Intent mainIntent = new Intent().setClass(
                this, MainActivity.class);
        startActivity(mainIntent);
        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        this.finish();
    }
}
