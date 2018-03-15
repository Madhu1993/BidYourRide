package bidyourride.kurama.com.bidyourride.helper;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by madhukurapati on 3/13/18.
 */

public class GoogleMapHelper {

    private static final String TAG = "GoogleMapHelper";

    public static void addMarkersOfOriginDestination(GoogleMap map, Double originLat, Double originLong, Double destinationLat, Double destinationLong) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(originLat, originLong))
                .title("Origin"));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(destinationLat, destinationLong))
                .title("Destination"));
    }

    public static void drawRouteOnMap(Polyline polyline,GoogleMap map, List<LatLng> positions) {
        if (!getCameraPositionAndDrawMap(polyline, map)) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                    .zoom(8)
                    .build();
            if (!map.getUiSettings().isZoomControlsEnabled()) {
                map.getUiSettings().setZoomControlsEnabled(true);
            }
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public static boolean getCameraPositionAndDrawMap(final Polyline polyline, GoogleMap map) {
        boolean isCameraPositionSet = false;
        boolean hasPoints = false;
        Double maxLat = null, minLat = null, minLon = null, maxLon = null;
        if (polyline != null && polyline.getPoints() != null) {
            List<LatLng> pts = polyline.getPoints();
            for (LatLng coordinate : pts) {
                // Find out the maximum and minimum latitudes & longitudes
                // Latitude
                maxLat = maxLat != null ? Math.max(coordinate.latitude, maxLat) : coordinate.latitude;
                minLat = minLat != null ? Math.min(coordinate.latitude, minLat) : coordinate.latitude;

                // Longitude
                maxLon = maxLon != null ? Math.max(coordinate.longitude, maxLon) : coordinate.longitude;
                minLon = minLon != null ? Math.min(coordinate.longitude, minLon) : coordinate.longitude;
                hasPoints = true;
            }
        }

        if (hasPoints) {
            try {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(maxLat, maxLon));
                builder.include(new LatLng(minLat, minLon));
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
                isCameraPositionSet = true;
            } catch (Exception e) {
                Log.d(TAG, "getCameraPositionAndDrawMap: " + e);
            }
        }
        return isCameraPositionSet;
    }

}
