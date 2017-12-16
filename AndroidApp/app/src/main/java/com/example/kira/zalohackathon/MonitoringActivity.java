package com.example.kira.zalohackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.kira.zalohackathon.HeartRateMeasure.HeartRateMonitor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.kira.zalohackathon.database.RealmController;
import com.example.kira.zalohackathon.database.entity.User;

import io.github.introml.activityrecognition.R;
import io.realm.RealmResults;

public class MonitoringActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Button MeasureButton;
    ImageButton setting,monitoring,aboutus,rating,info;
    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
//    private TextView downstairsTextView;
//
//    private TextView joggingTextView;
//    private TextView sittingTextView;
//    private TextView standingTextView;
//    private TextView upstairsTextView;
//    private TextView walkingTextView;
    private TextToSpeech textToSpeech;
    private float[] results;
    private TensorFlowClassifier classifier;
    RealmController realm;
    private String[] labels = {"Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        Init();
        checkPermissions();

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
//        testRealmDatabase();
//        downstairsTextView = (TextView) findViewById(R.id.downstairs_prob);
//        joggingTextView = (TextView) findViewById(R.id.jogging_prob);
//        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
//        standingTextView = (TextView) findViewById(R.id.standing_prob);
//        upstairsTextView = (TextView) findViewById(R.id.upstairs_prob);
//        walkingTextView = (TextView) findViewById(R.id.walking_prob);

        classifier = new TensorFlowClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
    }
    private void testRealmDatabase() {
        User user = new User("1","Duy",80.6,190.6,1994,"56465","6554646","Kien");
        realm = new RealmController(getApplication());
        realm.update(user);
        RealmResults<User> userRRs= realm.getByName("Duy");
        userRRs.get(0);
    }
    private void Init() {
        MeasureButton = (Button) findViewById(R.id.button);
        setting = (ImageButton) findViewById(R.id.btnsetting);
        monitoring = (ImageButton) findViewById(R.id.btnmonitoring);
        aboutus = (ImageButton) findViewById(R.id.btnaboutus);
        info = (ImageButton) findViewById(R.id.btninfo);
        rating = (ImageButton) findViewById(R.id.btnrating);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
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
        public void Measure(View view){
        Intent HRintent = new Intent(this, HeartRateMonitor.class);
        startActivity(HRintent);
    }

    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
            }
        }, 2000, 5000);
    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();
        x.add(event.values[0]);
        y.add(event.values[1]);
        z.add(event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {
            List<Float> data = new ArrayList<>();
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);

            results = classifier.predictProbabilities(toFloatArray(data));

//            downstairsTextView.setText(Float.toString(round(results[0], 2)));
//            joggingTextView.setText(Float.toString(round(results[1], 2)));
//            sittingTextView.setText(Float.toString(round(results[2], 2)));
//            standingTextView.setText(Float.toString(round(results[3], 2)));
//            upstairsTextView.setText(Float.toString(round(results[4], 2)));
//            walkingTextView.setText(Float.toString(round(results[5], 2)));

            x.clear();
            y.clear();
            z.clear();
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

}
