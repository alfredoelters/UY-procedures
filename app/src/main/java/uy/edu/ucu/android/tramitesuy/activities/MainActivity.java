package uy.edu.ucu.android.tramitesuy.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.fragment.ProceedingDetailsFragment;
import uy.edu.ucu.android.tramitesuy.fragment.ProceedingTabsFragment;
import uy.edu.ucu.android.tramitesuy.fragment.ProceedingsListFragment;
import uy.edu.ucu.android.tramitesuy.receivers.LoadFinishedBroadcastReceiver;



/**
 * Created by alfredo on 30/06/15.
 */
public class MainActivity extends AppCompatActivity implements ProceedingsListFragment.OnFragmentInteractionListener, ProceedingDetailsFragment.OnFragmentInteractionListener{
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProfilePictureView mProfilePictureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        setupDrawer();
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ProceedingsListFragment.newInstance())
                    .commit();
        }
       NotificationManagerCompat.from(this).cancel(LoadFinishedBroadcastReceiver.NOTIFICATION_ID);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                boolean result;
                switch (menuItem.getItemId()) {
                    case R.id.action_logout:
                        LoginManager.getInstance().logOut();
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        MainActivity.this.finish();
                        result = true;
                        break;
                    default:
                        result = false;
                }
                mDrawerLayout.closeDrawers();
                return result;
            }
        });
        // drawer toggle to allow action bar open/close
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.open_drawer_description,  /* "open drawer" description */
                R.string.close_drawer_description  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                syncState();
                invalidateOptionsMenu();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                syncState();
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        Profile currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            mProfilePictureView = (ProfilePictureView) mNavigationView
                    .findViewById(R.id.facebook_profile_picture);
            mProfilePictureView.setProfileId(currentProfile.getId());
            mProfilePictureView.setPresetSize(ProfilePictureView.LARGE);
            mProfilePictureView.setCropped(true);
            TextView userNameTextView = (TextView) mNavigationView.findViewById(R.id.user_name);
            userNameTextView.setText(currentProfile.getName());
        }else{
            mNavigationView.getMenu().getItem(0).setVisible(false);
            mNavigationView.findViewById(R.id.facebook_profile_picture).setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(String title) {
        TextView toolbarTextView = (TextView) findViewById(R.id.toolbar_title);
        toolbarTextView.setText(title);
    }

    @Override
    public void goToProceedingDetailsFragment(long proceedingId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ProceedingTabsFragment.newInstance(proceedingId))
                .addToBackStack(null).commit();
    }


}
