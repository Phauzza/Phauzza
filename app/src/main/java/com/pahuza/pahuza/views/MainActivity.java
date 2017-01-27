package com.pahuza.pahuza.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pahuza.pahuza.R;
import com.pahuza.pahuza.services.LocationService;
import com.pahuza.pahuza.services.PhotoService;

public class MainActivity extends AppCompatActivity {

    private TextView userTxt;
    private Button reqPhoto;
    private Button helpFriend;
    private Button myJobs;
    private Button signout;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userTxt = (TextView) findViewById(R.id.user_txt);
        reqPhoto = (Button) findViewById(R.id.req_photo_btn);
        helpFriend = (Button) findViewById(R.id.help_friend_btn);
        myJobs = (Button) findViewById(R.id.my_jobs_btn);
        signout = (Button) findViewById(R.id.signout_btn);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        userTxt.setText("User: " + auth.getCurrentUser().getEmail());

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 8);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent lIntent = new Intent(this, LocationService.class);
        startService(lIntent);

        Intent pIntent = new Intent(this, PhotoService.class);
        startService(pIntent);

        reqPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestPhotoActivity.class);
                startActivity(intent);
            }
        });

        helpFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpFriendActivity.class);
                startActivity(intent);
            }
        });
        myJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyJobsActivity.class);
                startActivity(intent);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    //sign out method
    public void signOut() {
        auth.signOut();
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
