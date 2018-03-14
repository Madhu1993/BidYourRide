package bidyourride.kurama.com.bidyourride.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.gson.Gson;

import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.helper.GoogleMapHelper;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.RideRequest;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    private static final String TAG = "PostViewHolder";
    private TextView titleView;
    private TextView authorView;
    public ImageView starView;
    private TextView numStarsView;
    String positions;
    DirectionObject directionObject;

    private Double originLat, originLong, destinationLat, destinationLong;
    private MapView mapView;
    public GoogleMap map;
    private View layout;
    private Gson gson;
    private List<LatLng> mDirections;

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
            // Initialise the MapView
            mapView.onCreate(null);
            // Set the map ready callback to receive the GoogleMap object
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
        if (rideRequest.getmDirections() == null) {
            mDirections = null;
        } else {
            String positions = rideRequest.getmDirections();
            Log.d(TAG, "bindToPost: " + positions);
            DirectionObject directionObject = gson.fromJson(positions, DirectionObject.class);
            Log.d(TAG, "bindToPost: " + directionObject.getRoutes());
            mDirections = GoogleMapHelper.getDirectionPolylines(directionObject.getRoutes());
            //addMarkersAndCameraAngle(mDirections);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        map = googleMap;
        setMapLocation();
    }

    public void setMapLocation() {
        if (map == null) return;
        if (originLat == null | originLong == null) {
            return;
        }
        if (mDirections == null) {
            LatLng latLng = new LatLng(originLat, originLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            map.addMarker(new MarkerOptions().position(latLng));
        } else {
            addMarkersAndCameraAngle(mDirections);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void addMarkersAndCameraAngle(List<LatLng> listLatLong) {
        GoogleMapHelper.addMarkersOfOriginDestination(map, originLat, originLong, destinationLat, destinationLong);
        GoogleMapHelper.drawRouteOnMap(map, listLatLong);
    }

    public void bindView(int pos) {
        // Store a reference of the ViewHolder object in the layout.
        layout.setTag(this);
        setMapLocation();
    }


}
