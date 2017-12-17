package com.example.kira.zalohackathon;

import android.Manifest;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kira.zalohackathon.HeartRateMeasure.HeartRateMonitor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.example.kira.zalohackathon.database.RealmController;
import com.example.kira.zalohackathon.database.entity.HeartRate;
import com.example.kira.zalohackathon.database.entity.InvalidWarning;
import com.example.kira.zalohackathon.database.entity.User;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import io.github.introml.activityrecognition.R;
import io.realm.RealmResults;

public class MonitoringActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {
    private DataSingleton dataSingleton = DataSingleton.getInstance();
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Button MeasureButton;
    ImageButton setting,monitoring,aboutus,rating,info;
    ImageView imageView;

    public static final String APP_TAG = "ZaloHackathon";
    private static boolean isConnectedToSHealth = false;
    private static MonitoringActivity mInstance = null;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private Set<HealthPermissionManager.PermissionKey> mKeySet;
    int heartRateCount = 0;
    long end_time = 0;
    int current_activity;
    double alpha = 0.5;
    double default_threshold = 10;
    Integer[][] init_param;
    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    TextView textView13;
//    private TextView downstairsTextView;
//
//    private TextView joggingTextView;
//    private TextView sittingTextView;
//    private TextView standingTextView;
//    private TextView upstairsTextView;
//    private TextView walkingTextView;
    //private TextToSpeech textToSpeech;
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
        init_param = new Integer[][]{{120,110},{140,135},{75,70},{80,77},{130,128},{110,115}};
        mInstance = this;
        mKeySet = new HashSet<HealthPermissionManager.PermissionKey>();
        mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.HeartRate.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mStore = new HealthDataStore(this, mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();

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

        //textToSpeech = new TextToSpeech(this, this);
        //textToSpeech.setLanguage(Locale.US);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("HeartRate", String.valueOf(heartRateCount));
                                Log.d("EndTime", String.valueOf(end_time));
                                Log.d("NewDay", String.format("%s", System.currentTimeMillis()));
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
                                dataSingleton.setType(idx);
                                //textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
                                if(idx == 0)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_upstrairs, getApplicationContext().getTheme()));
                                if(idx == 1)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_jogging, getApplicationContext().getTheme()));
                                if(idx == 2)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_sitting, getApplicationContext().getTheme()));
                                if(idx == 3)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_standing, getApplicationContext().getTheme()));
                                if(idx == 4)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_upstrairs, getApplicationContext().getTheme()));
                                if(idx == 5)
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_walking, getApplicationContext().getTheme()));
                                if (isConnectedToSHealth) {
                                    getHeartRateFromSHealth();
//                    if ( System.currentTimeMillis() - end_time <= 10000){
                                    HeartRate heartRate = new HeartRate(1, MonitoringActivity.this.heartRateCount,new Date(end_time), null ,"1" );
//                                    realm.update(heartRate);
                                    Log.d("HeartRate", String.valueOf(heartRateCount));
//                        textView13.setText(String.format("Nhịp tim hiện tại:\\t\\t %s bpm", MonitoringActivity.this.heartRateCount));
                                    textView13.setText(String.format("Nhịp tim hiện tại:\\t\\t %s bpm", dataSingleton.getHeartRate()));
                                    //TODO: Kien pls put your code here
                                    // if bat thuong ->

                                    if(idx < 6 && idx>=0){
                                        double standard = alpha * init_param[idx][0] + (1-alpha) * init_param[idx][1];
                                        double threshold = Math.abs(heartRateCount - standard);
                                        if(threshold > default_threshold){
                                            Toast.makeText(MonitoringActivity.this,"Nhịp tim của bạn có vẻ bất ổn.",Toast.LENGTH_LONG).show();
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(getAppl.icationContext());
//                                            builder.setTitle("Cảnh báo")
//                                                    .setMessage("Nhịp tim của bạn đang tăng cao. Bạn có ổn không?")
//                                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            // continue with delete
//                                                            dialog.cancel();
//
//                                                        }
//                                                    })
//                                                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            // do nothing
//                                                            InvalidWarning invalidWarning = new InvalidWarning("1", current_activity, MonitoringActivity.this.heartRateCount);
//                                                            realm.update(invalidWarning);
//                                                        }
//                                                    })
//                                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                                    .show();
                                        }
                                    }
                                }
                            }
