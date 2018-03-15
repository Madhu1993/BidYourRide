package bidyourride.kurama.com.bidyourride.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.helper.GoogleMapHelper;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = "PostViewHolder";
    private TextView titleView;
    private TextView authorView;
    public ImageView starView;
    private TextView numStarsView;
    private Double originLat, originLong, destinationLat, destinationLong;
    private MapView mapView;
    public GoogleMap map;
    private View layout;
    private Gson gson;
    List<LatLng> retrievedLatLongFromJson;
    List<RouteObject> listRouteObjects;
    PolylineOptions options;
    Polyline polyline;


    public PostViewHolder(View itemView) {
        super(itemView);
        layout = itemView;

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        /*location = itemView.findViewById(R.id.location_name);*/
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        /*bodyView = itemView.findViewById(R.id.post_body);*/
        de.hdodenhof.circleimageview.CircleImageView authorImageView = itemView.findViewById(R.id.post_author_photo);
        /*mapLayout = itemView.findViewById(R.id.map);*/
        mapView = layout.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

    }

    public void bindToPost(Context context, String refKey, RideRequest rideRequest, View.OnClickListener starClickListener) {
        titleView.setText(rideRequest.title);
        authorView.setText(rideRequest.author);
        numStarsView.setText(String.valueOf(rideRequest.starCount));
        starView.setOnClickListener(starClickListener);
        String originLatS = rideRequest.getOriginLat();
        String originLongS = rideRequest.getOriginLong();
        String destinationLatS = rideRequest.getDestinationLat();
        String destinationLongS = rideRequest.getDestinationLong();
        originLat = Double.parseDouble(originLatS);
        originLong = Double.parseDouble(originLongS);
        destinationLat = Double.parseDouble(destinationLatS);
        destinationLong = Double.parseDouble(destinationLongS);
        gson = new Gson();

        /*String directionObjectJson = rideRequest.getmDirections();*/
        String directionListJson = rideRequest.getDirectionListJson();
        /*DirectionObject directionObject = gson.fromJson(directionObjectJson, DirectionObject.class);*/
        /*listRouteObjects = directionObject.getRoutes();*/
        Type listOfLatLong = new TypeToken<List<LatLng>>() {
        }.getType();
        retrievedLatLongFromJson = gson.fromJson(directionListJson, listOfLatLong);
        setMapLocation(retrievedLatLongFromJson);
    }

    @Override
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
    }


    public void setMapLocation(List<LatLng> latLngs) {
        if (map == null) return;
        if (originLat == null | originLong == null) {
            return;
        }
        addMarkersAndCameraAngle(latLngs);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void addMarkersAndCameraAngle(List<LatLng> latLngs) {
        options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(latLngs);
        polyline = map.addPolyline(options);
        if (polyline.getPoints() != null) {
            GoogleMapHelper.addMarkersOfOriginDestination(map, originLat, originLong, destinationLat, destinationLong);
            GoogleMapHelper.drawRouteOnMap(polyline, map, latLngs);
        }
    }

    public void bindView(int pos) {
        layout.setTag(this);
        //setMapLocation(retrievedLatLongFromJson);
        if (retrievedLatLongFromJson == null) {
            LatLng latLng = new LatLng(originLat, originLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 48f));
            map.addMarker(new MarkerOptions().position(latLng));
        } else {
            setMapLocation(retrievedLatLongFromJson);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
