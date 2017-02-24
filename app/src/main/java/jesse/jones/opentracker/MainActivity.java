package jesse.jones.opentracker;

import android.Manifest;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import jesse.jones.opentracker.network.entity.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener{



    GoogleMap mMap;
    GetGooglePlacesResponse mNewGoog;
    Retrofit mRetrofit;
    GooglePlacesService mPlacesService;

    LatLng mLocation;
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

        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if(mLocation != null){
            mMap.addMarker(new MarkerOptions().position(mLocation).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
        }

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
            mMap.addMarker(new MarkerOptions().position(mLocation).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
    }
}
