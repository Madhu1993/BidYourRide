package bidyourride.kurama.com.bidyourride.model;

/**
 * Created by madhukurapati on 3/16/18.
 */

public class Overview_polyline
{
    private String points;

    public String getPoints ()
    {
        return points;
    }

    public void setPoints (String points)
    {
        this.points = points;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [points = "+points+"]";
    }
}
