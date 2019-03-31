package bidyourride.kurama.com.eapaka.helper;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

/**
 * Created by madhukurapati on 3/13/18.
 */

public class GoogleMapHelper {

    private static final String TAG = "GoogleMapHelper";

    public static void addMarkersOfOriginDestination(GoogleMap map, Double originLat, Double originLong, Double destinationLat, Double destinationLong) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(originLat, originLong))
                .title("O"));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(destinationLat, destinationLong))
                .title("D"));
    }

    public static void drawRouteOnMap(Polyline polyline, GoogleMap map, List<LatLng> positions) {
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
                if (!map.getUiSettings().isZoomControlsEnabled()) {
                    map.getUiSettings().setZoomControlsEnabled(true);
                }
                isCameraPositionSet = true;
            } catch (Exception e) {
                Log.d(TAG, "getCameraPositionAndDrawMap: " + e);
            }
        }
        return isCameraPositionSet;
    }

    /*public static class GetDirectionsFromPositions extends AsyncTask<Void, Void, List<LatLng>> {
        List<LatLng> directionList;
        List<RouteObject> routeObjectList;

        public GetDirectionsFromPositions(List<RouteObject> routeObjectList) {
            this.routeObjectList = routeObjectList;
        }

        private List<LatLng> decodePoly(String encoded) {
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

        private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
            directionList = new ArrayList<LatLng>();
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

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            directionList = getDirectionPolylines(routeObjectList);
            return directionList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(getApplicationContext(), "LOADING", Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<LatLng> latLngs) {

        }*//*
    }*/

    /* @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        googleMap.setOnMapClickListener(this);
        map = googleMap;
        if (retrievedLatLongFromJson == null) {
            LatLng latLng = new LatLng(originLat, originLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 48f));
            map.addMarker(new MarkerOptions().position(latLng));
        } else {
            setMapLocation(retrievedLatLongFromJson);
        }
    }*/


    /*public void setMapLocation(List<LatLng> latLngs) {
        if (map == null) return;
        if (originLat == null | originLong == null) {
            return;
        }
        addMarkersAndCameraAngle(latLngs);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }*/

    /*private void addMarkersAndCameraAngle(List<LatLng> latLngs) {
        options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(latLngs);
        polyline = map.addPolyline(options);
        if (polyline.getPoints() != null) {
            GoogleMapHelper.addMarkersOfOriginDestination(map, originLat, originLong, destinationLat, destinationLong);
            GoogleMapHelper.drawRouteOnMap(polyline, map, latLngs);
        }
    }*/

    /* gson = new Gson();

        *//*String directionObjectJson = rideRequest.getmDirections();*//*
        String directionListJson = rideRequest.getDirectionListJson();
        Type listOfLatLong = new TypeToken<List<LatLng>>() {
        }.getType();
        retrievedLatLongFromJson = gson.fromJson(directionListJson, listOfLatLong);*/

}
