package bidyourride.kurama.com.bidyourride.helper;

import android.os.Binder;

import bidyourride.kurama.com.bidyourride.model.RideRequest;

/**
 * Created by madhukurapati on 3/17/18.
 */

public class ObjectWrapperForBinder extends Binder {

    private final RideRequest mData;

    public ObjectWrapperForBinder(RideRequest data) {
        mData = data;
    }

    public RideRequest getData() {
        return mData;
    }
}
