package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by Mathias on 7/2/15.
 */
public class HomeFragment extends Fragment {

    private static final int CATEGORIES_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    private SearchView mSearchView;
    private Spinner mSpinner;

    private List<String> mCategories;

    private final LoaderManager.LoaderCallbacks mCategoriesLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {ProceedingsContract.CategoryEntry.COLUMN_NAME};
            return new CursorLoader(getActivity(),
                    ProceedingsContract.CategoryEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                mCategories = new ArrayList<>();
                while (data.moveToNext()) {
                    String category = data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME));
                    mCategories.add(category);
                }
                mSpinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        mCategories));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public static HomeFragment newInstance() {
        /*
        Bundle args = new Bundle();
        args.put...(TAG, value);
        Fragment f = new Fragment();
        f.setArguments(args);
        return f;
         */
        return new HomeFragment();
    }

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSpinner = (Spinner) view.findViewById(R.id.spinner);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.setTitle("TramitesUY");
        /*
        if (getArguments() != null) {
            value = getArguments().get...(TAG);
        }
         */
        getLoaderManager().initLoader(CATEGORIES_LOADER, null, mCategoriesLoaderCallbacks);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setTitle(String title);
    }
}
