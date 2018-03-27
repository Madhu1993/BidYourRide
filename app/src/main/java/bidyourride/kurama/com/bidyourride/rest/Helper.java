package bidyourride.kurama.com.bidyourride.rest;

/**
 * Created by madhukurapati on 3/11/18.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private static final String STATIC_IMAGE_API = "https://maps.googleapis.com/maps/api/staticmap?size=600x600";
    private static final String MAP_DIRECTION_WITHIN_GOOGLE_MAP_URI = "https://www.google.com/maps/dir/?api=1&origin=";
    public static final String API_KEY = "AIzaSyDHmJxQrAx1J9wZUHWD3kuOmcdy1JYD9sU";
    public static final String TAG = "HelperClass";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon) {
        return Helper.DIRECTION_API + originLat + "," + originLon + "&destination=" + destinationLat + "," + destinationLon +"&mode=driving" +"&key=" + API_KEY;
    }

    public static String getStaticImageApi(double originLat, double originLong, double destinationLat, double destinationLong, String polyEncode) {
        String s = Helper.STATIC_IMAGE_API + "&path=color:0x000000|weight:5|enc:" + polyEncode + "&maptype=roadmap&weight:3|color:blue|geodesic:true|" + "&markers=color:blue|label:O|" + originLat + "," + originLong + "&&markers=color:red|label:D|" + destinationLat + "," + destinationLong + "|" + "&key=" + API_KEY;
        return s;


    }

    public static String getMapDirectionWithinGoogleMapUri(String originLat, String originLong, String destinationLat, String destinationLong) {
        String s = Helper.MAP_DIRECTION_WITHIN_GOOGLE_MAP_URI + originLat + "," + originLong + "&destination=" + destinationLat + "," + destinationLong +"&mode=driving" ;
        return s;


    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



//https://www.google.com/maps/dir/?api=1&origin=Space+Needle+Seattle+WA&destination=Pike+Place+Market+Seattle+WA&travelmode=bicycling