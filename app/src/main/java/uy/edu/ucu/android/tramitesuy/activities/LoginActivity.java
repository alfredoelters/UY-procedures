package uy.edu.ucu.android.tramitesuy.activities;

import android.support.v4.app.Fragment;

import uy.edu.ucu.android.tramitesuy.fragment.LoginFragment;
import uy.edu.ucu.android.tramitesuy.util.SingleFragmentActivity;

/**
 * Created by alfredo on 05/07/15.
 */
public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
