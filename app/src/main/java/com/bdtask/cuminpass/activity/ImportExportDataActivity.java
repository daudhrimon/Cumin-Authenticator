package com.bdtask.cuminpass.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bdtask.cuminpass.Database.DatabaseHelper;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.model.UserData;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ImportExportDataActivity extends AppCompatActivity {

    String from = "";
    List<UserData> allData;
    DatabaseHelper databaseHelper;
    QRGEncoder qrgEncoder;
    ImageView qrCodeIV;
    Button doneBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export_data);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Transfer Account");
        }

        databaseHelper = new DatabaseHelper(ImportExportDataActivity.this);
        from = getIntent().getStringExtra("from");

        // initialize id's
        qrCodeIV = findViewById(R.id.scanImage);
        doneBtn  = findViewById(R.id.doneBtn);


        if (from.equals("export")){
            getSupportActionBar().setTitle("Scan the QR code");
            allData = new ArrayList<>();
            allData.addAll(databaseHelper.getAllUserData());
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;
            qrgEncoder = new QRGEncoder(ApplicationClass.encryptMessage(new Gson().toJson(allData),getString(R.string.MESSAGE_CRYPT_KEY)), null, QRGContents.Type.TEXT, dimen);
            try {
                Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                qrCodeIV.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.e("Tag", e.toString());
            }
        }


        doneBtn.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
            finish();
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