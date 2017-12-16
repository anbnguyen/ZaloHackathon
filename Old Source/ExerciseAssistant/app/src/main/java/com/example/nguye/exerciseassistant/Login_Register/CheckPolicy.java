package com.example.nguye.exerciseassistant.Login_Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.example.nguye.exerciseassistant.R;

public class CheckPolicy extends AppCompatActivity {
    CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

    }
    public void next(View view){
        cb = (CheckBox) findViewById(R.id.cb_ConfirmCondition);
        if(cb.isChecked()){
            Intent next = new Intent(this,Register.class);
            startActivity(next);
        }
    }
}
