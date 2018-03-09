package bidyourride.kurama.com.bidyourride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.model.Category;
import bidyourride.kurama.com.bidyourride.view.CircleImageView;
import bidyourride.kurama.com.bidyourride.view.ClearableEditText;

/**
 * Created by madhukurapati on 3/7/18.
 */

public class AddRideActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener {
    private Spinner spinner;
    private EditText originLocation;
    private EditText destinationLocation;
    private AutocompleteFilter autocompleteFilter;
    private Intent intent;
    private CircleImageView originImage, destinationImage;

    static final int ORIGIN_AUTOCOMPLETE_REQUEST_CODE = 1;
    static final int DESTINATION_AUTOCOMPLETE_REQUEST_CODE = 2;

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

        originLocation = findViewById(R.id.origin_location);
        destinationLocation = findViewById(R.id.destination_location);
        originLocation.setOnFocusChangeListener(this);
        destinationLocation.setOnFocusChangeListener(this);

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

    private void setDataToSpinner() {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new Category(" ", " Request A Ride "));
        categoryArrayList.add(new Category(" ", " Post A Ride "));

        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryArrayList);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
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
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            //Status status = PlaceAutocomplete.getStatus(this, data);
            //TODO: Handle the error.
        } else if (resultCode == RESULT_CANCELED) {

        }
    }

    private void checkIfOriginResultIsOk(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            originLocation.setText(place.getName());
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
}

