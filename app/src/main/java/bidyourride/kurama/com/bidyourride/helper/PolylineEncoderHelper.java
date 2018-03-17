package bidyourride.kurama.com.bidyourride.helper;

/**
 * Created by madhukurapati on 3/15/18.
 */

public class PolylineEncoderHelper {

    public static int floor1e5(double coordinate) {
        return (int)(Math.round(coordinate * 1e5));
    }
    public static String encodeSignedNumber(int num) {
        int sgn_num = num << 1;
        if (num < 0) {
            sgn_num = ~(sgn_num);
        }
        return(encodeNumber(sgn_num));
    }
    public static String encodeNumber(int num) {
        StringBuffer encodeString = new StringBuffer();
        while (num >= 0x20) {
            int nextValue = (0x20 | (num & 0x1f)) + 63;
            if (nextValue == 92) {
                encodeString.append((char)(nextValue));
            }
            encodeString.append((char)(nextValue));
            num >>= 5;
        }

        num += 63;
        if (num == 92) {
            encodeString.append((char)(num));
        }

        encodeString.append((char)(num));

        return encodeString.toString();

    }
}
