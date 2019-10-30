package com.pratikbutani.workerexample.alram;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pratikbutani.workerexample.R;
import com.pratikbutani.workerexample.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActvity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final String TAG = "LocationUpdate";

    ActivityMainBinding mainBinding;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mainBinding.toolbar);

        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        try {
           if (started==true) {
                mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_stop));
                mainBinding.message.setText(getString(R.string.message_worker_running));
                mainBinding.logs.setText(getString(R.string.log_for_running));

            } else {
                mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_start));
                mainBinding.message.setText(getString(R.string.message_worker_stopped));
                mainBinding.logs.setText(getString(R.string.log_for_stopped));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainBinding.appCompatButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainBinding.appCompatButtonStart.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
                    // START Worker
                    started=true;
                    /* Retrieve a PendingIntent that will perform a broadcast */
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.MINUTE, 1);

                    Intent alarmIntent = new Intent(MainActvity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(MainActvity.this, 0, alarmIntent, 0);
                    manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    int interval = 60000;// for 1 minute
                    // manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                     Log.d("printing pending intent", "--3-----"+pendingIntent);
                    try{
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                   // Toast.makeText(MainActvity.this, "Location Worker Started : " + periodicWork.getId(), Toast.LENGTH_SHORT).show();

                    mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_stop));
                    mainBinding.logs.setText(getString(R.string.log_for_running));
                } else {
                    started=false;
                    manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);

                    Toast.makeText(MainActvity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();

                    mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_start));
                    mainBinding.message.setText(getString(R.string.message_worker_stopped));
                    mainBinding.logs.setText(getString(R.string.log_for_stopped));
                }
            }
        });
    }



    /**
     * All about permission
     */
    private boolean checkLocationPermission() {
        int result3 = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        return result3 == PackageManager.PERMISSION_GRANTED &&
                result4 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean coarseLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (coarseLocation && fineLocation)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

