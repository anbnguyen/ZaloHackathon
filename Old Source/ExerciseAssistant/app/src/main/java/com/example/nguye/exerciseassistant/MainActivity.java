package com.example.nguye.exerciseassistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguye.exerciseassistant.ExerciseVideo.ExerciseVideoActivity;
import com.example.nguye.exerciseassistant.ExerciseVideo.Recent_Video;
import com.example.nguye.exerciseassistant.HeartRateMeasure.HeartRateMonitor;
import com.example.nguye.exerciseassistant.Login_Register.ConnectionClass;
import com.example.nguye.exerciseassistant.Login_Register.LoginActivity;
import com.example.nguye.exerciseassistant.Training.Pedometer;
import com.example.nguye.exerciseassistant.Training.Plank_Squat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int WALK_REQUEST = 10 ;
    private static final String STEP_COUNTER = "Step Counter Detected : ";
    private static final String DISTANCE = "Distance : ";
    private static final String CALORIES = "Calories : ";
    private static final String TIME = "Time : ";
    private static final String LAST_SENSORS_STEP ="lastSensorStep";
    private static final int PLANK_REQUEST = 11;
    private static final int SQUAT_REQUEST = 12;
    private static final int CYCLING_REQUEST = 13;
    private String step = null,initSteps = null;
    Spinner mySpinner;
    String text;
    Button trainbtn;

    @Override
    public void onResume(){
        super.onResume();
        SelecTop5Recent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkPermissions();
        SelecTop5Recent();
        saveFile();
        Toast.makeText(this,"Welcome "+ GlobalVariables.username + " !", Toast.LENGTH_LONG).show();
    }

    private void saveFile() {
        String filename = "Username";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(GlobalVariables.username.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void Init() throws IOException {
        trainbtn = (Button)findViewById(R.id.trainingbtn);
        mySpinner=(Spinner) findViewById(R.id.spinner);
        initSteps = loadFileStep();
    }

    private String loadFileStep() throws IOException {
        String yourFilePath = getApplicationContext().getFilesDir() + "/" + "Step";
        File yourFile = new File( yourFilePath );
        FileInputStream fis = getApplicationContext().openFileInput("Step");
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public void train(View view){
        text = mySpinner.getSelectedItem().toString();
        if(text.equals("Plank") ){
            Intent Plank = new Intent (this, Plank_Squat.class);
            Plank.putExtra("Request", PLANK_REQUEST);
            startActivityForResult(Plank,PLANK_REQUEST);
        }
        else if (text.equals("Squat")) {
            Intent Squat = new Intent(this, Plank_Squat.class);
            Squat.putExtra("Request", SQUAT_REQUEST);
            startActivityForResult(Squat, SQUAT_REQUEST);
        }
        else if(text.equals("Walk")){
            Intent Walk = new Intent (this, Pedometer.class);
            if(initSteps == null) {
                initSteps = "0";
            }
            Walk.putExtra(LAST_SENSORS_STEP, initSteps);
            startActivityForResult(Walk, WALK_REQUEST);
        }
    }
    public void video(View view) {
        Intent i = new Intent(this, ExerciseVideoActivity.class);
        startActivity(i);
    }
    public void recent(View view){
        Intent Recent = new Intent(this, Recent_Video.class);
        startActivity(Recent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == WALK_REQUEST) {
            if (resultCode == RESULT_OK) {
                savetoFile(data,WALK_REQUEST);
            }
        }
        else if (requestCode ==  SQUAT_REQUEST){
            if(resultCode ==  RESULT_OK){
                return;
            }
        }
        else if (requestCode ==  PLANK_REQUEST) {
            if (resultCode == RESULT_OK) {
                return;
            }
        }
        else if (requestCode ==  CYCLING_REQUEST) {
            if (resultCode == RESULT_OK) {
                return;
            }
        }
    }
    public void logout(View view){
        File dir = getFilesDir();
        File file = new File(dir, "Username");
        boolean deleted = file.delete();
        GlobalVariables.username = "";
        Intent toLogin = new Intent(this,LoginActivity.class);
        startActivity(toLogin);
    }
    private void savetoFile(Intent data, int RequestCode) {
        if(RequestCode == WALK_REQUEST) {
            step = data.getStringExtra(STEP_COUNTER);
            if (initSteps != "0")
                initSteps = String.valueOf(Integer.valueOf(step) + Integer.valueOf(data.getStringExtra(LAST_SENSORS_STEP)));
            else
                initSteps = step;
            String filename = "Step";
            File file = new File(getApplicationContext().getFilesDir(), filename);
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(Integer.parseInt(initSteps));
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        else if (RequestCode  == PLANK_REQUEST){
            return;
        }
        else if (RequestCode == SQUAT_REQUEST){
            return;
        }
        else if(RequestCode == CYCLING_REQUEST){
            return;
        }
    }
    private void SelecTop5Recent()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this,"Get Data...","loading Data, waiting....",true);
        final AsyncTask asyncLogin = new AsyncTask()
        {
            String output = "";
            Boolean isSuccess = false;
            Context context;
            ConnectionClass connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionClass();
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Object o)
            {
                super.onPostExecute(o);
                progressDialog.dismiss();

                //Close connection
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                    Log.e("ERROR: ", e.getMessage());
                }

                Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();

            }

            @Override
            protected Object doInBackground(Object[] params)
            {
                try
                {
                    if (con == null)
                    {
                        output = "Check Your Internet Access!";
                    }
                    else
                    {
                        // Change below query according to your own database.
                        String query = "SELECT TOP 5 videoID FROM ViewInfo\n" +
                                "WHERE username = '" + GlobalVariables.username + "' " + " \n" +
                                "ORDER by viewCount DESC;";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        GlobalVariables.RecentVideo.clear();
                        while(rs.next())
                        {
                            output = "CheckTop5";
                            String  input = rs.getString("videoID");
                            input = input.replace(" ", "");
                            GlobalVariables.RecentVideo.add(input);
                            //GlobalVariables.RecentVideo = (ArrayList<String>) rs.getArray(0);
                            isSuccess=true;
                        }
                        con.close();
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    output = ex.getMessage();
                }
                return output;
            }
        };

        asyncLogin.execute();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            //System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        int id= android.os.Process.myPid();
        android.os.Process.killProcess(id);
    }
}
