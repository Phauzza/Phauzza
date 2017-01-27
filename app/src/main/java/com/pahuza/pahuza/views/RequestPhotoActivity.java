package com.pahuza.pahuza.views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pahuza.pahuza.R;
import com.pahuza.pahuza.models.CustomPlace;
import com.pahuza.pahuza.models.Job;

import java.util.UUID;

public class RequestPhotoActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = RequestPhotoActivity.class.getCanonicalName();
    private GoogleMap mMap;
    private Place searchPlace;
    private boolean isFirstTime = true;

    private PlaceAutocompleteFragment autocompleteFragment;
    private SupportMapFragment mapFragment;
    private RelativeLayout addDescription;
    private Button yesBtn;
    private Button noBtn;
    private EditText descTxt;

    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_photo);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        addDescription = (RelativeLayout) findViewById(R.id.add_description);
        yesBtn = (Button) findViewById(R.id.yes_btn);
        noBtn = (Button) findViewById(R.id.no_btn);
        descTxt = (EditText) findViewById(R.id.desc_txt);

        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (isFirstTime) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(point).title("Description: ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
                    mMap.animateCamera(zoom);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        CheckLocationAvailability();
    }

    @Override
    protected void onStart() {
        super.onStart();
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "lat: " + searchPlace.getLatLng().latitude + ", long: " + searchPlace.getLatLng().longitude);
                LatLng point = new LatLng(searchPlace.getLatLng().latitude, searchPlace.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(point).title("Description: " + descTxt.getText().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                addDescription.setVisibility(View.GONE);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                if (isFirstTime) {
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                    mMap.animateCamera(zoom);
                    isFirstTime = false;
                }

                CustomPlace cp = new CustomPlace(searchPlace.getAddress().toString(), searchPlace.getLatLng());
                String id = UUID.randomUUID().toString();
                Job job = new Job(id, FirebaseAuth.getInstance().getCurrentUser(), cp, descTxt.getText().toString(), "0");

                mDatabase.child("jobs").child(id).setValue(job);

            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDescription.setVisibility(View.GONE);
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                searchPlace = place;
                addDescription.setVisibility(View.VISIBLE);
                mMap.getUiSettings().setAllGesturesEnabled(false);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void CheckLocationAvailability() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(RequestPhotoActivity.this);
            dialog.setMessage("GPS not available");
            dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }
}
