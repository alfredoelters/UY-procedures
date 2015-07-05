package uy.edu.ucu.android.tramitesuy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by alfredo on 05/07/15.
 */
public class ProceedingMapFragment extends Fragment{
    private static final String KEY_PROCEEDING_ID = "proceedingId";

    public static ProceedingMapFragment newInstance(long proceedingId) {
        Bundle args = new Bundle();
        args.putLong(KEY_PROCEEDING_ID, proceedingId);
        ProceedingMapFragment instance = new ProceedingMapFragment();
        instance.setArguments(args);
        return instance;
    }
}
