package bidyourride.kurama.com.bidyourride.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bidyourride.kurama.com.bidyourride.Config;
import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.model.Category;
import bidyourride.kurama.com.bidyourride.model.LocationResponse;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.User;
import bidyourride.kurama.com.bidyourride.rest.ApiInterface;
import bidyourride.kurama.com.bidyourride.rest.LocationClient;
import bidyourride.kurama.com.bidyourride.view.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private TextView datePicker, timePicker, maxDistancetv;
    private Button datePicketButton, timePickerButton, submit;
    private Calendar calendar;
    private Location originLatLong, destLatLong;
    private float distanceBwOriginAndDest;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private Double lat;
    private Double longitude;
    private String location;
    private int typeOfRequest;

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
        maxDistancetv = findViewById(R.id.max_distance_result);
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

        maxDistancetv.setTypeface(null, Typeface.BOLD);

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

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    private void setDataToSpinner() {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new Category(" 1", " Request A Ride "));
        categoryArrayList.add(new Category(" 2", " Give A Ride "));

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryArrayList);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        int idObtained = (int) id;
        switch (idObtained) {
            case 1:
                typeOfRequest = 1;
                return;

            case 2:
                typeOfRequest = 2;
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
            destLatLong.setLatitude(place.getLatLng().latitude);
            destLatLong.setLongitude(place.getLatLng().longitude);
            if (!isOriginFieldEmpty()) {
                findTheDistance();
            }
            Log.d("", "checkIfDestinationResultIsOk: " + place.getLatLng());
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            //Status status = PlaceAutocomplete.getStatus(this, data);
            //TODO: Handle the error.
        } else if (resultCode == RESULT_CANCELED) {

        }
    }

    private Double getMiles(Float s) {
        return s * 0.000621371192;
    }

    private void findTheDistance() {
        try {
            distanceBwOriginAndDest = originLatLong.distanceTo(destLatLong);
            Double miles = getMiles(distanceBwOriginAndDest);
            int a = (int) Math.round(miles);
            String str1 = Integer.toString(a);
            String str2 = " miles";
            String result = str1.concat(str2);
            maxDistancetv.setText(result);
        } catch (Exception e) {
            Log.d("AddRideActivity", "findTheDistance: " + e);
        }

    }

    private void checkIfOriginResultIsOk(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            originLocation.setText(place.getAddress());
            Log.d("AddRideActivity", "pl: ");
            originLatLong.setLatitude(place.getLatLng().latitude);
            originLatLong.setLongitude(place.getLatLng().longitude);
            if (!isDestFieldEmpty()) {
                findTheDistance();
            }
            removeErrorRequired(R.id.origin_location);
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            //Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
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
        String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
        datePicker.setText(date);
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
                    addRideToFirebase();
                } else {
                    Toast.makeText(this, "Grant Location Permission to app", Toast.LENGTH_SHORT).show();
                }
        }

    }

    public void addRideToFirebase() {
        if (!isDestFieldEmpty() & !isOriginFieldEmpty() & !isDatePickerEmpty() & !isTimePickerEmpty()) {
            final String origin = originLocation.getText().toString();
            final String destination = destinationLocation.getText().toString();
            final String time = timePicker.getText().toString();
            final String date = datePicker.getText().toString();
            final String distance = maxDistancetv.getText().toString();

            if(typeOfRequest == 0){
                typeOfRequest = 1;
            }

            // Disable button so there are no multi-posts
//            setEditingEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();


            //Location call starts
            ApiInterface locationClient =
                    LocationClient.getClient().create(ApiInterface.class);

            Call<LocationResponse> call = locationClient.getLocation(Config.GOOGLE_API_KEY);
            Log.d(TAG, "submitRide: ");
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    try {
                        Toast.makeText(AddRideActivity.this, "Response", Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "onResponse: + inside1" + response.body());
                        LocationResponse lr = response.body();
                        lat = lr.getLocation().getLat();
                        longitude = lr.getLocation().getLng();

                        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        Log.d(TAG, "onResponse: inside2");
                        try {
                            if (lat != null & longitude != null) {
                                List<Address> addresses = geoCoder.getFromLocation(lat, longitude, 1);
                                Log.d(TAG, "onResponse: + inside try 3");

                                if (addresses.size() > 0) {
                                    location = addresses.get(0).getLocality();

                                    // [START single_value_read]
                                    final String userId = getUid();
                                    mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // Get user value
                                                    User user = dataSnapshot.getValue(User.class);

                                                    // [START_EXCLUDE]
                                                    if (user == null) {
                                                        // User is null, error out
                                                        Log.e(TAG, "User " + userId + " is unexpectedly null");
                                                        Toast.makeText(AddRideActivity.this,
                                                                "Error: could not fetch user.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Write new post
                                                        // Body is required
                                                        String title = concatenateOriginAndDest();
                                                        try {
                                                            String capTitle = capitalize(title);
                                                            writeNewRide(userId, user.username, capTitle, origin, destination, typeOfRequest, distance, date, time, location);
                                                            Toast.makeText(AddRideActivity.this, "I am successfully Submitted", Toast.LENGTH_SHORT).show();
                                                        } catch (Exception e) {
                                                            Toast.makeText(AddRideActivity.this, "I am toasted", Toast.LENGTH_SHORT).show();
                                                            writeNewRide(userId, user.username, title, origin, destination, typeOfRequest, distance, date, time, location);
                                                        }

                                                    }

                                                    // Finish this Activity, back to the stream
                                                    setEditingEnabled(true);
                                                    finish();
                                                    // [END_EXCLUDE]
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                                    // [START_EXCLUDE]
                                                    setEditingEnabled(true);
                                                    // [END_EXCLUDE]
                                                }
                                            });
                                    // [END single_value_read]
                                } else {

                                }
                            } else {
                                System.out.println("Reverse geocoding failed on lat, long null values");
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse: " + e.toString());

                    }
                }

                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    System.out.println("response" + t);
                }
            });
        } else {
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
    }

    private void writeNewRide(String userId, String username, String capTitle, String origin, String destination, int typeOfRequest, String distance, String date, String time, String location) {
        String key = mDatabase.child("rides").push().getKey();

        RideRequest rideRequest = new RideRequest(userId, username, capTitle, origin, destination, typeOfRequest, distance, date, time, location);
        Map<String, Object> rideValues = rideRequest.toMapWithoutimageEncoded();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/rides/" + key, rideValues);
        childUpdates.put("/user-rides/" + userId + "/" + key, rideValues);
        mDatabase.updateChildren(childUpdates);

    }

    private String concatenateOriginAndDest() {
        String str1 = originLocation.getText().toString() + " to";
        String str2 = destinationLocation.getText().toString();
        String result = str1.concat(str2);

        return result;
    }

    private void setEditingEnabled(boolean enabled) {
        originLocation.setEnabled(enabled);
        destinationLocation.setEnabled(enabled);
        if (enabled) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
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
        dpd.show(getFragmentManager(), "Choose Date:");
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

}
