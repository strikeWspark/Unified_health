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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;


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


        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    997,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    protected void onStart() {
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

    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i("100", "Range Start: " + dateFormat.format(startTime));
        Log.i("100", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        DataSet dataSet = dataReadResponse.getDataSet(DataType.TYPE_HEART_RATE_BPM);
                        dumpDataSet(dataSet);
                    }
                });

    }


    private void dumpDataSet(DataSet dataSet) {
        Log.i("100", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        //me idhar mean nikal raha he, incase ek se jyada values aye
        Integer mean=0;
        int size=dataSet.getDataPoints().size();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i("111", "Data point:");
            Log.i("111", "\tType: " + dp.getDataType().getName());
            Log.i("111", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("111", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            for (Field field : dp.getDataType().getFields()) {
                //yaha actual heartrate read ho raha he.
                double d = Double.parseDouble(dp.getValue(field).toString());
                int it=(int) d;
                mean=mean+ it;
                Log.i("111", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }
        mean= mean/size;

        mDatabaseReference.child("heartrate").setValue(mean.toString());
        Log.e("500",mean.toString());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 997) {
                accessGoogleFit();
            }
        }
    }

}