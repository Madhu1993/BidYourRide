package bidyourride.kurama.com.eapaka.helper;

/**
 * Created by madhukurapati on 3/13/18.
 */

public class AddRideActivityHelper {
    public static String capitalize(String line) {
        if(line == null){
            return " ";
        }
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String convertDoubleToString(Double d) {
        return String.valueOf(d);
    }

    public static Double getMiles(Float s) {
        return s * 0.000621371192;
    }
}
