package com.bdtask.cuminpass.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bdtask.cuminpass.Database.DatabaseHelper;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.model.UserData;
import com.bdtask.cuminpass.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity {

    ZXingScannerView zXingScannerView;
    DatabaseHelper databaseHelper;
    ProgressBar progressBar;

    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("QR Scanner");
        }


        SharedPref.init(ScanActivity.this);
        databaseHelper = new DatabaseHelper(ScanActivity.this);
        zXingScannerView = findViewById(R.id.scanner_view);
        progressBar = findViewById(R.id.progressBar);

        // get intent value
        from = getIntent().getStringExtra("from");

        checkRunTimePermission();


    }


    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        zXingScannerView.stopCamera();
        super.onDestroy();

    }


    private void checkRunTimePermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            zXingScannerView.startCamera();
                            zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                                @Override
                                public void handleResult(Result rawResult) {

                                    String code = rawResult.getText();

                                    if (from == null) {
                                        if (!code.isEmpty() && code.length() > 20) {
                                            Log.wtf("code11", code.substring(0, 16));
                                            Log.wtf("code12", code.substring(16, 18));
                                            Log.wtf("code13", code.substring(18));
                                            Log.wtf("codefull", code);
                                            if (code.substring(16, 18).equals("==")) {
                                                long res = databaseHelper.insertUserData(code.substring(18), code.substring(0, 16));
                                                if (res != -1) {
                                                    startActivity(new Intent(ScanActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(ScanActivity.this, "failed to save info", Toast.LENGTH_SHORT).show();
                                                }
                                                zXingScannerView.stopCamera();
                                                zXingScannerView.setVisibility(View.GONE);
                                                startActivity(new Intent(ScanActivity.this, MainActivity.class));
                                                finish();

                                            } else {
                                                Toast.makeText(ScanActivity.this, "InValid Code", Toast.LENGTH_SHORT).show();
                                                zXingScannerView.resumeCameraPreview(this::handleResult);
                                            }


                                        } else {
                                            Toast.makeText(ScanActivity.this, "InValid Code", Toast.LENGTH_SHORT).show();
                                            zXingScannerView.resumeCameraPreview(this::handleResult);
                                        }
                                    } else {
                                        if (from.equals("import")) {
                                            String data = ApplicationClass.decryptMessage(code, getString(R.string.MESSAGE_CRYPT_KEY));
                                            List<UserData> accountData = new Gson().fromJson(data, new TypeToken<List<UserData>>() {
                                            }.getType());
                                            if (accountData.size() > 0) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                for (int i = 0; i < accountData.size(); i++) {
                                                    databaseHelper.insertUserData(accountData.get(i).getAccountName(), accountData.get(i).getAccountKey());
                                                }

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressBar.setVisibility(View.GONE);
                                                                startActivity(new Intent(ScanActivity.this, MainActivity.class));
                                                                finish();
                                                            }
                                                        });

                                                    }
                                                }, 3000);


                                            }else {
                                                Toast.makeText(ScanActivity.this, "no data found", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ScanActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    }


                                }
                            });

                        } else {
                            checkRunTimePermission();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();


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