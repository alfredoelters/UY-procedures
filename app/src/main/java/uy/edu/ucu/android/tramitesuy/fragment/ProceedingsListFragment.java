package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Dependence;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by Mathias on 7/2/15.
 */
public class ProceedingsListFragment extends Fragment {


    private static final String TAG = ProceedingsListFragment.class.getSimpleName();
    private static final String SPINNER_POSITION = "spinner_position";
    private static final String EXTRA_SEARCH_CRITERIA = "search_criteria";
    private static final int CATEGORIES_LOADER = 0;
    private static final int PROCEEDINGS_LOADER = 1;

    private OnFragmentInteractionListener mListener;

    private SearchView mSearchView;
    private Spinner mCategoriesSpinner;
    private ListView mProceedingsListView;
    private CategoryAdapter mCategoryAdapter;
    private ProceedingAdapter mProceedingAdapter;

    private Category mSelectedCategory;
    private int mSpinnerSelectedItemPosition;



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
                List<Category> categories = new ArrayList<>();
                Category category;
                while (data.moveToNext()) {
                    category = new Category();
                    category.setName(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME)));
                    category.setCode(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_CODE)));
                    categories.add(category);
                }
                mCategoryAdapter.setCategoriesData(categories);
                mCategoriesSpinner.setSelection(mSpinnerSelectedItemPosition);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private final LoaderManager.LoaderCallbacks mProceedingsLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader loader;
            String[] selectionArgs;
            String selection;
            if (mSelectedCategory != null) {
                loader = new CursorLoader(getActivity(),
                        ProceedingsContract.CategoryEntry.buildCategoryProceedingsUri(mSelectedCategory.getCode()),
                        null,
                        null,
                        null,
                        null);
            } else {
                String searchCriteria = "%" + args.getString(EXTRA_SEARCH_CRITERIA) + "%";
                selection = ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON + " LIKE ? OR " +
                        ProceedingsContract.ProceedingEntry.COLUMN_TITLE + " LIKE ?";
                selectionArgs = new String[]{searchCriteria, searchCriteria};
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
                List<Proceeding> proceedings = new ArrayList<>();
                Proceeding proceeding;
                while (data.moveToNext()) {
                    proceeding = new Proceeding();
                    proceeding.setId(String.valueOf(data.getLong(data.getColumnIndex(ProceedingsContract.ProceedingEntry._ID))));
                    String title = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_TITLE));
                    proceeding.setTitle(title);
                    String description = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION));
                    proceeding.setDescription(description);
                    Dependence dependence = new Dependence();
                    String dependsOn = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON));
                    dependence.setOrganization(dependsOn);
                    proceeding.setDependence(dependence);
                    proceedings.add(proceeding);
                }
                mProceedingAdapter.setProceedingData(proceedings);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    public static ProceedingsListFragment newInstance() {
        return new ProceedingsListFragment();
    }

    public ProceedingsListFragment() {
    }

    public interface OnFragmentInteractionListener {
        void setTitle(String title);
        void goToProceedingDetailsFragment(long proceedingId);
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
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mCategoriesSpinner = (Spinner) view.findViewById(R.id.categories_spinner);
        mCategoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCategory = (Category) parent.getItemAtPosition(position);
                if (getLoaderManager().getLoader(PROCEEDINGS_LOADER) == null) {
                    getLoaderManager().initLoader(PROCEEDINGS_LOADER, null, mProceedingsLoaderCallbacks);
                } else {
                    getLoaderManager().restartLoader(PROCEEDINGS_LOADER, null, mProceedingsLoaderCallbacks);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedCategory = null;
            }
        });
        mCategoryAdapter = new CategoryAdapter(new ArrayList<Category>());
        mCategoriesSpinner.setAdapter(mCategoryAdapter);
        mProceedingsListView = (ListView) view.findViewById(R.id.proceedings_list_view);
        mProceedingAdapter = new ProceedingAdapter(new ArrayList<Proceeding>());
        mProceedingsListView.setAdapter(mProceedingAdapter);
        mProceedingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.goToProceedingDetailsFragment(
                        Long.parseLong(((Proceeding)parent.getItemAtPosition(position)).getId()));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.setTitle(getString(R.string.app_name));
        if (savedInstanceState != null) {
            mSpinnerSelectedItemPosition = savedInstanceState.getInt(SPINNER_POSITION);
        }
        getLoaderManager().initLoader(CATEGORIES_LOADER, null, mCategoriesLoaderCallbacks);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CATEGORIES_LOADER, null, mCategoriesLoaderCallbacks);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSpinnerSelectedItemPosition = mCategoriesSpinner.getSelectedItemPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getLoaderManager().getLoader(CATEGORIES_LOADER) != null) {
            getLoaderManager().destroyLoader(CATEGORIES_LOADER);
        }
        if (getLoaderManager().getLoader(PROCEEDINGS_LOADER) != null) {
            getLoaderManager().destroyLoader(PROCEEDINGS_LOADER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CATEGORIES_LOADER, null, mCategoriesLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search_proceeding_action).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSelectedCategory = null;
                Bundle args = new Bundle();
                args.putString(EXTRA_SEARCH_CRITERIA, query);
                if (getLoaderManager().getLoader(PROCEEDINGS_LOADER) == null) {
                    getLoaderManager().initLoader(PROCEEDINGS_LOADER, args, mProceedingsLoaderCallbacks);
                } else {
                    getLoaderManager().restartLoader(PROCEEDINGS_LOADER, args, mProceedingsLoaderCallbacks);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSelectedCategory = null;
                Bundle args = new Bundle();
                args.putString(EXTRA_SEARCH_CRITERIA, newText);
                if (getLoaderManager().getLoader(PROCEEDINGS_LOADER) == null) {
                    getLoaderManager().initLoader(PROCEEDINGS_LOADER, args, mProceedingsLoaderCallbacks);
                } else {
                    getLoaderManager().restartLoader(PROCEEDINGS_LOADER, args, mProceedingsLoaderCallbacks);
                }
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCategoriesSpinner != null){
            outState.putInt(SPINNER_POSITION, mCategoriesSpinner.getSelectedItemPosition());
        }
    }

    private class CategoryAdapter extends ArrayAdapter<Category> {

        private List<Category> mCategories;

        public CategoryAdapter(List<Category> categories) {
            super(getActivity(), -1);
            mCategories = categories;
        }

        public void setCategoriesData(List<Category> categories){
            mCategories.clear();
            mCategories.addAll(categories);
            notifyDataSetChanged();
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
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Category getItem(int position) {
            return mCategories.get(position);
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

    private class ProceedingAdapter extends ArrayAdapter<Proceeding> {

        private List<Proceeding> mProceedings;

        public ProceedingAdapter(List<Proceeding> proceedings) {
            super(getActivity(), -1);
            mProceedings = proceedings;
        }

        public void setProceedingData(List<Proceeding> proceedings) {
            mProceedings.clear();
            mProceedings.addAll(proceedings);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mProceedings.size();
        }

        @Override
        public Proceeding getItem(int position) {
            return mProceedings.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.proceedings_list_item, null);
            }
            Proceeding proceeding = getItem(position);
            TextView title = (TextView) convertView.findViewById(R.id.proceeding_title);
            TextView description = (TextView) convertView.findViewById(R.id.proceeding_description);
            TextView responsibleBody = (TextView) convertView.findViewById(R.id.proceeding_responsible_body);
            title.setText(proceeding.getTitle());
            description.setText(proceeding.getDescription());
            responsibleBody.setText(proceeding.getDependence().getOrganization());
            return convertView;
        }
    }


}
