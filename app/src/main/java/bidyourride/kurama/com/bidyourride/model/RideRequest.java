package bidyourride.kurama.com.bidyourride.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by madhukurapati on 11/20/17.
 */

public class RideRequest {
    public String uid;
    public String author;
    public String title;
    public String origin;
    public String destination;
    public int typeOfRequest;
    public String distancebetweenOriginAndLocation;
    public String dateOfRide;
    public String timeOfRide;
    public int starCount = 0;

    public RideRequest(String uid, String author, String title, String origin, String destination, int typeOfRequest, String distancebetweenOriginAndLocation, String dateOfRide, String timeOfRide, String locationPostedFrom) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.origin = origin;
        this.destination = destination;
        this.typeOfRequest = typeOfRequest;
        this.distancebetweenOriginAndLocation = distancebetweenOriginAndLocation;
        this.dateOfRide = dateOfRide;
        this.timeOfRide = timeOfRide;
        this.locationPostedFrom = locationPostedFrom;
    }

    public Map<String, Boolean> stars = new HashMap<>();
    public String locationPostedFrom;

    public RideRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    @Exclude
    public Map<String, Object> toMapWithoutimageEncoded() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("origin", origin);
        result.put("title", title);
        result.put("destination", destination);
        result.put("typeOfRequest", typeOfRequest);
        result.put("distancebetweenOriginAndLocation", distancebetweenOriginAndLocation);
        result.put("dateOfRide", dateOfRide);
        result.put("timeOfRide", timeOfRide);
        result.put("timeOfRide", timeOfRide);
        result.put("locationPostedFrom", locationPostedFrom);
        result.put("stars", stars);
        return result;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTypeOfRequest() {
        return typeOfRequest;
    }

    public void setTypeOfRequest(int typeOfRequest) {
        this.typeOfRequest = typeOfRequest;
    }

    public String getDistancebetweenOriginAndLocation() {
        return distancebetweenOriginAndLocation;
    }

    public void setDistancebetweenOriginAndLocation(String distancebetweenOriginAndLocation) {
        this.distancebetweenOriginAndLocation = distancebetweenOriginAndLocation;
    }

    public String getDateOfRide() {
        return dateOfRide;
    }

    public void setDateOfRide(String dateOfRide) {
        this.dateOfRide = dateOfRide;
    }

    public String getTimeOfRide() {
        return timeOfRide;
    }

    public void setTimeOfRide(String timeOfRide) {
        this.timeOfRide = timeOfRide;
    }

    public int getStarCount() {
        return starCount;
    }

    public String getLocationPostedFrom() {
        return locationPostedFrom;
    }

    public void setLocationPostedFrom(String locationPostedFrom) {
        this.locationPostedFrom = locationPostedFrom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }
    // [END post_to_map]

}
