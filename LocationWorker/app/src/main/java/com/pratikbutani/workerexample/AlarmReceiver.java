package com.pratikbutani.workerexample.alram;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private Context context;
	private String currentLocality = "";
	private String currentLocation = "";
		private Intent i;

	@Override
	public void onReceive(Context context, Intent intent) {
		// For our recurring task, we'll just display a message
		this.context = context;
		//Log.d("AlarmReceiver","OnReceive()================");
		this.i = intent;

		getImeiLocation();

	}

	public void getImeiLocation() {

		LocationManager locationManager = (LocationManager)
				context.getSystemService(Context.LOCATION_SERVICE);
try{
		LocationListener locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

} catch (SecurityException unlikely) {
	Log.e("", "Lost location permission." + unlikely);
}
	}


	/*---------- Listener class to get coordinates ------------- */
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			String longitude = "Longitude: " + loc.getLongitude();
			String latitude = "Latitude: " + loc.getLatitude();
			/*------- To get city name from coordinates -------- */
			String cityName = null;
			Geocoder gcd = new Geocoder(context, Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				if (addresses.size() > 0) {


					//Log.d("addresses", "----"+addresses);

					//System.out.println(addresses.get(0).getLocality());
					cityName = addresses.get(0).getLocality();
					currentLocality = cityName;
					try {
						currentLocality = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubLocality() + ", " + addresses.get(0).getLocality();

					} catch (Exception e) {

						e.printStackTrace();
					}
				}

				currentLocation = loc.getLatitude() + "," + loc.getLongitude();
				Log.d("current city", "printing"+currentLocality+";;;"+addresses.get(0).getAddressLine(0)+","+addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality());
				Log.d("currentLocation", "printing"+currentLocation);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}


}