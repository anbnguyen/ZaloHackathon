package com.example.nguye.exerciseassistant.Training;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.nguye.exerciseassistant.HeartRateMeasure.HeartRateMonitor;
import com.example.nguye.exerciseassistant.R;


public class Pedometer extends AppCompatActivity implements SensorEventListener  {
    private static final String STEP_COUNTER = "Step Counter Detected : ";
    private static final String DISTANCE = "Distance : ";
    private static final String CALORIES = "Calories : ";
    private static final String TIME = "Time : ";
    private static final String LAST_SENSORS_STEP ="lastSensorStep";

    private TextView stepCounter, distanceByStep, calories;

    private String step, distance, calo,initsteps;
    private Chronometer MyChronometer;

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        Init();
        startstopWatch();

    }

    private void startstopWatch() {
        MyChronometer.setBase(SystemClock.elapsedRealtime());
        MyChronometer.start();
    }
    public void heartRate(View view){
        Intent HRintent = new Intent(this, HeartRateMonitor.class);
        startActivity(HRintent);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;

        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            step = String.valueOf(value - Integer.valueOf(initsteps));
            distance = String.valueOf(roundDouble.roundTwoDecimals((value - Integer.valueOf(initsteps)) * 0.7143));
            calo = String.valueOf(roundDouble.roundTwoDecimals((value - Integer.valueOf(initsteps)) * CaloriesBurnConstant.WALK_CALO_BURN_CONSTANT));
            stepCounter.setText(step);
            distanceByStep.setText( distance + " m");
            calories.setText(calo + " cal");
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            stepCounter.setText("Step Detector Detected : " + (value - Integer.valueOf(initsteps)));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

    }
    public void saveData(View view){
        Intent returnIntent = new Intent();
        MyChronometer.stop();
        if(step != null) {
            returnIntent.putExtra(STEP_COUNTER, step);
            returnIntent.putExtra(DISTANCE, distance);
            returnIntent.putExtra(CALORIES, calo);
            returnIntent.putExtra(TIME, MyChronometer.getText().toString());
            returnIntent.putExtra(LAST_SENSORS_STEP, initsteps);
            setResult(Activity.RESULT_OK,returnIntent);
        }
        else
            setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void Init(){
        stepCounter = (TextView) findViewById(R.id.step_counter);
        distanceByStep = (TextView) findViewById(R.id.distance);
        calories = (TextView)findViewById(R.id.calories);
        MyChronometer = (Chronometer)findViewById(R.id.chronometer3) ;
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        initsteps = getIntent().getExtras().getString(LAST_SENSORS_STEP);
    }
}
