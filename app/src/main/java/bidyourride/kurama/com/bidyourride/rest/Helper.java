package bidyourride.kurama.com.bidyourride.rest;

/**
 * Created by madhukurapati on 3/11/18.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String API_KEY = "AIzaSyDHmJxQrAx1J9wZUHWD3kuOmcdy1JYD9sU";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon) {
        return Helper.DIRECTION_API + originLat + "," + originLon + "&destination=" + destinationLat + "," + destinationLon + "&key=" + API_KEY;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
