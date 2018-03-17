package bidyourride.kurama.com.bidyourride.model;

/**
 * Created by madhukurapati on 3/11/18.
 */

import java.util.List;
public class LegsObject {

    private Duration duration;

    private Distance distance;

    private End_location end_location;

    private String start_address;

    private String end_address;
    private List<StepsObject> steps;
    public LegsObject(List<StepsObject> steps) {
        this.steps = steps;
    }
    public List<StepsObject> getSteps() {
        return steps;

    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }
}

