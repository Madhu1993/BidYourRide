package bidyourride.kurama.com.eapaka.model;

/**
 * Created by madhukurapati on 3/11/18.
 */

public class StepsObject {
    private PolylineObject polyline;
    public StepsObject(PolylineObject polyline) {
        this.polyline = polyline;
    }
    public PolylineObject getPolyline() {
        return polyline;
    }
}
