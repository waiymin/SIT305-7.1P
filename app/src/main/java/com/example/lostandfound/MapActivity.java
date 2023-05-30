package com.example.lostandfound;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Add markers to the map
        LatLng deakinUniversity = new LatLng(-37.8479, 145.1155);
        googleMap.addMarker(new MarkerOptions().position(deakinUniversity).title("Deakin University"));

        LatLng boxHillCentral = new LatLng(-37.8197, 145.1251);
        googleMap.addMarker(new MarkerOptions().position(boxHillCentral).title("Box Hill Central"));

        LatLng melbourneCentral = new LatLng(-37.8102, 144.9629);
        googleMap.addMarker(new MarkerOptions().position(melbourneCentral).title("Melbourne Central"));

        // Set a default location and zoom level
        LatLng defaultLocation = new LatLng(-37.8479, 145.1155);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));
    }

}
