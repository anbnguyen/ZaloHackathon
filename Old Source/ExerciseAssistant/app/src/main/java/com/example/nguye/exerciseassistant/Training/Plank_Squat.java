package com.example.nguye.exerciseassistant.Training;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguye.exerciseassistant.HeartRateMeasure.HeartRateMonitor;
import com.example.nguye.exerciseassistant.R;

public class Plank_Squat extends AppCompatActivity {
    private Chronometer MyChronometer;
    private int requestCode;
    Button startandStop;
    private static final int PLANK_REQUEST = 11;
    private static final int SQUAT_REQUEST = 12;
    double CaloriesConstant = 0;
    private int startCountClick = 0;
    private TextView caloCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plank__squat);
        Init();
        CaloriesCalculator(requestCode);
    }

    private void CaloriesCalculator(int requestCode) {
        if (requestCode == PLANK_REQUEST) {
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            imgView.setImageResource(R.drawable.icn_plank);
            CaloriesConstant = CaloriesBurnConstant.PLANK_CALO_BURN_CONSTANT;

        } else if (requestCode == SQUAT_REQUEST) {
            //ImageHandler();
            CaloriesConstant = CaloriesBurnConstant.SQUAT_CALO_BURN_CONSTANT;
        }
    }

    public void startWatch(View view) {
        if(startCountClick == 0) {
            MyChronometer.setBase(SystemClock.elapsedRealtime());
            MyChronometer.start();
            MyChronometer.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener(){

                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        // TODO Auto-generated method stub
                        long myElapsedSecond = (SystemClock.elapsedRealtime() - MyChronometer.getBase())/1000;
                        caloCal.setText(String.valueOf(roundDouble.roundTwoDecimals(myElapsedSecond*CaloriesConstant)));
                    }}
             );
            startCountClick += 1;
            startandStop.setBackgroundResource(R.drawable.btnstop);

        }
        else {
            putBackdataToFirstActivity();
            finish();
        }
    }
    public void heartRate(View view){
        Intent HRintent = new Intent(this, HeartRateMonitor.class);
        startActivity(HRintent);
    }
    private void putBackdataToFirstActivity() {
        return;
    }


    private void Init() {
        requestCode = getIntent().getExtras().getInt("Request");
        MyChronometer = (Chronometer)findViewById(R.id.chronometer2);
        startandStop = (Button)findViewById(R.id.startBtn);
        caloCal = (TextView)findViewById(R.id.caloCount);
    }
}
