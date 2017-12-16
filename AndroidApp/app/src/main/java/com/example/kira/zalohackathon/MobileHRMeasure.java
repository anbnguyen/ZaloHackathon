package com.example.kira.zalohackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.kira.zalohackathon.HeartRateMeasure.HeartRateMonitor;
import com.example.kira.zalohackathon.database.RealmController;
import com.example.kira.zalohackathon.database.entity.User;

import java.util.Date;

import io.github.introml.activityrecognition.R;
import io.realm.Realm;
import io.realm.RealmResults;

public class MobileHRMeasure extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Button MeasureButton;
    ImageButton setting,monitoring,aboutus,rating,info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobileheartratemeasure);
        Init();
        checkPermissions();


    }


    @Override
    public void onResume(){
        super.onResume();

    }
    private void Init() {
        MeasureButton = (Button) findViewById(R.id.button);

    }

   /* public void Measure(View view){
        Intent HRintent = new Intent(this, HeartRateMonitor.class);
        startActivity(HRintent);
    }*/
    public void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
