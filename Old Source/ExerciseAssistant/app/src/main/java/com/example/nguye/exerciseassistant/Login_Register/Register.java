package com.example.nguye.exerciseassistant.Login_Register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nguye.exerciseassistant.GlobalVariables;
import com.example.nguye.exerciseassistant.MainActivity;
import com.example.nguye.exerciseassistant.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class Register extends AppCompatActivity {

    Button btnRegister;
    EditText UsernameEt,PasswordEt,RepeatPasswordEt,EmailEt;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        UsernameEt = (EditText)findViewById(R.id.et_SignUpUserName);
        PasswordEt = (EditText)findViewById(R.id.et_SignUpPassword);
        RepeatPasswordEt = (EditText)findViewById(R.id.et_SignUpRePass);
        EmailEt = (EditText)findViewById(R.id.et_SignUpEmail);
        btnRegister = (Button)findViewById(R.id.btn_Register);
        btnRegister.setOnClickListener(onClickListener);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            username = UsernameEt.getText().toString();
            String password = PasswordEt.getText().toString();
            String RePass = RepeatPasswordEt.getText().toString();
            String Email = EmailEt.getText().toString();
            String type = "Register";
            boolean check = (Objects.equals(password, RePass));
            if (username.trim().equals(""))
            Toast.makeText(getApplicationContext(), "Please insert your Username", Toast.LENGTH_LONG).show();
        else if (password.trim().equals(""))
            Toast.makeText(getApplicationContext(), "Please insert your Password", Toast.LENGTH_LONG).show();
        else if (RePass.trim().equals(""))
            Toast.makeText(getApplicationContext(), "Please insert your Repeat Password", Toast.LENGTH_LONG).show();
        else if (!check)
            Toast.makeText(getApplicationContext(), "Your Password and RepeatPassword is not match", Toast.LENGTH_LONG).show();
        else if (Email.trim().equals(""))
            Toast.makeText(getApplicationContext(), "Please insert your Email", Toast.LENGTH_LONG).show();
        else
            register(username, password, Email);
        }
    };

    private void register(final String user,final String pass,final String Email) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Register....", "Trying to create, please wait...", true);
        final AsyncTask asyncLogin = new AsyncTask()
        {
            String result = "";
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

                Toast.makeText(Register.this,result, Toast.LENGTH_LONG).show();

            }

            @Override
            protected Object doInBackground(Object[] params)
            {
                if(user.trim().equals(""))
                    result = "Please insert your Username\n";
                else
                {
                    try
                    {
                        if (con == null)
                        {
                            result = "Check Your Internet Access!";
                        }
                        else
                        {
                            // Change below query according to your own database.
                            String query = "INSERT INTO accountM\n " +
                                    "(username,password,email)\n" +
                                    "VALUES\n" +
                                    "('"+user+"','"+pass+"','"+Email+"');" ;
                            Statement stmt = con.createStatement();
                            int rs = stmt.executeUpdate(query);
                            if(rs > 0)
                            {
                                result = "Register successful";
                                isSuccess=true;
                                con.close();
                                GlobalVariables.username =username;
                                Intent toMain = new Intent(getApplicationContext(),MainActivity.class);
                                toMain.putExtra("Username",username);
                                startActivity(toMain);
                            }
                            else
                            {
                                result = "Can't Create account";
                                isSuccess = false;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        isSuccess = false;
                        result = ex.getMessage();
                    }
                }
                return result;
            }
        };

        asyncLogin.execute();
    }
}
