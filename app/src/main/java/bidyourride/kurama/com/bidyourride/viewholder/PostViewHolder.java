package bidyourride.kurama.com.bidyourride.viewholder;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.model.RideRequest;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public de.hdodenhof.circleimageview.CircleImageView authorImageView;

    public FrameLayout mapLayout;
    public SupportMapFragment mapFragment;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        /*location = itemView.findViewById(R.id.location_name);*/
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        /*bodyView = itemView.findViewById(R.id.post_body);*/
        authorImageView = itemView.findViewById(R.id.post_author_photo);
        mapLayout = itemView.findViewById(R.id.map);

    }

    public SupportMapFragment getMapFragmentAndCallback(Context context, OnMapReadyCallback callback) {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(callback);
        }
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
        return mapFragment;
    }

    public void removeMapFragment(Context context) {
        if (mapFragment != null) {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
            mapFragment = null;
        }
    }

    public void bindToPost(Context context, RideRequest rideRequest, View.OnClickListener starClickListener) {
        titleView.setText(rideRequest.title);
        authorView.setText(rideRequest.author);
        numStarsView.setText(String.valueOf(rideRequest.starCount));
        starView.setOnClickListener(starClickListener);
    }
}
