package bidyourride.kurama.com.eapaka.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import bidyourride.kurama.com.eapaka.R;
import bidyourride.kurama.com.eapaka.helper.AddRideActivityHelper;
import bidyourride.kurama.com.eapaka.model.DirectionObject;
import bidyourride.kurama.com.eapaka.model.Distance;
import bidyourride.kurama.com.eapaka.model.Duration;
import bidyourride.kurama.com.eapaka.model.LegsObject;
import bidyourride.kurama.com.eapaka.model.RideRequest;

public class RidesViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "RidesViewHolder";
    private TextView titleView;
    private TextView authorView;
    private TextView dateView;
    private TextView timeView;
    private TextView distanceView;
    private TextView durationView;
    public ImageView starView;
    private TextView numStarsView;
    private Double originLat, originLong, destinationLat, destinationLong;
    private View layout;
    ImageView imageView;
    private Gson gson;
    DirectionObject directionObject;
    String mapImageStringDecoded;
    String checkIfTitleHasNullValue = "Null";

    public RidesViewHolder(View itemView) {
        super(itemView);
        layout = itemView;
        titleView = itemView.findViewById(R.id.post_title);
        dateView = itemView.findViewById(R.id.date_tv);
        timeView = itemView.findViewById(R.id.time_tv);
        distanceView = itemView.findViewById(R.id.distace_tv);
        durationView = itemView.findViewById(R.id.duration_tv);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        imageView = itemView.findViewById(R.id.map);


    }

    public void bindToPost(RideRequest rideRequest, View.OnClickListener starClickListener) {
        if(rideRequest.title.toLowerCase().contains(checkIfTitleHasNullValue.toLowerCase())){
            String omitNullFromTitle = AddRideActivityHelper.capitalize(rideRequest.title.replaceAll(checkIfTitleHasNullValue, ""));
            titleView.setText(omitNullFromTitle);
        }else {
            titleView.setText(rideRequest.title);
        }
        authorView.setText(rideRequest.author);
        numStarsView.setText(String.valueOf(rideRequest.starCount));
        starView.setOnClickListener(starClickListener);
        dateView.setText(rideRequest.dateOfRide);
        timeView.setText(rideRequest.timeOfRide);
        String originLatS = rideRequest.getOriginLat();
        String originLongS = rideRequest.getOriginLong();
        String destinationLatS = rideRequest.getDestinationLat();
        String destinationLongS = rideRequest.getDestinationLong();
        originLat = Double.parseDouble(originLatS);
        originLong = Double.parseDouble(originLongS);
        destinationLat = Double.parseDouble(destinationLatS);
        destinationLong = Double.parseDouble(destinationLongS);
        gson = new Gson();
        String directionObjectJson = rideRequest.getmDirections();
        directionObject = gson.fromJson(directionObjectJson, DirectionObject.class);

        if(directionObject != null){
            List<LegsObject> legsObjects = directionObject.getRoutes().get(0).getLegs();
            Distance distance = legsObjects.get(0).getDistance();
            Duration duration = legsObjects.get(0).getDuration();
            distanceView.setText(distance.getText());
            String avgTimeText = "Travel duration: " + duration.getText();
            durationView.setText(avgTimeText);

        }
        mapImageStringDecoded = rideRequest.getEncodedMapString();
        if (mapImageStringDecoded != null && !TextUtils.isEmpty(mapImageStringDecoded))
            imageView.setImageBitmap(decodeImageFromFirebase(mapImageStringDecoded));
    }

    private Bitmap decodeImageFromFirebase(String image) {
        byte[] decodeImage = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.length);
    }

}
