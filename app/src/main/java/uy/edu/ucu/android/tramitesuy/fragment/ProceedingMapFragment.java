package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by alfredo on 05/07/15.
 */
public class ProceedingMapFragment extends Fragment {
    private static final String KEY_PROCEEDING_ID = "proceedingId";
    private static final int LOCATIONS_LOADER = 0;
    private Long mProceedingId;
    private List<Location> mLocations;

    private final LoaderManager.LoaderCallbacks mLocationsLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    ProceedingsContract.LocationEntry.CONTENT_URI,
                    null,
                    ProceedingsContract.LocationEntry.COLUMN_PROC_KEY + " = ?",
                    new String[]{String.valueOf(mProceedingId)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                mLocations = new ArrayList<>();
                Location location;
                while (data.moveToNext()) {
                    location = new Location();
                    location.setCity(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_CITY)));
                    location.setAddress(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_ADDRESS)));
                    location.setTime(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_TIME)));
                    location.setState(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_STATE)));
                    location.setComments(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_COMMENTS)));
                    location.setIsUruguay(ProceedingsContract.LocationEntry.COLUMN_IS_URUGUAY);
                    location.setPhone(ProceedingsContract.LocationEntry.COLUMN_PHONE);
                    mLocations.add(location);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public static ProceedingMapFragment newInstance(long proceedingId) {
        Bundle args = new Bundle();
        args.putLong(KEY_PROCEEDING_ID, proceedingId);
        ProceedingMapFragment instance = new ProceedingMapFragment();
        instance.setArguments(args);
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proceeding_map, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mProceedingId = getArguments().getLong(KEY_PROCEEDING_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mProceedingId = savedInstanceState.getLong(KEY_PROCEEDING_ID);
        }
        getLoaderManager().initLoader(LOCATIONS_LOADER, null, mLocationsLoaderCallbacks);
    }
}
