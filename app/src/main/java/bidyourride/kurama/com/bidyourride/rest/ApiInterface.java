package bidyourride.kurama.com.bidyourride.rest;

/**
 * Created by kurapma on 1/19/17.
 */

import bidyourride.kurama.com.bidyourride.model.LocationResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {

    @POST("geolocate")
    Call<LocationResponse> getLocation(@Query("key") String key);

}
