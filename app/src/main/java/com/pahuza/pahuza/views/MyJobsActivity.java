package com.pahuza.pahuza.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pahuza.pahuza.R;
import com.pahuza.pahuza.adapters.JobsAdapter;
import com.pahuza.pahuza.models.CustomPlace;
import com.pahuza.pahuza.models.Job;

import java.util.ArrayList;

public class MyJobsActivity extends AppCompatActivity {

    private ListView myJobsList;
    private ArrayList<Job> myJobs = new ArrayList<>();
    private JobsAdapter jobsAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        myJobsList = (ListView) findViewById(R.id.my_jobs_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query myTopPostsQuery = mDatabase.child("jobs");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    if (jobSnapshot.child("user").child("uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                        Job job = new Job();
                        job.setId(jobSnapshot.child("id").getValue().toString());
                        job.setDescription(jobSnapshot.child("description").getValue().toString());
                        job.setStatus(jobSnapshot.child("status").getValue().toString());
                        CustomPlace place = new CustomPlace();
                        place.setAddress(jobSnapshot.child("place").child("address").getValue().toString());
                        job.setPlace(place);
                        if (jobSnapshot.child("photo").getValue() != null)
                            job.setPhoto(jobSnapshot.child("photo").getValue().toString());

                        myJobs.add(job);
                        jobsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
        jobsAdapter = new JobsAdapter(myJobs, getApplicationContext());
        myJobsList.setAdapter(jobsAdapter);
        myJobsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creates an explicit intent for an Activity in your app
                if(myJobs.get(position).getPhoto() != null){
                    Intent resultIntent = new Intent(MyJobsActivity.this, ResultActivity.class);
                    resultIntent.putExtra("Description", myJobs.get(position).getDescription());
                    resultIntent.putExtra("BitmapImage", myJobs.get(position).getPhoto());
                    startActivity(resultIntent);
                }
            }
        });
    }
}
