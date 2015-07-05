package uy.edu.ucu.android.tramitesuy.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uy.edu.ucu.android.tramitesuy.R;

/**
 * Created by alfredo on 04/07/15.
 */
public class ProceedingTabsFragment extends Fragment {
    private static final String KEY_PROCEEDING_ID = "proceedingId";
    private Long mProceedingId;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;


    public static ProceedingTabsFragment newInstance(long proceedingId) {
        Bundle args = new Bundle();
        args.putLong(KEY_PROCEEDING_ID, proceedingId);
        ProceedingTabsFragment instance = new ProceedingTabsFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProceedingId = getArguments().getLong(KEY_PROCEEDING_ID);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mProceedingId = savedInstanceState.getLong(KEY_PROCEEDING_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proceeding_tabs, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mPagerAdapter = new ProceedingDetailsPagerAdapter(getActivity());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.details)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.map)));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_PROCEEDING_ID, mProceedingId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class ProceedingDetailsPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private Context context;

        public ProceedingDetailsPagerAdapter(Context context) {
            super(getActivity().getSupportFragmentManager());
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment result;
            switch (position) {
                case 0:
                    result = ProceedingDetailsFragment.newInstance(mProceedingId);
                    break;
                case 1:
                    result = ProceedingMapFragment.newInstance(mProceedingId);
                    break;
                default:
                    result = null;
            }
            return result;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence result;
            switch (position) {
                case 0:
                    result = getString(R.string.details);
                    break;
                case 1:
                    result = getString(R.string.map);
                    break;
                default:
                    result = null;
            }
            return result;
        }
    }


}
