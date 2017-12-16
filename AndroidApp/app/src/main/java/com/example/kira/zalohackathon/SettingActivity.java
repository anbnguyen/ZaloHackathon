package com.example.kira.zalohackathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import io.github.introml.activityrecognition.R;

public class SettingActivity extends AppCompatActivity {

    ImageButton setting,monitoring,aboutus,rating,info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Init();
    }

    private void Init() {
        setting = (ImageButton) findViewById(R.id.btnsetting);
        monitoring = (ImageButton) findViewById(R.id.btnmonitoring);
        aboutus = (ImageButton) findViewById(R.id.btnaboutus);
        info = (ImageButton) findViewById(R.id.btninfo);
        rating = (ImageButton) findViewById(R.id.btnrating);
        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MonitoringActivity.class);
                startActivity(intent);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky is clicked

            }
        });
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky is clicked
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
