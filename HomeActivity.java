package com.dryfire.unifiedhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private RelativeLayout infoLayout;
    private Toolbar home_toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private MaterialButton emergencycall;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private TextView heartrate, hemoglobin, bmi, name;
    private ImageView image;
    private StorageReference mStorageReference;
    private ProgressDialog homedialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homedialog = new ProgressDialog(this);

        home_toolbar = findViewById(R.id.uh_home_toolbar);
        setSupportActionBar(home_toolbar);

        image = findViewById(R.id.uh_image);
        name = findViewById(R.id.uh_home_name);
        heartrate = findViewById(R.id.actual_heartrate);
        hemoglobin = findViewById(R.id.actual_hemoglobin);
        bmi = findViewById(R.id.actual_BMI);
        emergencycall = findViewById(R.id.uh_emergency);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        SharedPreferences sp = getSharedPreferences("aadharNum", MODE_PRIVATE);


        String aadhar = sp.getString("aadhar", "null");
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(aadhar);
        mDatabaseReference.keepSynced(true);

        infoLayout = findViewById(R.id.relative_info_layout);

        /*if(mUser != null && mAuth != null){
            infoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this,InfoActivity.class));
                }
            });

        }*/

        emergencycall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:108"));
                startActivity(intent);
            }
        });

        /*GoogleSignInOptionsExtension fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ).build();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        Task<Void> response = Fitness.getSensorsClient(this, googleSignInAccount).add(
                new SensorRequest.Builder()
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setSamplingRate(1, TimeUnit.HOURS)
                .build(), myHeartRateListener);
        )*/


    }


        @Override
        public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.home_toolbar, menu);
            return super.onCreateOptionsMenu(menu);

        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.uh_signout:
                    Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onStart(){
            super.onStart();
            homedialog.setMessage("Loading Details...");
            homedialog.show();
            homedialog.setCancelable(false);
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Uified uified = dataSnapshot.getValue(Uified.class);
                    name.setText(uified.getName());
                    heartrate.setText(uified.getHeartrate() + " BPM");
                    hemoglobin.setText(uified.getHaemoglobin() + " Hb");
                    bmi.setText(uified.getBMI());
                    homedialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        /*
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Uified uified = dataSnapshot.getValue(Uified.class);
                heartrate.setText(uified.getHeartrate().toString() + " BPM");
                hemoglobin.setText(uified.getHaemoglobin().toString() + " Hb");
                /*if(uified.getBMI() > 22){
                    bmi.setText(uified.getBMI().toString() + " Over Weight");
                }else if(uified.getBMI() > 17 && uified.getBMI() < 22 ){
                    bmi.setText(uified.getBMI().toString() + " Normal");
                }else{
                    bmi.setText(uified.getBMI().toString() + " Under Weight");
                }*//*
                bmi.setText(uified.getBMI());

                String imageUrl = uified.getPhotourl();
                Picasso.get().load(imageUrl).into(image);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
        }
}

