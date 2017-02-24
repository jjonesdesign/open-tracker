package jesse.jones.opentracker.network;

import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 2/23/17.
 */

public interface GooglePlacesService {
    //maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyDvU6snqFqVYlm3DA-06Khmbbst0UzhBkw
    @GET("maps/api/place/nearbysearch/json")
    Call<GetGooglePlacesResponse> getPlaces(@Query("location") String location, @Query("radius") String radius, @Query("type") String type, @Query("keyword") String keyword, @Query("key") String key);
}
