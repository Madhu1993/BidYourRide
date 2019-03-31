package bidyourride.kurama.com.eapaka.helper;

import android.os.Binder;

import bidyourride.kurama.com.eapaka.model.RideRequest;

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
