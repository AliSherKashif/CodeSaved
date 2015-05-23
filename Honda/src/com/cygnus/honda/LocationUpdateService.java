package com.cygnus.honda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.cygnus.honda.utilities.GPSTracker;
import com.cygnus.honda.utilities.JSONParser;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdateService extends Service {

	private static String url, latitude, longitude;
	// check if GPS enabled
	GPSTracker gpsTracker;
	// constant
	public static final long NOTIFY_INTERVAL = 120 * 1000; // 2 mint

	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	// timer handling
	private Timer mTimer = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		
		
		
		gpsTracker = new GPSTracker(this);

		if (gpsTracker.canGetLocation()) {
			latitude = String.valueOf(gpsTracker.latitude);
			longitude = String.valueOf(gpsTracker.longitude);

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			//gpsTracker.showSettingsAlert();
		}
		// cancel if already existed
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// recreate new
			mTimer = new Timer();
		}
		// schedule task
		mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0,
				NOTIFY_INTERVAL);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		// For time consuming an long tasks you can launch a new thread here...
		//Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
		mTimer.cancel();
		mTimer.purge();

	}

	class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {

					
					 latitude = String.valueOf(gpsTracker.latitude);
					 longitude = String.valueOf(gpsTracker.longitude);
					 url =
					 "http://chatter.cygnussolution.com/ChatterWcf.svc/SetUserLocation?UserId="
					 + ContactFragment.userID
					 + "&Latitude="
					 + latitude
					 + "&Longitude=" + longitude;
					 new JSONParse2().execute();

				}

			});
		}

		private String getDateTime() {
			// get date time in custom format
			SimpleDateFormat sdf = new SimpleDateFormat(
					"[yyyy/MM/dd - HH:mm:ss]");
			return sdf.format(new Date());
		}

	}

	private class JSONParse2 extends AsyncTask<String, String, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jParser = new JSONParser();
			// Getting JSON from URL
			JSONObject json = jParser.getJSONFromUrlPostJson(url);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			if (jsonObject != null) {
				
				// Toast.makeText(getApplicationContext(), "success",
				// Toast.LENGTH_SHORT).show();

			}

		}
	}

}
