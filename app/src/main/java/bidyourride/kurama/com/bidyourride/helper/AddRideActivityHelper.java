package bidyourride.kurama.com.bidyourride.helper;

import android.content.Context;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.List;
import java.util.Locale;

/**
 * Created by madhukurapati on 3/13/18.
 */

public class AddRideActivityHelper {
    public static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String convertDoubleToString(Double d) {
        String value = String.valueOf(d);
        return value;
    }

    public static Double getMiles(Float s) {
        return s * 0.000621371192;
    }
}
