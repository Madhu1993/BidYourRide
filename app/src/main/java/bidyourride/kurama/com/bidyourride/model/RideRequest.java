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
    public String typeOfRequest;
    public String distancebetweenOriginAndLocation;
    public String dateOfRide;
    public String originLat;
    public String originLong;
    public String destinationLat;
    public String destinationLong;
    public String timeOfRide;
    public String originCityName;
    public String destinationCityName;
    public int starCount = 0;
    public String directionObjectJson;
    public String encodedMapString;

    public RideRequest(String uid, String author, String title, String origin, String destination, String typeOfRequest, String dateOfRide, String timeOfRide, String originCityName, String destinationCityName, String originLat, String originLong, String destinationLat, String destinationLong, String mDirections, String encodedMapString) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.origin = origin;
        this.destination = destination;
        this.typeOfRequest = typeOfRequest;
        this.dateOfRide = dateOfRide;
        this.timeOfRide = timeOfRide;
        this.originLat = originLat;
        this.originLong = originLong;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.originCityName = originCityName;
        this.destinationCityName = destinationCityName;
        this.directionObjectJson = mDirections;
        this.encodedMapString = encodedMapString;
    }

    public String getDirectionObjectJson() {
        return directionObjectJson;
    }

    public void setDirectionObjectJson(String directionObjectJson) {
        this.directionObjectJson = directionObjectJson;
    }

    public Map<String, Boolean> stars = new HashMap<>();

    public RideRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public String getmDirections() {
        return directionObjectJson;
    }

    public void setmDirections(String mDirections) {
        this.directionObjectJson = mDirections;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("origin", origin);
        result.put("title", title);
        result.put("destination", destination);
        result.put("typeOfRequest", typeOfRequest);
        result.put("dateOfRide", dateOfRide);
        result.put("timeOfRide", timeOfRide);
        result.put("stars", stars);
        result.put("originLat", originLat);
        result.put("originLong", originLong);
        result.put("destinationLat", destinationLat);
        result.put("destinationLong", destinationLong);
        result.put("destinationCityName", destinationCityName);
        result.put("originCityName", originCityName);
        result.put("directionObjectJson", directionObjectJson);
        result.put("encodedMapString", encodedMapString);
        return result;
    }

    public String getOriginCityName() {
        return originCityName;
    }

    public void setOriginCityName(String originCityName) {
        this.originCityName = originCityName;
    }

    public String getDestinationCityName() {
        return destinationCityName;
    }

    public void setDestinationCityName(String destinationCityName) {
        this.destinationCityName = destinationCityName;
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

    public String getEncodedMapString() {
        return encodedMapString;
    }

    public void setEncodedMapString(String encodedMapString) {
        this.encodedMapString = encodedMapString;
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

    public String getTypeOfRequest() {
        return typeOfRequest;
    }

    public void setTypeOfRequest(String typeOfRequest) {
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

    public String getOriginLat() {
        return originLat;
    }

    public void setOriginLat(String originLat) {
        this.originLat = originLat;
    }

    public String getOriginLong() {
        return originLong;
    }

    public void setOriginLong(String originLong) {
        this.originLong = originLong;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(String destinationLong) {
        this.destinationLong = destinationLong;
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
