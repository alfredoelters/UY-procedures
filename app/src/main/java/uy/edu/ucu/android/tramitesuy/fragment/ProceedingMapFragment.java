package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by alfredo on 05/07/15.
 */
public class ProceedingMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String KEY_PROCEEDING_ID = "proceedingId";
    private static final int LOCATIONS_LOADER = 0;
    private static final int DEFAULT_PADDING = 75;
    private static final int DEFAULT_ZOOM = 16;

    private long mProceedingId;


    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private android.location.Location mLocation;
    private boolean mRequestingLocationUpdates;
    private Geocoder mGeocoder;
    private List<Location> mLocations;
    private GetLocationsAyncTask mLocationsAyncTask;
    private Toast mLoadingLocationsToast;

    private final LoaderManager.LoaderCallbacks mLocationsLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {ProceedingsContract.LocationEntry.COLUMN_CITY,
                    ProceedingsContract.LocationEntry.COLUMN_ADDRESS, ProceedingsContract.LocationEntry.COLUMN_STATE};
            return new CursorLoader(getActivity(),
                    ProceedingsContract.LocationEntry.buildLocationProceeding(mProceedingId),
                    projection,
                    null,
                    null,
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
                    location.setState(data.getString(data
                            .getColumnIndex(ProceedingsContract.LocationEntry.COLUMN_STATE)));
                    mLocations.add(location);
                }
                mLocationsAyncTask = new GetLocationsAyncTask();
                mLocationsAyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mLoadingLocationsToast = Toast.makeText(getActivity(), getString(R.string.loading_locations), Toast.LENGTH_LONG);
                mLoadingLocationsToast.show();
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

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity(), new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                startLocationUpdates();
                if (mGoogleMap == null) {
                    mMapFragment.getMapAsync(ProceedingMapFragment.this);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        }).addApi(LocationServices.API).build();
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (mLocationRequest == null) {
            createLocationRequest();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        mRequestingLocationUpdates = true;
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeocoder = new Geocoder(getActivity(), Locale.ENGLISH);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        } else if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proceeding_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.map, mMapFragment).commit();
        }
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mProceedingId = getArguments().getLong(KEY_PROCEEDING_ID);
        } else if (savedInstanceState != null) {
            mProceedingId = savedInstanceState.getLong(KEY_PROCEEDING_ID);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (getLoaderManager().getLoader(LOCATIONS_LOADER) != null) {
            getLoaderManager().restartLoader(LOCATIONS_LOADER, null, mLocationsLoaderCallbacks);
        } else {
            getLoaderManager().initLoader(LOCATIONS_LOADER, null, mLocationsLoaderCallbacks);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getLoaderManager().getLoader(LOCATIONS_LOADER) != null) {
            getLoaderManager().destroyLoader(LOCATIONS_LOADER);
        }
        if (mLocationsAyncTask != null && mLocationsAyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLocationsAyncTask.cancel(true);
        }
        mLoadingLocationsToast.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_PROCEEDING_ID, mProceedingId);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (mLocation == null) {
            updateCamera();
        } else if (location.getAccuracy() > mLocation.getAccuracy()) {
            mLocation = location;
        }
    }


    private void updateCamera() {
        if (mLocation != null) {
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    private class GetLocationsAyncTask extends AsyncTask<Void, Void, Map<String, LatLng>> {

        @Override
        protected Map<String, LatLng> doInBackground(Void... params) {
            Map<String, LatLng> locations = new HashMap<>();
            try {
                List<Address> addresses;
                for (Location location : mLocations) {
                    addresses = mGeocoder.getFromLocationName(location.getAddress() + "." +
                            location.getCity() + "," + location.getState(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        locations.put(location.getAddress() + ", " + location.getState(),
                                new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                    }
                }
                return locations;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, LatLng> latLngs) {
            super.onPostExecute(latLngs);
            mLoadingLocationsToast.cancel();
            if (latLngs != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng latLng;
                android.location.Location facilityLocation;
                for (String title : latLngs.keySet()) {
                    latLng = latLngs.get(title);
                    String snippet = "";
                    if (mLocation != null) {
                        facilityLocation = new android.location.Location("");
                        facilityLocation.setLatitude(latLng.latitude);
                        facilityLocation.setLongitude(latLng.longitude);
                        snippet = String.format("%.1f", mLocation.distanceTo(facilityLocation) / 1000) + " km.";
                    }
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(snippet));
                    builder.include(latLng);
                }
                boolean moveCamera = false;
                CameraUpdate cameraUpdate = CameraUpdateFactory.scrollBy(0, 0);
                if (mLocation != null) {
                    if (latLngs.keySet().size() > 0) {
                        builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), DEFAULT_PADDING);
                        moveCamera = true;
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_locations), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (latLngs.keySet().size() == 1) {
                        Iterator<String> iterator = latLngs.keySet().iterator();
                        String key = iterator.next();
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngs.get(key), DEFAULT_ZOOM);
                        moveCamera = true;
                    } else if (latLngs.keySet().size() != 0) {
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), DEFAULT_PADDING);
                        moveCamera = true;
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_locations), Toast.LENGTH_SHORT).show();
                    }
                }
                if (moveCamera) {
                    mGoogleMap.moveCamera(cameraUpdate);
                    Log.i(KEY_PROCEEDING_ID, "END " + String.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

}
