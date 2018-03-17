package bidyourride.kurama.com.bidyourride.model;

/**
 * Created by madhukurapati on 3/11/18.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DirectionObject implements Parcelable {
    private List<RouteObject> routes;
    private String status;

    public DirectionObject() {

    }

    public DirectionObject(List<RouteObject> routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    protected DirectionObject(Parcel in) {
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DirectionObject> CREATOR = new Creator<DirectionObject>() {
        @Override
        public DirectionObject createFromParcel(Parcel in) {
            return new DirectionObject(in);
        }

        @Override
        public DirectionObject[] newArray(int size) {
            return new DirectionObject[size];
        }
    };

    public List<RouteObject> getRoutes() {
        return routes;
    }

    public String getStatus() {
        return status;
    }
}

/*{
        "geocoded_waypoints" : [
        {
        "geocoder_status" : "OK",
        "place_id" : "ChIJZymwbofzU0YRvgugqrVwH8Q",
        "types" : [ "street_address" ]
        },
        {
        "geocoder_status" : "OK",
        "place_id" : "EiJOeWdhdGFuIDMyLCAyNDIgMzEgSMO2cmJ5LCBTdmVyaWdl",
        "types" : [ "street_address" ]
        }
        ],
        "routes" : [
        {
        "bounds" : {
        "northeast" : {
        "lat" : 55.8541564,
        "lng" : 13.661235
        },
        "southwest" : {
        "lat" : 55.85187149999999,
        "lng" : 13.660381
        }
        },
        "copyrights" : "Map data ©2016 Google",
        "legs" : [
        {
        "distance" : {
        "text" : "0.3 km",
        "value" : 260
        },
        "duration" : {
        "text" : "1 min",
        "value" : 84
        },
        "end_address" : "Nygatan 32, 242 31 Hörby, Sweden",
        "end_location" : {
        "lat" : 55.85187149999999,
        "lng" : 13.660381
        },
        "start_address" : "Nygatan 12B, 242 31 Hörby, Sweden",
        "start_location" : {
        "lat" : 55.8541564,
        "lng" : 13.661235
        },
        "steps" : [
        {
        "distance" : {
        "text" : "0.3 km",
        "value" : 260
        },
        "duration" : {
        "text" : "1 min",
        "value" : 84
        },
        "end_location" : {
        "lat" : 55.85187149999999,
        "lng" : 13.660381
        },
        "html_instructions" : "Head \u003cb\u003esouth\u003c/b\u003e on \u003cb\u003eNygatan\u003c/b\u003e toward \u003cb\u003eKvarngatan\u003c/b\u003e\u003cdiv style=\"font-size:0.9em\"\u003eDestination will be on the left\u003c/div\u003e",
        "polyline" : {
        "points" : "o_|sIwekrAVHxBj@|Bh@nBr@d@Hb@L"
        },
        "start_location" : {
        "lat" : 55.8541564,
        "lng" : 13.661235
        },
        "travel_mode" : "DRIVING"
        }
        ],
        "traffic_speed_entry" : [],
        "via_waypoint" : []
        }
        ],
        "overview_polyline" : {
        "points" : "o_|sIwekrApCt@|Bh@nBr@hAV"
        },
        "summary" : "Nygatan",
        "warnings" : [],
        "waypoint_order" : []
        }
        ],
        "status" : "OK"
        }*/
