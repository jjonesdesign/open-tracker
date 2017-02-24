package jesse.jones.opentracker;

import android.Manifest;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import jesse.jones.opentracker.network.entity.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,GoogleMap.OnMapClickListener{

    @BindView(R.id.updateLocationButton)
    FloatingActionButton mUpdateLocationButton;


    GoogleMap mMap;
    GetGooglePlacesResponse mNewGoog;
    Retrofit mRetrofit;
    GooglePlacesService mPlacesService;

    LatLng mLocation;
    LatLng mPreviousLocation;
    ArrayList<LatLng> mLocationArray;
    LocationManager mLocationManager;

    Boolean mMapReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mPlacesService = mRetrofit.create(GooglePlacesService.class);

        Call<GetGooglePlacesResponse> foundPlaces = mPlacesService.getPlaces("-33.8670522,151.1957362","500","","food","AIzaSyDvU6snqFqVYlm3DA-06Khmbbst0UzhBkw");

        foundPlaces.enqueue(new Callback<GetGooglePlacesResponse>() {
            @Override
            public void onResponse(Call<GetGooglePlacesResponse> call, Response<GetGooglePlacesResponse> response) {
                Toast.makeText(MainActivity.this, "FOUND: " + response.body().getResults().size(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<GetGooglePlacesResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == 0){
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMapReady = true;



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
        mLocation = new LatLng(location.getLatitude(),location.getLongitude());
        if(mMapReady){

            Toast.makeText(MainActivity.this, "Location Updated", Toast.LENGTH_LONG).show();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(mLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));


            mPreviousLocation = mLocation;
            if(mLocationManager != null) {
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == 0){
                    mLocationManager.removeUpdates(this);
                }
            }

        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
    }

    /** calculates the distance between two locations in MILES */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }



    //Click Handlers
    @OnClick(R.id.updateLocationButton)
    public void updateLocationButtonClicked(FloatingActionButton button) {
        Toast.makeText(MainActivity.this, "Updating Location", Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == 0){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(MainActivity.this, "Location Set Manually", Toast.LENGTH_SHORT).show();
        mLocation = latLng;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
    }
}
