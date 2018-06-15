package jesse.jones.opentracker.network;

import jesse.jones.opentracker.network.entity.GetGoogleLocationNameResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 2/26/17.
 */

public interface GoogleCityNameService {
    String URL_GET_LOCATION = "maps/api/geocode/json";
    String FIELD_LAT_LONG = "latlng";

    //http://maps.googleapis.com/maps/api/geocode/json?latlng=45.48160743,-122.55518623
    @GET(URL_GET_LOCATION)
    Call<GetGoogleLocationNameResponse> getCityName(@Query(FIELD_LAT_LONG) String location);

}