//            }
                        });
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("HeartRate", String.valueOf(heartRateCount));
//                Log.d("EndTime", String.valueOf(end_time));
//                Log.d("NewDay", String.format("%s", System.currentTimeMillis()));
//                if (results == null || results.length == 0) {
//                    return;
//                }
//                float max = -1;
//                int idx = -1;
//                for (int i = 0; i < results.length; i++) {
//                    if (results[i] > max) {
//                        idx = i;
//                        max = results[i];
//                    }
//                }
//                dataSingleton.setType(idx);
//                //textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
//                if(idx == 0)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_upstrairs, getApplicationContext().getTheme()));
//                if(idx == 1)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_jogging, getApplicationContext().getTheme()));
//                if(idx == 2)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_sitting, getApplicationContext().getTheme()));
//                if(idx == 3)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_standing, getApplicationContext().getTheme()));
//                if(idx == 4)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_upstrairs, getApplicationContext().getTheme()));
//                if(idx == 5)
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icn_walking, getApplicationContext().getTheme()));
//                if (isConnectedToSHealth) {
//                    getHeartRateFromSHealth();
////                    if ( System.currentTimeMillis() - end_time <= 10000){
//                    HeartRate heartRate = new HeartRate(1, MonitoringActivity.this.heartRateCount,new Date(end_time), null ,"1" );
//                    realm.update(heartRate);
//                    Log.d("HeartRate", String.valueOf(heartRateCount));
////                        textView13.setText(String.format("Nhịp tim hiện tại:\\t\\t %s bpm", MonitoringActivity.this.heartRateCount));
//                    textView13.setText(String.format("Nhịp tim hiện tại:\\t\\t %s bpm", dataSingleton.getHeartRate()));
//                    //TODO: Kien pls put your code here
//                    // if bat thuong ->
//
//                    if(idx < 6 && idx>=0){
//                        double standard = alpha * init_param[idx][0] + (1-alpha) * init_param[idx][1];
//                        double threshold = Math.abs(heartRateCount - standard);
//                        if(threshold > default_threshold){
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                            builder.setTitle("Cảnh báo")
//                                    .setMessage("Nhịp tim của bạn đang tăng cao. Bạn có ổn không?")
//                                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // continue with delete
//                                            dialog.cancel();
//
//                                        }
//                                    })
//                                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // do nothing
//                                            InvalidWarning invalidWarning = new InvalidWarning("1", current_activity, MonitoringActivity.this.heartRateCount);
//                                            realm.update(invalidWarning);
//                                        }
//                                    })
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        }
//                    }
//                }
//            }
////            }
//
//        }, 2000, 5000);
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

        imageView = (ImageView) findViewById(R.id.imageView);
        textView13 = (TextView) findViewById(R.id.textView13);
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
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {
                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    isConnectedToSHealth = false;
                    // Request the permission for reading step counts if it is not acquired
                    pmsManager.requestPermissions(mKeySet, MonitoringActivity.this).setResultListener(mPermissionListener);
                } else {
                    isConnectedToSHealth = true;
//                    getHeartRateFromSHealth();
//                    TimerTask timerTask = new TimerTask() {
//                        @Override
//                        public void run() {
//                            getHeartRateFromSHealth();
//                            Log.d("Count", String.valueOf(heartRateCount));
//                        }
//                    };
//                    Timer timer = new Timer();
//                    timer.scheduleAtFixedRate(timerTask, 1000, 5000);
////                    getHeartRateFromSHealth();
                }
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        }


        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(APP_TAG, "Health data service is not available.");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };
    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = error;
        String message = "Connection with Samsung Health is not available";

        if (mConnError.hasResolution()) {
            switch(error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install Samsung Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade Samsung Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable Samsung Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with Samsung Health policy";
                    break;
                default:
                    message = "Please make Samsung Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Log.d(APP_TAG, "Permission callback is received.");
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        Toast.makeText(MonitoringActivity.this, "Failed to get permission", Toast.LENGTH_SHORT).show();
                    } else {
                        isConnectedToSHealth = true;
                        };
                }

            };

    private void getHeartRateFromSHealth() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        // Get the current heart rate and display it
        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[]{HealthConstants.HeartRate.HEART_RATE,HealthConstants.HeartRate.END_TIME})
                .build();
        try {
            resolver.read(request).setResultListener(mListener);

        } catch (Exception e) {
            Log.d("ErrorTEST", e.getMessage());
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListener = new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
        @Override
        public void onResult(HealthDataResolver.ReadResult healthData) {
            heartRateCount = 0;
            end_time = 0;
            final RealmController realm = new RealmController(getApplication());
            try {
                for (HealthData data : healthData) {
                    heartRateCount = data.getInt(HealthConstants.HeartRate.HEART_RATE);
                    end_time = data.getLong(HealthConstants.HeartRate.END_TIME);

                }

            } finally {
                healthData.close();
            }
        }
    };
}
