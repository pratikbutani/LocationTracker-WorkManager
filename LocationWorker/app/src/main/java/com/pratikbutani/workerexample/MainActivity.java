package com.pratikbutani.workerexample;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.pratikbutani.workerexample.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

	private static final int PERMISSION_REQUEST_CODE = 200;

	private static final String TAG = "LocationUpdate";

	ActivityMainBinding mainBinding;

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
			if (isWorkScheduled(WorkManager.getInstance().getWorkInfosByTag(TAG).get())) {
				mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_stop));
				mainBinding.message.setText(getString(R.string.message_worker_running));
				mainBinding.logs.setText(getString(R.string.log_for_running));
			} else {
				mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_start));
				mainBinding.message.setText(getString(R.string.message_worker_stopped));
				mainBinding.logs.setText(getString(R.string.log_for_stopped));
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mainBinding.appCompatButtonStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mainBinding.appCompatButtonStart.getText().toString().equalsIgnoreCase(getString(R.string.button_text_start))) {
					// START Worker
					PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
							.addTag(TAG)
							.build();
					WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

					Toast.makeText(MainActivity.this, "Location Worker Started : " + periodicWork.getId(), Toast.LENGTH_SHORT).show();

					mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_stop));
					mainBinding.message.setText(periodicWork.getId().toString());
					mainBinding.logs.setText(getString(R.string.log_for_running));
				} else {

					WorkManager.getInstance().cancelAllWorkByTag(TAG);

					mainBinding.appCompatButtonStart.setText(getString(R.string.button_text_start));
					mainBinding.message.setText(getString(R.string.message_worker_stopped));
					mainBinding.logs.setText(getString(R.string.log_for_stopped));
				}
			}
		});
	}

	private boolean isWorkScheduled(List<WorkInfo> workInfos) {
		boolean running = false;
		if (workInfos == null || workInfos.size() == 0) return false;
		for (WorkInfo workStatus : workInfos) {
			running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
		}
		return running;
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
