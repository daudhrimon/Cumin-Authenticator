package com.bdtask.cuminpass.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bdtask.cuminpass.Database.DatabaseHelper;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.adapter.DataListAdapter;
import com.bdtask.cuminpass.model.UserData;
import com.bdtask.cuminpass.utils.CustomDialog;
import com.bdtask.cuminpass.utils.NetworkStatus;
import com.bdtask.cuminpass.utils.SharedPref;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jsoup.Jsoup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomDialog.SingleChoiceListener{

    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;
    DataListAdapter listAdapter;
    List<UserData> allData;
    LinearLayout noData;
    FloatingActionButton floatingActionButton;
    private ActionMode mActionMode;
    String accName,accKey;
    int accId;
    CustomDialog customDialog ;
    String currentVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPref.init(MainActivity.this);
        if (SharedPref.read("appTheme","").isEmpty()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

       // Log.wtf("shareData",SharedPref.read("PinAction","00"));

        if (SharedPref.read("PinAction","").isEmpty()){
            Log.wtf("ovi",SharedPref.read("PinAction",""));
            showPinView("newFirst");
        }

        databaseHelper = new DatabaseHelper(MainActivity.this);

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (new NetworkStatus().checkNetworkConnection(MainActivity.this)) {
                //versionCheck(version);
                GetVersionCode getVersionCode = new GetVersionCode();
                getVersionCode.execute();
            } else {/**/}

        } catch (PackageManager.NameNotFoundException e) {/**/}



        floatingActionButton = findViewById(R.id.floatingBtn);
        noData = findViewById(R.id.noData);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);

        allData = new ArrayList<>();
        allData.addAll(databaseHelper.getAllUserData());

        if (allData.size() > 0) {
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            listAdapter = new DataListAdapter(MainActivity.this, allData);
            recyclerView.setAdapter(listAdapter);

        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
            }
        });


    }

    public void itemClick(int id, String accountName, String accountKey) {

        accId   = id;
        accName = accountName;
        accKey  = accountKey;


        if (mActionMode != null) {/**/}

        mActionMode = startSupportActionMode(mActionModeCallback);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu, menu);
            mode.setTitle("Choose your option");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    showDeleteItemDialog(mode);
                    //mode.finish();
                    return true;
                case R.id.edit:
                    Intent   intent = new Intent(MainActivity.this,AddInfo.class);
                    intent.putExtra("id",accId);
                    intent.putExtra("name",accName);
                    intent.putExtra("key",accKey);
                    startActivity(intent);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            allData.clear();
            allData = databaseHelper.getAllUserData();
            if (allData.size() > 0){
                listAdapter = new DataListAdapter(MainActivity.this,allData);
                recyclerView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
            }else {
                recyclerView.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }
        }
    };

    private void showDeleteItemDialog(ActionMode mode) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setTitle("Want to Delete ?");
        //builder.setMessage("this is a message");

        final AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                databaseHelper.deleteUserData(accId);

                allData.clear();
                allData = databaseHelper.getAllUserData();
                Log.wtf("dddddd", String.valueOf(allData.size()));
                if (allData.size() > 0){
                    listAdapter = new DataListAdapter(MainActivity.this,allData);
                    recyclerView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                }else {
                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }


                mode.finish();
                alertDialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                mode.finish();
            }
        });

        builder.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu,menu);

        if (SharedPref.read("pinNumber","").isEmpty()){
            MenuItem item = menu.findItem(R.id.changePin);
            item.setVisible(false);//
            MenuItem item2 = menu.findItem(R.id.deletePin);
            item2.setVisible(false);//
        }else {
            MenuItem item = menu.findItem(R.id.addPin);
            item.setVisible(false);//

        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.changeTheme){
            customDialog = new CustomDialog();
            customDialog.setCancelable(true);
            customDialog.show(getSupportFragmentManager(), "Single Choice Dialog");

        }else if (id == R.id.how){
            SharedPref.write("appTheme","");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else if (id == R.id.changePin){
            showPinView("old");
        }else if (id == R.id.addPin){
            showPinView("new");
        }else if (id == R.id.deletePin){
            showPinView("delete");
        }else if (id == R.id.transfer){
            startActivity(new Intent(MainActivity.this,TransferActivity.class));
        }



        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onPositiveButtonClicked(String[] list, int position) {

        if (position == 0){
            SharedPref.write("appTheme","yes");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            customDialog.dismiss();
        }else {
            SharedPref.write("appTheme","");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            customDialog.dismiss();
        }

    }

    @Override
    public void onNegativeButtonClicked() {

    }


    public class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (!onlineVersion.equals(currentVersion)) {
                    updateDialog();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);
                }
            }
        }
    }


    private void updateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setTitle("Update Available");
        builder.setMessage("Please update the latest version...");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                platStore();
            }
        });
        builder.show();

    }

    private void platStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.bdtask.gitpass"));
        intent.setPackage("com.android.vending");
        startActivity(intent);
    }


    private void showPinView(String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(customView);

        EditText oldPinEditText = customView.findViewById(R.id.oldPin);
        EditText newPinEditText = customView.findViewById(R.id.newPin);
        Button submit           = customView.findViewById(R.id.addPin);
        Button laterBtn         = customView.findViewById(R.id.later);

        SharedPref.write("PinAction","done");

        if (type.equals("new")){
            laterBtn.setVisibility(View.GONE);
            oldPinEditText.setVisibility(View.GONE);
            newPinEditText.setVisibility(View.VISIBLE);
        }else if (type.equals("old")){
            submit.setText("Change");
            oldPinEditText.setVisibility(View.VISIBLE);
            newPinEditText.setVisibility(View.VISIBLE);
            laterBtn.setVisibility(View.GONE);
        }else if (type.equals("delete")){
            submit.setText("Delete Pin");
            oldPinEditText.setVisibility(View.VISIBLE);
            newPinEditText.setVisibility(View.GONE);
            laterBtn.setVisibility(View.GONE);
        }else if (type.equals("newFirst")){
            oldPinEditText.setVisibility(View.GONE);
            newPinEditText.setVisibility(View.VISIBLE);
            laterBtn.setVisibility(View.VISIBLE);
        }

        AlertDialog alert = builder.create();
        alert.show();
        submit.setOnClickListener(view -> {

            if (type.equals("new")){

                oldPinEditText.setVisibility(View.GONE);
                newPinEditText.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(newPinEditText.getText().toString())) {

                    newPinEditText.requestFocus();

                } else {

                    alert.dismiss();
                    SharedPref.write("pinNumber",newPinEditText.getText().toString());
                    startActivity(new Intent(this,MainActivity.class));

                }

            }else if (type.equals("old")){

                oldPinEditText.setVisibility(View.VISIBLE);
                newPinEditText.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(newPinEditText.getText().toString()) || TextUtils.isEmpty(oldPinEditText.getText().toString())) {

                    newPinEditText.requestFocus();

                } else {

                    if (SharedPref.read("pinNumber","").equals(oldPinEditText.getText().toString())){

                        alert.dismiss();
                        SharedPref.write("pinNumber",newPinEditText.getText().toString());
                        Toast.makeText(this, "PIN number updated successfully", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(this, "Wrong Old pin Number", Toast.LENGTH_SHORT).show();
                    }

                }
            }else if (type.equals("delete")){

                oldPinEditText.setVisibility(View.VISIBLE);
                newPinEditText.setVisibility(View.GONE);

                if (TextUtils.isEmpty(oldPinEditText.getText().toString())) {
                    oldPinEditText.requestFocus();
                } else {

                    if (SharedPref.read("pinNumber","").equals(oldPinEditText.getText().toString())){
                        alert.dismiss();
                        SharedPref.write("pinNumber","");
                        Toast.makeText(this, "PIN number deleted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,MainActivity.class));

                    }else {
                        Toast.makeText(this, "Wrong Old pin Number", Toast.LENGTH_SHORT).show();
                    }

                }
            }else if (type.equals("newFirst")){
                oldPinEditText.setVisibility(View.GONE);
                newPinEditText.setVisibility(View.VISIBLE);
                laterBtn.setVisibility(View.VISIBLE);


                if (TextUtils.isEmpty(newPinEditText.getText().toString())) {

                    newPinEditText.requestFocus();

                } else {

                    alert.dismiss();
                    SharedPref.write("pinNumber",newPinEditText.getText().toString());
                    startActivity(new Intent(this,MainActivity.class));

                }
            }

        });

        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.write("PinAction","later");
                alert.dismiss();
            }
        });
    }

}