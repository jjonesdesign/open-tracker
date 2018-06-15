package jesse.jones.opentracker.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.R;
import jesse.jones.opentracker.events.NewActivityEntryEvent;
import jesse.jones.opentracker.events.UpdatedActivityEntryEvent;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import jesse.jones.opentracker.network.entity.Result;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, NavigationView.OnNavigationItemSelectedListener  {

    GoogleMap mMap;
    Retrofit mRetrofit;
    GooglePlacesService mPlacesService;

    LatLng mLocation;
    LatLng mPreviousLocation;
    LatLng mCurrentLocationSelection;
    List<Result> mLocationResultsArray = new ArrayList<Result>();

    LocationManager mLocationManager;

    DatabaseHelper mDatabaseHelper;
    List<ActivityEntry> mActivityLocations;

    Boolean mMapReady = false;

    //Location and Data Services
    boolean mGpsEnabled = false;
    boolean mNetworkEnabled = false;

    public static final int MY_PERMISSION_FINE_LOCATION = 1;

    public static String CORDS_LOCATION = "location";
    public static String CORDS_LATITUDE = "latitude";
    public static String CORDS_LONGITUDE = "longitude";

    public static String GOOGLE_API = "https://maps.googleapis.com/";
    public static String GOOGLE_RADIUS = "10000";
    public static String GOOGLE_KEY = "AIzaSyDvU6snqFqVYlm3DA-06Khmbbst0UzhBkw";

    public MapFragment() {
    }

    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapFragment.this);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRetrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlacesService = mRetrofit.create(GooglePlacesService.class);

        mDatabaseHelper = new DatabaseHelper(getContext());
        mActivityLocations = new ArrayList<ActivityEntry>();
        mActivityLocations.addAll(mDatabaseHelper.getActivityEntries());

        // Ask for permissions to check location
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_FINE_LOCATION);
            }
        } else {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    //==================== Map ======================
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        android.location.Location lastLocation = null;
        try {
            mMap.setMyLocationEnabled(true);
            lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (lastLocation != null) {
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mLocation = lastLatLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMapReady = true;

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.map_custom_info_window, null);

                TextView locationName = (TextView) v.findViewById(R.id.locationNameText);
                locationName.setText(marker.getTitle());

                TextView locationDescription = (TextView) v.findViewById(R.id.locationDescriptionText);
                locationDescription.setText(marker.getSnippet());
                // Returning the view containing InfoWindow contents
                return v;
            }
        });

        rebuildMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();

        return true;
    }

    public void rebuildMap() {
        mMap.clear();

        for (int i = 0; i < mLocationResultsArray.size(); i++) {
            Result newRespone = mLocationResultsArray.get(i);

            MarkerOptions aNewMarker = new MarkerOptions();
            aNewMarker.title(newRespone.getName().toString());
            aNewMarker.position(new LatLng(newRespone.getGeometry().getLocation().getLat(), newRespone.getGeometry().getLocation().getLng()));
            aNewMarker.snippet(newRespone.getName().toString());

            mMap.addMarker(aNewMarker);
        }

        for (int i = 0; i < mActivityLocations.size(); i++) {
            ActivityEntry aActivityEntry = mActivityLocations.get(i);

            MarkerOptions aNewMarker = new MarkerOptions();
            aNewMarker.title(aActivityEntry.getName().toString());
            aNewMarker.position(new LatLng(new Double(aActivityEntry.getLatitude()), new Double(aActivityEntry.getLongitude())));
            aNewMarker.snippet(aActivityEntry.getDescription().toString());

            aNewMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.arrows));

            mMap.addMarker(aNewMarker);
        }

        if (mCurrentLocationSelection != null) {
            MarkerOptions aNewMarker = new MarkerOptions();
            aNewMarker.title(getString(R.string.map_current_selected_location));
            aNewMarker.position(new LatLng(new Double(mCurrentLocationSelection.latitude), new Double(mCurrentLocationSelection.longitude)));

            aNewMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.selectedlocation));

            mMap.addMarker(aNewMarker);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

        Toast.makeText(getActivity(), getString(R.string.toast_location_set_manually), Toast.LENGTH_SHORT).show();

        mMap.clear();

        mCurrentLocationSelection = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocationSelection));

        rebuildMap();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mCurrentLocationSelection = marker.getPosition();
        rebuildMap();
        marker.showInfoWindow();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        isGpsOn();

        if (!mGpsEnabled) {
            if (mLocation != null) {
                //Temporarly removing as it seems to cause user flow problems
                //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            }
            Toast.makeText(getActivity(), getString(R.string.toast_gps_must_be_enabled), Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(getActivity(), getString(R.string.toast_updating_location), Toast.LENGTH_SHORT).show();

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mCurrentLocationSelection = mLocation;
        rebuildMap();

        return false;
    }

    //==================== Search ======================
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!isGpsOn() && mLocation == null) {
            Toast.makeText(getContext(), getString(R.string.toast_gps_not_on), Toast.LENGTH_LONG).show();
            return true;
        }
        if (query.length() <= 0) {
            Toast.makeText(getContext(), getString(R.string.toast_nothing_to_search), Toast.LENGTH_SHORT).show();
            return true;
        }
        //mOptionsMenu.findItem(R.id.action_show_result_list).setVisible(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));


        String currentLocation = mLocation.latitude + "," + mLocation.longitude;
        String searchText = query;

        Call<GetGooglePlacesResponse> foundPlaces = mPlacesService.getPlaces(currentLocation, GOOGLE_RADIUS, "", searchText, GOOGLE_KEY);

        foundPlaces.enqueue(new Callback<GetGooglePlacesResponse>() {
            @Override
            public void onResponse(Call<GetGooglePlacesResponse> call, Response<GetGooglePlacesResponse> response) {

                mLocationResultsArray.addAll(response.body().getResults());
                rebuildMap();
            }

            @Override
            public void onFailure(Call<GetGooglePlacesResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        mLocationResultsArray.clear();
        rebuildMap();
        return false;
    }

    //====================== Data ======================

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (mCurrentLocationSelection == null && mLocation != null) {
            mCurrentLocationSelection = mLocation;
        }
        Toast.makeText(getActivity(), getString(R.string.toast_location_updated), Toast.LENGTH_LONG).show();

        if (mMapReady) {
            mPreviousLocation = mLocation;
            if (mLocationManager != null) {
                mLocationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        isGpsOn();
    }

    public boolean isGpsOn() {
        try {
            mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        return mGpsEnabled;
    }

    public boolean isDataOn() {
        try {
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return mNetworkEnabled;

    }

    // Get called when an event is added up updated.
    //Refresh adapter data
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewActivityEntryEvent event){
        mActivityLocations.clear();
        mActivityLocations.addAll(mDatabaseHelper.getActivityEntries());
        rebuildMap();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdatedActivityEntryEvent event){
        mActivityLocations.clear();
        mActivityLocations.addAll(mDatabaseHelper.getActivityEntries());
        rebuildMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {

                    // permission denied.
                }
                return;
            }

        }
    }



}
