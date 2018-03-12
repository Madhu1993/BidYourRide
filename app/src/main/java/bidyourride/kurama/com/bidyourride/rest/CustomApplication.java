package bidyourride.kurama.com.bidyourride.rest;

import android.app.Application;

import com.android.volley.RequestQueue;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by madhukurapati on 3/11/18.
 */

public class CustomApplication extends Application {
    private RequestQueue requestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
    }
    public RequestQueue getVolleyRequestQueue(){
        return requestQueue;
    }
}