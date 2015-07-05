package uy.edu.ucu.android.tramitesuy.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.provider.ProceedingsContract;

/**
 * Created by alfredo on 05/07/15.
 */
public class ProceedingMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
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
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private android.location.Location mLocation;
    private boolean mRequestingLocationUpdates;
    private Geocoder mGeocoder;

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
        if(mGoogleApiClient == null){
            buildGoogleApiClient();
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        android.location.Location lastKnownLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            LatLng myLocation = new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
        LatLng latLng;
        List<Address> addresses;
        MarkerOptions markerOptions;
        for(Location  location: mLocations){
            try {
                addresses = mGeocoder.getFromLocationName(location.getAddress() + "." +
                        location.getCity() + ","+location.getState(), 1);
                if (addresses!= null && !addresses.isEmpty()){
                    latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
}
