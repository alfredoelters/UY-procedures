package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Dependence;
import uy.edu.ucu.android.parser.model.Link;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.model.WhenAndWhere;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by alfredo on 03/07/15.
 */
public class ProceedingDetailsFragment extends Fragment {
    private static final int PROCEEDING_LOADER = 0;
    private long mDummyId = 4;
    private Proceeding mProceeding;
    private OnFragmentInteractionListener mListener;

    //UI Components
    private TextView mDescriptionTextView;
    private TextView mDependenceTextView;
    private TextView mRequirementsTextView;
    private TextView mUrlTextView;
    private TextView mStatusTextView;
    private TextView mCategoryTextView;
    private TextView mWhenAndWhereTextView;
    private LinearLayout mWhereAndWhereLinearLayout;


    private final LoaderManager.LoaderCallbacks mProceedingLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    ProceedingsContract.ProceedingEntry.buildProceedingUri(mDummyId),
                    null,
                    ProceedingsContract.ProceedingEntry.TABLE_NAME+"._id = ?",
                    new String[]{String.valueOf(mDummyId)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToNext()) {
                mProceeding.setId(String.valueOf(mDummyId));
                mProceeding.setTitle(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_TITLE)));
                mProceeding.setDescription(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DESCRIPTION)));
                Dependence dependence = new Dependence();
                dependence.setOrganization(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_DEPENDS_ON)));
                mProceeding.setDependence(dependence);
                mProceeding.setRequisites(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_REQUISITES)));
                mProceeding.setUrl(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_URL)));
                mProceeding.setStatus(data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_STATUS)));
                Category category = new Category();
                category.setName(data.getString(data.getColumnIndex(ProceedingsContract.CategoryEntry.COLUMN_NAME)));
                List<Category> categories = new ArrayList<>();
                categories.add(category);
                mProceeding.setCategories(categories);
                String whereAndWhereOtherData = data.getString(data.getColumnIndex(ProceedingsContract.ProceedingEntry.COLUMN_LOCATION_OTHER_DATA));
                if(whereAndWhereOtherData!=null) {
                    WhenAndWhere whenAndWhere = new WhenAndWhere();
                    whenAndWhere.setOtherData(whereAndWhereOtherData);
                    mProceeding.setWhenAndWhere(whenAndWhere);
                    mWhenAndWhereTextView.setText(mProceeding.getWhenAndWhere().getOtherData());
                }else{
                    mWhereAndWhereLinearLayout.setVisibility(View.GONE);
                }
                mListener.setTitle(mProceeding.getTitle());
                mDescriptionTextView.setText(mProceeding.getDescription());
                mDependenceTextView.setText(mProceeding.getDependence().getOrganization());
                mRequirementsTextView.setText(mProceeding.getRequisites());
                SpannableString content = new SpannableString(mProceeding.getUrl());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                mUrlTextView.setText(content);
                mUrlTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mProceeding.getUrl()));
                        startActivity(browserIntent);
                    }
                });
                mStatusTextView.setText(mProceeding.getStatus());
                mCategoryTextView.setText(mProceeding.getCategories().get(0).getName());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public static ProceedingDetailsFragment newInstance() {
        /*
        Bundle args = new Bundle();
        args.put...(TAG, value);
        Fragment f = new Fragment();
        f.setArguments(args);
        return f;
         */
        return new ProceedingDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProceeding = new Proceeding();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proceeding_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDescriptionTextView = (TextView) view.findViewById(R.id.proceeding_description);
        mRequirementsTextView = (TextView) view.findViewById(R.id.proceeding_requirements);
        mDependenceTextView = (TextView) view.findViewById(R.id.proceeding_dependence);
        mWhenAndWhereTextView = (TextView) view.findViewById(R.id.proceeding_when_and_where);
        mUrlTextView = (TextView) view.findViewById(R.id.proceeding_url);
        mStatusTextView= (TextView) view.findViewById(R.id.proceeding_status);
        mCategoryTextView = (TextView) view.findViewById(R.id.proceeding_category);
        mWhereAndWhereLinearLayout = (LinearLayout) view.findViewById(R.id.proceeding_when_and_where_layout);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PROCEEDING_LOADER, null, mProceedingLoaderCallbacks);

    }


    public interface OnFragmentInteractionListener {
        void setTitle(String title);
    }
}
