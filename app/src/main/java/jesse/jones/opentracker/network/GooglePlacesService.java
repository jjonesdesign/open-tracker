package jesse.jones.opentracker.network;

import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by admin on 2/23/17.
 */

public interface GooglePlacesService {
    String URL_NEARBY_PLACE = "maps/api/place/nearbysearch/json";
    String QUERY_LOCATION = "location";
    String QUERY_RADIUS = "radius";
    String QUERY_TYPE = "type";
    String QUERY_KEYWORD = "keyword";
    String QUERY_KEY = "key";

    //maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyDvU6snqFqVYlm3DA-06Khmbbst0UzhBkw
    @GET(URL_NEARBY_PLACE)
    Call<GetGooglePlacesResponse> getPlaces(@Query(QUERY_LOCATION) String location, @Query(QUERY_RADIUS) String radius, @Query(QUERY_TYPE) String type, @Query(QUERY_KEYWORD) String keyword, @Query(QUERY_KEY) String key);
}
