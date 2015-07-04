package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by Mathias on 7/2/15.
 */
public class ProceedingsListFragment extends Fragment {

    private static final int CATEGORIES_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    private SearchView mSearchView;
    private Spinner mSpinner;

    private List<Category> mCategories;
    private final LoaderManager.LoaderCallbacks mCategoriesLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    ProceedingsContract.CategoryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                mCategories = new ArrayList<>();
                Category category;
                while (data.moveToNext()) {
                    category = new Category();
                    category.setName(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME)));
                    category.setCode(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_CODE)));
                    mCategories.add(category);
                }
                mSpinner.setAdapter(new CategoryAdapter(mCategories));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    private Category mSelectedCategory;
    private String mSearchCriteria;
    private final LoaderManager.LoaderCallbacks mProceedingsLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader loader;
            String[] selectionArgs;
            String selection;
            if (mSelectedCategory != null) {
                selection = ProceedingsContract.ProceedingEntry.COLUMN_CAT_KEY + " = ?";
                selectionArgs = new String[]{mSelectedCategory.getCode()};
                loader = new CursorLoader(getActivity(),
                        ProceedingsContract.ProceedingEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
            } else {
                selectionArgs = new String[]{mSearchCriteria, mSearchCriteria};
                selection = ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON + " = ? OR" +
                        ProceedingsContract.ProceedingEntry.COLUMN_TITLE + "= ?";
                loader = new CursorLoader(getActivity(),
                        ProceedingsContract.ProceedingEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
            }
            return loader;
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                mCategories = new ArrayList<>();
                Category category;
                while (data.moveToNext()) {
                    category = new Category();
                    category.setName(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME)));
                    category.setCode(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_CODE)));
                    mCategories.add(category);
                }
                mSpinner.setAdapter(new CategoryAdapter(mCategories));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    public ProceedingsListFragment() {
    }

    public static ProceedingsListFragment newInstance() {
        /*
        Bundle args = new Bundle();
        args.put...(TAG, value);
        Fragment f = new Fragment();
        f.setArguments(args);
        return f;
         */
        return new ProceedingsListFragment();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setTitle(String title);
    }

    public class CategoryAdapter extends ArrayAdapter<Category> {

        public CategoryAdapter(List<Category> categories) {
            super(getActivity(), -1, categories);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.category_item, null);
            }
            TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
            categoryName.setText(getItem(position).getName());
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

}
