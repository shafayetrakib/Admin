package com.itbd.examnierteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final String PREF_NAME = "ExaminerTeacher";
    ProgressBar progressBar;
    int progress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this, R.color.blue_pr));

        progressBar = (ProgressBar) findViewById(R.id.progessbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startWindow();
            }
        });
        thread.start();

    }

    public void doWork() {
        for (progress = 20; progress <= 100; progress++) {

            try {
                Thread.sleep(20);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void startWindow() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean userCheck = sharedPreferences.getBoolean("userCheck", false);

        if (userCheck){
            startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            finish();
        }
    }

}
