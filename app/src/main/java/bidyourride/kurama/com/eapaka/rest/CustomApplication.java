package bidyourride.kurama.com.eapaka.rest;

import android.app.Application;

import com.android.volley.RequestQueue;

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