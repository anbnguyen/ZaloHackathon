package com.example.nguye.exerciseassistant.Login_Register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nguye.exerciseassistant.MainActivity;
import com.example.nguye.exerciseassistant.R;
import com.example.nguye.exerciseassistant.GlobalVariables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {
    EditText UsernameEt,PasswordEt;
    Button btnLogin;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            GlobalVariables.username = loadUsernamefromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(GlobalVariables.username != "" && GlobalVariables.username != null){
            Intent toMain = new Intent(this,MainActivity.class);
            startActivity(toMain);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UsernameEt = (EditText)findViewById(R.id.etUserName);
        PasswordEt = (EditText)findViewById(R.id.etPassword);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onClickListener);
    }

    private String loadUsernamefromFile() throws IOException {
        String yourFilePath = getApplicationContext().getFilesDir() + "/" + "Username";
        File yourFile = new File( yourFilePath );
        FileInputStream fis = getApplicationContext().openFileInput("Username");
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                username = UsernameEt.getText().toString();
                String password = PasswordEt.getText().toString();
                String type = "Login";

        login(username, password);

        }
    };

    private void login(final String user, final String pass)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Login....", "Logging in, please wait...", true);
        final AsyncTask asyncLogin = new AsyncTask()
        {
            String z = "";
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

                Toast.makeText(LoginActivity.this, z, Toast.LENGTH_SHORT).show();

            }

            @Override
            protected Object doInBackground(Object[] params)
            {
                if(user.trim().equals("")|| pass.trim().equals(""))
                    z = "Enter Username and Password";
                else
                {
                    try
                    {
                        if (con == null)
                        {
                            z = "Check Your Internet Access!";
                        }
                        else
                        {
                            // Change below query according to your own database.
                            String query = "select * from accountM where username = '" + user + "' and password = '"+ pass +"'  ";
                            Statement stmt = con.createStatement();
                            ResultSet rs = stmt.executeQuery(query);
                            if(rs.next())
                            {
                                z = "Login successful";
                                isSuccess=true;
                                con.close();
                                GlobalVariables.username =username;
                                //loadVideoID(username);
                                Intent toMain = new Intent(getApplicationContext(),MainActivity.class);
                                toMain.putExtra("Username",username);
                                startActivity(toMain);
                            }
                            else
                            {
                                z = "Invalid Credentials!";
                                isSuccess = false;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        isSuccess = false;
                        z = ex.getMessage();
                    }
                }
                return z;
            }
        };

        asyncLogin.execute();
    }

    public void OnSignUp(View view)
    {
        Intent next = new Intent(this,CheckPolicy.class);
        startActivity(next);
    }
}