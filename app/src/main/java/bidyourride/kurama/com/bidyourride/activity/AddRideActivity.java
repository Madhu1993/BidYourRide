package bidyourride.kurama.com.bidyourride.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.helper.AddRideActivityHelper;
import bidyourride.kurama.com.bidyourride.model.Category;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.GsonRequest;
import bidyourride.kurama.com.bidyourride.model.Overview_polyline;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.User;
import bidyourride.kurama.com.bidyourride.rest.Helper;
import bidyourride.kurama.com.bidyourride.rest.VolleySingleton;
import bidyourride.kurama.com.bidyourride.view.CircleImageView;

/**
 * Created by madhukurapati on 3/7/18.
 */

public class AddRideActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private Spinner spinner;
    private EditText originLocation;
    private EditText destinationLocation;
    private AutocompleteFilter autocompleteFilter;
    private Intent intent;
    private CircleImageView originImage, destinationImage;
    private TextView datePicker, timePicker;
    private Button datePicketButton, timePickerButton, submit;
    private Calendar calendar;
    private Location originLatLong, destLatLong;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String originCityName;
    private String destinationCityName;
    private String typeOfRequest;
    private String originLatString = "";
    private String originLongString = "";
    private String destinationLatString = " ";
    private String destinationLongString = " ";
    private String directionObjectJson;
    private String directionListJson;
    Double destinationlat;
    Double destinationlongitude;
    Double originLat;
    Double originLong;
    Gson gson;
    String localTimeForFirebase;

    static final int ORIGIN_AUTOCOMPLETE_REQUEST_CODE = 1;
    static final int DESTINATION_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final String REQUIRED = "Required";
    private static final String TAG = "AddRideActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_ride);
        super.onCreate(savedInstanceState);
        originImage = findViewById(R.id.origin_image);
        originImage.getLayoutParams().height = 100;
        originImage.getLayoutParams().width = 100;
        originImage.requestLayout();
        gson = new Gson();

        destinationImage = findViewById(R.id.destination_image);
        destinationImage.getLayoutParams().height = 100;
        destinationImage.getLayoutParams().width = 100;
        destinationImage.requestLayout();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        originLocation = findViewById(R.id.origin_location);
        destinationLocation = findViewById(R.id.destination_location);
        datePicker = findViewById(R.id.date_picker_tv);
        timePicker = findViewById(R.id.time_picker_tv);
        datePicketButton = findViewById(R.id.date_picker_button);
        timePickerButton = findViewById(R.id.time_picker_button);
        submit = findViewById(R.id.submit);
        originLocation.setOnFocusChangeListener(this);
        destinationLocation.setOnFocusChangeListener(this);
        datePicketButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);
        submit.setOnClickListener(this);
        calendar = Calendar.getInstance();
        originLatLong = new Location("");
        destLatLong = new Location("");

        spinner = findViewById(R.id.spinner);
        setDataToSpinner();
        spinner.setOnItemSelectedListener(this);

        autocompleteFilter = new AutocompleteFilter.Builder()
                .setCountry("US")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        try {
            intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(autocompleteFilter)
                            .build(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(AddRideActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Log.d(TAG, "onRequestPermissionsResult: " + " ");
                    }

                }
            }

        }
    }


    private void setDataToSpinner() {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new Category(" 1", "Request A Ride "));
        categoryArrayList.add(new Category(" 2", "Give A Ride "));

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryArrayList);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                typeOfRequest = "1";
                return;

            case 1:
                typeOfRequest = "2";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void destinationPlaceComplete(View v) {
        if (v.hasFocus()) {
            v.clearFocus();
        }
        try {
            startActivityForResult(intent, DESTINATION_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            Log.d("", "destinationPlaceComplete: ");
        }

    }

    private void originPlaceComplete(View v) {
        if (v.hasFocus()) {
            v.clearFocus();
        }
        try {
            startActivityForResult(intent, ORIGIN_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            Log.d("", "destinationPlaceComplete: ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ORIGIN_AUTOCOMPLETE_REQUEST_CODE:
                checkIfOriginResultIsOk(resultCode, data);
                return;
            case DESTINATION_AUTOCOMPLETE_REQUEST_CODE:
                checkIfDestinationResultIsOk(resultCode, data);
        }
    }

    private void checkIfDestinationResultIsOk(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            destinationLocation.setText(place.getAddress());
            removeErrorRequired(R.id.destination_location);
            destinationlat = place.getLatLng().latitude;
            destinationlongitude = place.getLatLng().longitude;

            destinationCityName = getCityName(destinationlat, destinationlongitude);
            destLatLong.setLatitude(destinationlat);
            destLatLong.setLongitude(destinationlongitude);
            destinationLatString = AddRideActivityHelper.convertDoubleToString(destinationlat);
            destinationLongString = AddRideActivityHelper.convertDoubleToString(destinationlongitude);
            Log.d("", "checkIfDestinationResultIsOk: " + place.getLatLng());
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            //Status status = PlaceAutocomplete.getStatus(this, data);
            //TODO: Handle the error.
        } else if (resultCode == RESULT_CANCELED) {

        }
    }

    private void checkIfOriginResultIsOk(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            originLocation.setText(place.getAddress());
            originLat = place.getLatLng().latitude;
            originLong = place.getLatLng().longitude;

            originCityName = getCityName(originLat, originLong);
            originLatLong.setLatitude(originLat);
            originLatLong.setLongitude(originLong);
            originLatString = AddRideActivityHelper.convertDoubleToString(originLat);
            originLongString = AddRideActivityHelper.convertDoubleToString(originLong);

            removeErrorRequired(R.id.origin_location);
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

        } else if (resultCode == RESULT_CANCELED) {
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.origin_location) {
            if (v.hasFocus()) {
                originPlaceComplete(v);
                return;
            }

        }

        if (v.getId() == R.id.destination_location) {
            if (v.hasFocus()) {
                destinationPlaceComplete(v);
            }
        }
    }

    private boolean isOriginFieldEmpty() {
        Boolean value = true;
        String dest = originLocation.getText().toString();
        if (!dest.isEmpty()) {
            value = false;
        }
        return value;

    }

    private boolean isDestFieldEmpty() {
        Boolean value = true;
        String dest = destinationLocation.getText().toString();
        if (!dest.isEmpty()) {
            value = false;
        }
        return value;
    }

    private boolean isDatePickerEmpty() {
        Boolean value = true;
        String dest = datePicker.getText().toString();
        if (!dest.isEmpty()) {
            value = false;
        }
        return value;
    }

    private boolean isTimePickerEmpty() {
        Boolean value = true;
        String dest = timePicker.getText().toString();
        if (!dest.isEmpty()) {
            value = false;
        }
        return value;
    }

    private void removeErrorRequired(Integer id) {
        switch (id) {
            case R.id.origin_location:
                originLocation.setError(null);
                return;
            case R.id.destination_location:
                destinationLocation.setError(null);
                return;
            case R.id.date_picker_tv:
                datePicker.setError(null);
                return;
            case R.id.time_picker_tv:
                timePicker.setError(null);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String dayMode = " ";
        if (hourOfDay >= 12) {
            dayMode = " PM ";
        } else {
            dayMode = " AM ";
        }
        String time = hourOfDay + ":" + minute + " " + dayMode;
        timePicker.setText(time);

        removeErrorRequired(R.id.time_picker_tv);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //2018-03-16
        DecimalFormat mFormat = new DecimalFormat("00");
        monthOfYear = monthOfYear + 1;
        String derivedDate = (year) + "-" + ((mFormat.format(Double.valueOf(monthOfYear))) + "-" + mFormat.format(Double.valueOf(dayOfMonth)));
        datePicker.setText(derivedDate);
        removeErrorRequired(R.id.date_picker_tv);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker_button:
                showDatePicker();
                return;
            case R.id.time_picker_button:
                showTimePicker();
                return;
            case R.id.submit:
                if (checkLocationPermission()) {
                    getPolylines();
                }
        }

    }

    public void addRideToFirebase(final String directionObjectJson, final String directionListJson, final String encodedMapString) {
        if (!isDestFieldEmpty() & !isOriginFieldEmpty() & !isDatePickerEmpty() & !isTimePickerEmpty()) {
            final String time = timePicker.getText().toString();
            final String date = datePicker.getText().toString();
            final String originFullAddress = originLocation.getText().toString();
            final String destinationFullAddress = destinationLocation.getText().toString();
            if (TextUtils.isEmpty(typeOfRequest)) {
                typeOfRequest = "1";
            }
            final String userId = getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user == null) {
                                Log.e(TAG, "User " + userId + " is unexpectedly null");
                                Toast.makeText(AddRideActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String title = concatenateOriginAndDest(originCityName, destinationCityName);
                                try {
                                    String capTitle = AddRideActivityHelper.capitalize(title);
                                    writeNewRide(userId, user.username, capTitle, originFullAddress, destinationFullAddress, typeOfRequest, date, time, originCityName, destinationCityName, originLatString, originLongString, destinationLatString, destinationLongString, directionObjectJson, encodedMapString);
                                } catch (Exception e) {
                                    writeNewRide(userId, user.username, title, originFullAddress, destinationFullAddress, typeOfRequest, date, time, originCityName, destinationCityName, originLatString, originLongString, destinationLatString, destinationLongString, directionObjectJson, encodedMapString);
                                }
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });

        } else {
            setRequiredErrorForEmptyFields();
        }
    }

    private void setRequiredErrorForEmptyFields() {
        if (isOriginFieldEmpty()) {
            originLocation.setError(REQUIRED);
        }

        if (isDestFieldEmpty()) {
            destinationLocation.setError(REQUIRED);
        }

        if (isTimePickerEmpty()) {
            timePicker.setError(REQUIRED);
        }

        if (isDatePickerEmpty()) {
            datePicker.setError(REQUIRED);
        }
    }

    private void getPolylines() {
        if (!isDestFieldEmpty() & !isOriginFieldEmpty() & !isDatePickerEmpty() & !isTimePickerEmpty()) {
            String directionApiPath = Helper.getUrl(String.valueOf(originLatString), String.valueOf(originLongString),
                    String.valueOf(destinationLatString), String.valueOf(destinationLongString));
            getDirectionFromDirectionApiServer(directionApiPath);
        } else {
            setRequiredErrorForEmptyFields();
        }
    }

    private void writeNewRide(String userId, String username, String capTitle, String origin, String destination, String typeOfRequest, String date, String time, String originCityName, String destinationCityName, String originLat, String originLong, String destinationLat, String destinationLong, String directionObjectJson, String encodedMapString) {
        String key = mDatabase.child("rides").push().getKey();

        RideRequest rideRequest = new RideRequest(userId, username, capTitle, origin, destination, typeOfRequest, date, time, originCityName, destinationCityName, originLat, originLong, destinationLat, destinationLong, directionObjectJson, encodedMapString);
        Map<String, Object> rideValues = rideRequest.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (typeOfRequest.equals("1")) {
            childUpdates.put("/rides-requested/" + key, rideValues);
        } else if (typeOfRequest.equals("2")) {
            childUpdates.put("/rides-provided/" + key, rideValues);
        }
        childUpdates.put("/user-rides/" + userId + "/" + key, rideValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void getDirectionFromDirectionApiServer(String url) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }

    public com.android.volley.Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new com.android.volley.Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    directionObjectJson = gson.toJson(response);
                    List<RouteObject> routeObject = response.getRoutes();
                    Overview_polyline overview_polyline = routeObject.get(0).getOverview_polyline();
                    String point = overview_polyline.getPoints();
                    setImageView(point);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static com.android.volley.Response.ErrorListener createRequestErrorListener() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private String concatenateOriginAndDest(String originCityName, String destinationCityName) {
        if (originCityName.toLowerCase().contains("Null".toLowerCase())) {
            if (destinationCityName.toLowerCase().contains("Null".toLowerCase())) {
                return " ";
            } else {
                String str1 = " To ";
                return str1.concat(destinationCityName);
            }
        } else {
            String str1 = originCityName + " to ";
            return str1.concat(destinationCityName);
        }

    }

    private void showTimePicker() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        tpd.show(getFragmentManager(), "Choose Time:");
    }


    private void showDatePicker() {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.setTime(getMaxDate());

        dpd.setMinDate(minDate);
        dpd.setMaxDate(maxDate);
        dpd.show(getFragmentManager(), "Choose Date:");

        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
    }

    private Date getMaxDate() {
        int noOfDays = 10; //i.e two weeks
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        return calendar.getTime();
    }


    private String getCityName(Double lt, Double lg) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lt, lg, 1);
            String cityName;
            cityName = addresses.get(0).getLocality();
            return cityName;
        } catch (Exception e) {
            return " ";
        }

    }

    public class PostToFirebase extends AsyncTask<Void, Void, Void> {
        String directionOb;
        String directionList;
        String encodedMapString;

        public PostToFirebase(String directionOb, String directionList, String encodedMapString) {
            this.directionOb = directionOb;
            this.directionList = directionList;
            this.encodedMapString = encodedMapString;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            addRideToFirebase(directionOb, directionList, encodedMapString);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void setImageView(String polyEncode) {
        String directionApiPath = Helper.getStaticImageApi(originLat, originLong, destinationlat, destinationlongitude, polyEncode);
        Log.d(TAG, "setImageView: " + directionApiPath);
        ImageRequest imageRequest = new ImageRequest(directionApiPath, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap response) {
                encodeBitmapToString(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Volley" + error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(imageRequest);

    }

    private void encodeBitmapToString(Bitmap response) {
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        response.compress(Bitmap.CompressFormat.PNG, 100, baos2);
        String encodeMapString = Base64.encodeToString(baos2.toByteArray(), Base64.DEFAULT);
        PostToFirebase postToFirebase = new PostToFirebase(directionObjectJson, directionListJson, encodeMapString);
        Log.d(TAG, "encodeBitmapToString:+ before postToFirebase ");
        postToFirebase.execute();
    }


}

