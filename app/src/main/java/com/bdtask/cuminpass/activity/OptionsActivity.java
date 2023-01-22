package com.bdtask.cuminpass.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.bdtask.cuminpass.Database.DatabaseHelper;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.utils.SharedPref;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OptionsActivity extends AppCompatActivity {

    TextView scanBtn,secretKeyBtn;
    DatabaseHelper databaseHelper;
    static final String DATEFORMAT = "yyyyMMddHHmm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Set up your Account");
        }

        SharedPref.init(OptionsActivity.this);
        databaseHelper   =  new DatabaseHelper(OptionsActivity.this);


        scanBtn        = findViewById(R.id.scanBtn);
        secretKeyBtn   = findViewById(R.id.secretKeyBtn);

        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        Log.w("testTime",utcTime);



        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity( new Intent(OptionsActivity.this, ScanActivity.class));
            }
        });

        secretKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(OptionsActivity.this, AddInfo.class));
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }




}