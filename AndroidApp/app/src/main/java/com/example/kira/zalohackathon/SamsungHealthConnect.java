package com.example.kira.zalohackathon;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kira.zalohackathon.database.RealmController;
import com.example.kira.zalohackathon.database.entity.HeartRate;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.github.introml.activityrecognition.R;

public class SamsungHealthConnect extends AppCompatActivity {

    public static final String APP_TAG = "ZaloHackathon";

    private static SamsungHealthConnect mInstance = null;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private Set<HealthPermissionManager.PermissionKey> mKeySet;
     int count = 0;
     long end_time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samsung_health_connect);

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
    }

    @Override
    public void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {
                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    pmsManager.requestPermissions(mKeySet, SamsungHealthConnect.this).setResultListener(mPermissionListener);
                } else {
                    getHeartRateFromSHealth();
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
                        Toast.makeText(SamsungHealthConnect.this, "Failed to get permission", Toast.LENGTH_SHORT).show();
                    } else {
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                getHeartRateFromSHealth();
                            }
                        };
                       Timer timer = new Timer();
                       timer.scheduleAtFixedRate(timerTask, 1000, 5000);
                    }
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
//            Log.d("Count",String.valueOf(count));
        } catch (Exception e) {
            Log.d("ErrorTEST", e.getMessage());
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListener = new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
        @Override
        public void onResult(HealthDataResolver.ReadResult healthData) {
            count = 0;
            end_time = 0;
            try {
                for (HealthData data : healthData) {
                    count = data.getInt(HealthConstants.HeartRate.HEART_RATE);
                    end_time = data.getLong(HealthConstants.HeartRate.END_TIME);
                }


                if ( System.currentTimeMillis() - end_time <= 10000){
                    HeartRate heartRate = new HeartRate("1", count ,new Date(end_time), null ,"1" );
                    RealmController realm = new RealmController(getApplication());
                    realm.update(heartRate);
                    //TODO: Kien pls put your code here
                    //
                }

            } finally {
                healthData.close();
            }


        }

    };

}

