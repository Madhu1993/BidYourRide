package bidyourride.kurama.com.eapaka.model;

/**
 * Created by madhukurapati on 3/16/18.
 */

public class Duration
{
    private String text;

    private String value;

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+text+", value = "+value+"]";
    }
}
