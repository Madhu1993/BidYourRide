package bidyourride.kurama.com.bidyourride.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by madhukurapati on 3/22/18.
 */

public class RequestBid {
    public String uid;
    public String author;
    public String dateAndTime;
    public double bidValue;

    public RequestBid() {
    }

    public RequestBid(String uid, String author, String dateAndTime, double bidValue) {
        this.uid = uid;
        this.author = author;
        this.dateAndTime = dateAndTime;
        this.bidValue = bidValue;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("bidValue", bidValue);
        result.put("dateAndTime", dateAndTime);
        return result;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setBidValue(double bidValue) {
        this.bidValue = bidValue;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getBidValue() {
        return bidValue;
    }
}
