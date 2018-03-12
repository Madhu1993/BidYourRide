package bidyourride.kurama.com.bidyourride.model;

import java.util.List;

/**
 * Created by madhukurapati on 3/11/18.
 */

public class RouteObject {
    private List<LegsObject> legs;
    public RouteObject(List<LegsObject> legs) {
        this.legs = legs;
    }
    public List<LegsObject> getLegs() {
        return legs;
    }
}
