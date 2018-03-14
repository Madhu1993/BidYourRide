package bidyourride.kurama.com.bidyourride.helper;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.LegsObject;
import bidyourride.kurama.com.bidyourride.model.PolylineObject;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.StepsObject;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    public static Response.Listener<DirectionObject> createRequestSuccessListener(final GoogleMap googleMap) {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if (response.getStatus().equals("OK")) {
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(googleMap, mDirections);
                    } else {
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
    }

    public static void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);

        Polyline polyline = map.addPolyline(options);

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


    public static List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    directionList.addAll(singlePolyline);
                }
            }
        }
        return directionList;
    }

    public static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

}
