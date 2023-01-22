package com.bdtask.cuminpass.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.fragment.SecurityFragment;
import com.bdtask.cuminpass.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPref.init(SplashActivity.this);

        frameLayout        = findViewById(R.id.mainFrame);
        constraintLayout   = findViewById(R.id.mainConstraintLayout);


        if (SharedPref.read("appTheme","").isEmpty()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);

                    if (!SharedPref.read("pinNumber","").isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                constraintLayout.setVisibility(View.GONE);
                                frameLayout.setVisibility(View.VISIBLE);

                            }
                        });
                        loadFragment(new SecurityFragment(),"pinView");

                    }else {
//                        constraintLayout.setVisibility(View.GONE);
//                        frameLayout.setVisibility(View.VISIBLE);
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            }
        });

        thread.start();

    }

    public boolean loadFragment(Fragment fragment, String fragmentName) {
        try {
            if (fragment != null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.mainFrame,fragment,fragmentName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

                return true;
            }

        }catch (Exception e){
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}