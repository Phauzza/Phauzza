package com.pahuza.pahuza.views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pahuza.pahuza.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class HelpFriendActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // Activity request codes
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap imageBitmap;
    private List<Marker> markers;
    private Marker selectedMarker;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private RelativeLayout orta;
    private TextView userTxt;
    private TextView descTxt;
    private TextView closeBtn;
    private Button takePhotoBtn;
    private Button reportBtn;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference mDatabase;
    private boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_friend);
        markers = new ArrayList<>();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        orta = (RelativeLayout) findViewById(R.id.orta);
        userTxt = (TextView) orta.findViewById(R.id.user_txt);
        descTxt = (TextView) orta.findViewById(R.id.desc_txt);
        closeBtn = (TextView) orta.findViewById(R.id.close_btn);
        takePhotoBtn = (Button) orta.findViewById(R.id.take_photo_btn);
        reportBtn = (Button) orta.findViewById(R.id.report_btn);

        reportBtn.setVisibility(View.GONE);

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
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                    mMap.animateCamera(zoom);

                    setAllPointsOnMap();
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
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orta.setVisibility(View.GONE);
            }
        });
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (imageBitmap != null) {
//                    selectedMarker.getTag();
//                    ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
//                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
//                    imageBitmap.recycle();
//                    byte[] byteArray = bYtE.toByteArray();
//                    String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("photo").setValue(encodedImage);
//                    mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("status").setValue("1");
//                    mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("userResponse").setValue(FirebaseAuth.getInstance().getCurrentUser());
//
//                    orta.setVisibility(View.GONE);
//                }
            }
        });
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (markers.contains(marker)) {
            //get firebase auth instance
            mDatabase.child("jobs").child(marker.getTag().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    userTxt.setText("User: " + snapshot.child("user").child("email").getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            descTxt.setText(marker.getTitle());
            orta.setVisibility(View.VISIBLE);
            selectedMarker = markers.get(markers.indexOf(marker));
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                selectedMarker.getTag();
                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                imageBitmap.recycle();
                byte[] byteArray = bYtE.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("photo").setValue(encodedImage);
                mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("status").setValue("1");
                mDatabase.child("jobs").child(selectedMarker.getTag().toString()).child("userResponse").setValue(FirebaseAuth.getInstance().getCurrentUser());

                orta.setVisibility(View.GONE);
            }
//            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private void setAllPointsOnMap() {
        Query myTopPostsQuery = mDatabase.child("jobs");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (!postSnapshot.child("status").getValue().toString().equals("1")) {
                        LatLng point = new LatLng(Double.valueOf(postSnapshot.child("place").child("latlng").child("latitude").getValue().toString()), Double.valueOf(postSnapshot.child("place").child("latlng").child("longitude").getValue().toString()));
                        MarkerOptions mo = new MarkerOptions().position(point).title(postSnapshot.child("description").getValue().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        Marker marker = mMap.addMarker(mo);
                        marker.setTag(postSnapshot.child("id").getValue().toString());
                        markers.add(marker);
                        isFirstTime = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(HelpFriendActivity.this);
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

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
