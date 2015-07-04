package uy.edu.ucu.android.tramitesuy.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by alfredo on 03/07/15.
 */
public class ProceedingDetailsFragment extends Fragment{
    private Integer mDummyId = 4;
    private Proceeding mProceeding;


    private final LoaderManager.LoaderCallbacks mProceedingLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    ProceedingsContract.CategoryEntry.CONTENT_URI,
                    null,
                    "_ID = ?",
                    new String[]{String.valueOf(mDummyId)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.moveToNext()) {
                    mProceeding = new Proceeding();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
}
