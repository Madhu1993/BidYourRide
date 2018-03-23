package bidyourride.kurama.com.bidyourride.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bidyourride.kurama.com.bidyourride.R;

/**
 * Created by madhukurapati on 3/22/18.
 */

public class RideRequestBidViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bidValue;
    public TextView dateAndTime;

    public RideRequestBidViewHolder(View itemView) {
        super(itemView);

        authorView = (TextView) itemView.findViewById(R.id.bid_author);
        bidValue = (TextView) itemView.findViewById(R.id.item_bid_value);
        dateAndTime = (TextView) itemView.findViewById(R.id.time_stamp);
    }
}
