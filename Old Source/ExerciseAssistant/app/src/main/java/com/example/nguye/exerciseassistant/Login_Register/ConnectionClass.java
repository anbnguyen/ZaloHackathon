package com.example.nguye.exerciseassistant.Login_Register;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass
{
    String server = "mssql5.gear.host";
    String database = "accountss";
    String user = "accountss";
    String password = "aN281196@";

    Connection connection = null;

    @SuppressLint("NewApi")
    public Connection connectionClass()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String ConnectionURL;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("Error: ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("Error: ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("Error: ", e.getMessage());
        }
        return connection;
    }
}
