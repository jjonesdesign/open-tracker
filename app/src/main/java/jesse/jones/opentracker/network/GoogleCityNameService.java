package jesse.jones.opentracker.network;

import com.google.android.gms.maps.model.LatLng;

import jesse.jones.opentracker.network.entity.GetGoogleLocationNameResponse;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 2/26/17.
 */

public interface GoogleCityNameService {

    //http://maps.googleapis.com/maps/api/geocode/json?latlng=45.48160743,-122.55518623
    @GET("maps/api/geocode/json")
    Call<GetGoogleLocationNameResponse> getCityName(@Query("latlng") String location);

}
