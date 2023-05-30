package com.example.lostandfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private AdvertDbHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private CheckBox isLostCheckBox;
    private CheckBox isFoundCheckBox;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private Button saveButton;
    private Button getCurrentLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        dbHelper = new AdvertDbHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        isLostCheckBox = findViewById(R.id.isLostCheckBox);
        isFoundCheckBox = findViewById(R.id.isFoundCheckBox);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        saveButton = findViewById(R.id.saveButton);
        getCurrentLocationButton = findViewById(R.id.getCurrentLocationButton);

        Places.initialize(getApplicationContext(), "AIzaSyCpAX_odlXTiTVW053-e92V6i-YMUSr418");

        locationEditText.setFocusable(false);
        locationEditText.setClickable(true);
        locationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(CreateAdvertActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    getCurrentLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(AdvertDbHelper.COLUMN_IS_LOST, isLostCheckBox.isChecked() ? 1 : 0);
                values.put(AdvertDbHelper.COLUMN_IS_FOUND, isFoundCheckBox.isChecked() ? 1 : 0);
                values.put(AdvertDbHelper.COLUMN_NAME, nameEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_PHONE, phoneEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_DESCRIPTION, descriptionEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_LOCATION, locationEditText.getText().toString());

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String date = sdf.format(new Date());
                values.put(AdvertDbHelper.COLUMN_DATE, date);

                db.insert(AdvertDbHelper.TABLE_NAME, null, values);

                Toast.makeText(CreateAdvertActivity.this, "Advert created successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    private boolean checkLocationPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String address = getAddressFromLocation(latitude, longitude);
                            locationEditText.setText(address);
                        } else {
                            Toast.makeText(CreateAdvertActivity.this, "Failed to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        sb.append(", ");
                    }
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = place.getAddress();
                locationEditText.setText(address);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle error
                Toast.makeText(CreateAdvertActivity.this, "Error: " + Autocomplete.getStatusFromIntent(data).getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(CreateAdvertActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
