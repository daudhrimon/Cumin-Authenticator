package com.bdtask.cuminpass.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bdtask.cuminpass.Database.DatabaseHelper;
import com.bdtask.cuminpass.R;

public class AddInfo extends AppCompatActivity {

    Button addButton;
    EditText accountName,accountKey;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add Account Info");
        }

        addButton      = findViewById(R.id.addAccount);
        accountName    = findViewById(R.id.accountName);
        accountKey     = findViewById(R.id.accountKey);
        databaseHelper = new DatabaseHelper(AddInfo.this);

//        accountName.setText(getIntent().getStringExtra("name"));
//        accountKey.setVisibility(View.GONE);
        if (getIntent().getExtras() != null){
            accountName.setText(getIntent().getStringExtra("name"));
            accountKey.setVisibility(View.GONE);
            addButton.setText("Update");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
            }
        });


    }

    private void saveInfo() {

        String accName = accountName.getText().toString();
        String accKey  = accountKey.getText().toString();

        if (accName.isEmpty()){
            accountName.setError("Account Name");
            return;
        }else if (accountKey.getVisibility()==View.VISIBLE && accKey.isEmpty()){
            accountKey.setError("Account key");
            return;
        }

        if (getIntent().getExtras() == null){
            long res = databaseHelper.insertUserData(accName,accKey);
            if (res != -1){
                startActivity(new Intent(AddInfo.this,MainActivity.class));
                finish();
            }else {
                Toast.makeText(this, "failed to save info", Toast.LENGTH_SHORT).show();
            }
        }else {

            int result = databaseHelper.updateData(getIntent().getIntExtra("id",0),accountName.getText().toString(),getIntent().getStringExtra("key"));
            startActivity(new Intent(AddInfo.this,MainActivity.class));
            finish();
        }
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