package jesse.jones.opentracker;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.interfaces.NewActivityAdded;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import jesse.jones.opentracker.network.entity.Location;
import jesse.jones.opentracker.network.entity.Result;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener,GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMarkerClickListener, NewActivityAdded, SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    @BindView(R.id.mainViewFrameLayout)
    FrameLayout mContentViewArea;

    @BindView(R.id.addActivityButton)
    FloatingActionButton mAddActivityButton;



    GoogleMap mMap;
    GetGooglePlacesResponse mNewGoog;
    Retrofit mRetrofit;
    GooglePlacesService mPlacesService;

    LatLng mLocation;
    LatLng mPreviousLocation;
    List<Result> mLocationResultsArray;
    LocationManager mLocationManager;

    DatabaseHelper mDatabaseHelper;
    List<ActivityEntry> mActivityLocations;

    Boolean mMapReady = false;

    //Location and Data Services
    boolean mGpsEnabled = false;
    boolean mNetworkEnabled = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLocationResultsArray = new ArrayList<Result>();


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlacesService = mRetrofit.create(GooglePlacesService.class);

        mDatabaseHelper = new DatabaseHelper(getBaseContext());
        mActivityLocations = new ArrayList<ActivityEntry>();
        mActivityLocations.addAll(mDatabaseHelper.getActivityEntries());


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        return true;
    }

    //====================== Map & Data Providers ======================
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            mMap.setMyLocationEnabled(true);
        }

        android.location.Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastLocation != null) {
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mLocation = lastLatLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMapReady = true;

        rebuildMap();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        return true;
    }

    public void rebuildMap(){
        mMap.clear();

        for(int i=0; i < mLocationResultsArray.size(); i++){
            Result newRespone = mLocationResultsArray.get(i);

            MarkerOptions aNewMarker = new MarkerOptions();
            aNewMarker.title(newRespone.getName().toString());
            aNewMarker.position(new LatLng(newRespone.getGeometry().getLocation().getLat(),newRespone.getGeometry().getLocation().getLng()));
            aNewMarker.snippet(newRespone.getName().toString());

            mMap.addMarker(aNewMarker);
        }

        for(int i=0; i < mActivityLocations.size(); i++){
            ActivityEntry aActivityEntry = mActivityLocations.get(i);

            MarkerOptions aNewMarker = new MarkerOptions();
            aNewMarker.title(aActivityEntry.getName().toString());
            aNewMarker.position(new LatLng(new Double(aActivityEntry.getLatitude()),new Double(aActivityEntry.getLongitude())));
            aNewMarker.snippet(aActivityEntry.getDescription().toString());


            aNewMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.arrows));

            mMap.addMarker(aNewMarker);
        }

    }


    @Override
    public void onProviderDisabled(String provider) {
        //Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Log.d("Latitude","enable");
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        //txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(MainActivity.this, "Location Updated", Toast.LENGTH_LONG).show();

        if (mMapReady) {


            mPreviousLocation = mLocation;
            if (mLocationManager != null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
                    mLocationManager.removeUpdates(this);
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
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

    //====================== Click Handlers ======================
    @OnClick(R.id.addActivityButton)
    public void addActivityButtonClicked(FloatingActionButton button) {
        if (mLocation == null) {
            Toast.makeText(MainActivity.this, "No Location Set, Add One Manually", Toast.LENGTH_SHORT).show();
            return;
        }

        AddActivityFragment addActivityFragment = new AddActivityFragment();
        Bundle bundle = new Bundle();
        String locationString = mLocation.latitude + "," + mLocation.longitude;
        bundle.putString("location",locationString);
        bundle.putString("latitude",String.valueOf(mLocation.latitude));
        bundle.putString("longitude",String.valueOf(mLocation.longitude));
        addActivityFragment.setArguments(bundle);
        addActivityFragment.show(getSupportFragmentManager(), addActivityFragment.getClass().getSimpleName());
    }

    @Override
    public void onMapClick(LatLng latLng) {

        Toast.makeText(MainActivity.this, "Location Set Manually", Toast.LENGTH_SHORT).show();
        mLocation = latLng;
        mMap.clear();

        rebuildMap();

        mMap.addMarker(new MarkerOptions().position(mLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));


    }
    @Override
    public boolean onMyLocationButtonClick() {
        isGpsOn();

        if (!mGpsEnabled) {
            if(mLocation != null){
                //Temporarly removing as it seems to cause user flow problems
                //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            }
            Toast.makeText(MainActivity.this, "GPS Must Be Enabled To Update Location.", Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(MainActivity.this, "Updating Location", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_goto_activities:
                ListActivitiesFragment listActivitiesFragment = new ListActivitiesFragment();
                Bundle bundle = new Bundle();
                listActivitiesFragment.setArguments(bundle);
                listActivitiesFragment.show(getSupportFragmentManager(), listActivitiesFragment.getClass().getSimpleName());
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }



    //====================== Fragments ======================
    public void replaceFragment(Fragment fragment, String descriptor) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(mContentViewArea.getId(), fragment, descriptor);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void addFragment(Fragment fragment, String descriptor) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(mContentViewArea.getId(), fragment, descriptor);
        ft.addToBackStack(null);
        ft.commit();
    }





    @Override
    public void notifyNewActivityAdded() {
        mActivityLocations.clear();
        mActivityLocations.addAll(mDatabaseHelper.getActivityEntries());
        rebuildMap();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!isGpsOn() && mLocation == null){
            Toast.makeText(getBaseContext(), "No Location Set. Turn on GPS or click on the map.", Toast.LENGTH_LONG).show();
            return true;
        }
        if(query.length() <= 0){
            Toast.makeText(getBaseContext(), "No search input has been added yet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));


        String currentLocation = mLocation.latitude + "," + mLocation.longitude;
        String searchText = query;

        Call<GetGooglePlacesResponse> foundPlaces = mPlacesService.getPlaces(currentLocation, "10000", "", searchText, "AIzaSyDvU6snqFqVYlm3DA-06Khmbbst0UzhBkw");

        foundPlaces.enqueue(new Callback<GetGooglePlacesResponse>() {
            @Override
            public void onResponse(Call<GetGooglePlacesResponse> call, Response<GetGooglePlacesResponse> response) {
                Toast.makeText(MainActivity.this, "FOUND: " + response.body().getResults().size(), Toast.LENGTH_SHORT).show();

                mLocationResultsArray.addAll(response.body().getResults());
                rebuildMap();
            }

            @Override
            public void onFailure(Call<GetGooglePlacesResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
}
