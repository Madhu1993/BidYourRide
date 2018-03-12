package bidyourride.kurama.com.bidyourride.model;

/**
 * Created by madhukurapati on 3/11/18.
 */

import java.util.List;
public class LegsObject {
    private List<StepsObject> steps;
    public LegsObject(List<StepsObject> steps) {
        this.steps = steps;
    }
    public List<StepsObject> getSteps() {
        return steps;
    }
}

