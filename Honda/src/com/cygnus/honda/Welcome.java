package com.cygnus.honda;

import java.io.IOException;
import java.util.Calendar;
import android.view.WindowManager;
import com.cygnus.honda.R;
import com.cygnus.honda.utilities.ConnectionDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Welcome extends Activity implements OnClickListener {

	ConnectionDetector detector;

	// ////////////// GCM part //////////////////
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started." Honda name at
	 * console google
	 */
	String SENDER_ID = "406739306702";
	static final String TAG = "GCM Demo";
	String regid;
	GoogleCloudMessaging gcm;
	Context context;
	SharedPreferences.Editor editor;
	SharedPreferences prefs;

	// //////////////////////////////////////////

	// Company Info

	public static final String APPLICATION_ID = "1";
	public static final String COMPANY_ID = "1";

	// ////// User Info
	public static final String SUCCESS = "Success";
	public static final String USER_ID = "UserId";
	public static final String USER_NAME = "UserName";
	public static final String NAME = "Name";
	public static final String USER_STATUS = "MoodStatus";
	public static final String EMAIL = "Email";
	public static final String PHONE = "MobileNumber";
	public static final String PASSWORD = "Password";
	public static final String CITY = "CityName";
	public static final String COUNTRY = "CountryName";
	public static final String USER_JSON_OBJ = "userobj";
	public static final String USER_STATUS_ICON = "userstatusicon";

	// ////// Buttons

	Button createAccount, login;
	RelativeLayout mainRRBG;

	CheckBox checkBoxMode;
	boolean appMode;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);

		mainRRBG = (RelativeLayout) findViewById(R.id.main_rr_bg);
		createAccount = (Button) findViewById(R.id.create_account_btn);
		login = (Button) findViewById(R.id.login_btn);

		// //// GCM part
		context = getApplicationContext();
		// define the sharedpreferences setter and getter
		editor = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE)
				.edit();
		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);

		// CheckBox object
		checkBoxMode = (CheckBox) findViewById(R.id.remember_checkBox_welcome);

		checkBoxMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (checkBoxMode.isChecked()) {
					appMode = true;
					editor.putBoolean("appmode", appMode);
					editor.commit();

					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						// Do some stuff
						OrientationLandScape();

					}

					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						// Do some stuff
						OrientationPortrait();
					}

				} else {

					appMode = false;
					editor.putBoolean("appmode", appMode);
					editor.commit();

					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						// Do some stuff
						OrientationLandScape();

					}

					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						// Do some stuff
						OrientationPortrait();
					}

				}

			}
		});

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Do some stuff
			OrientationLandScape();

		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Do some stuff
			OrientationPortrait();
		}

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.

		if (CheckConnection()) {

			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId(context);
				Log.i("Already Stored Device_ID", regid);
				Log.e("Already Stored Device_ID", regid);
				Log.d("Already Stored Device_ID", regid);

				if (regid.isEmpty()) {
					registerInBackground();
				}
			} else {
				Log.i(TAG, "No valid Google Play Services APK found.");
			}
		}

		createAccount.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	private void OrientationPortrait() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {

			checkBoxMode.setChecked(true);

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			createAccount.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));

			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.welcom_dayp));

			LinearLayout.LayoutParams relativeParams12 = (LinearLayout.LayoutParams) createAccount
					.getLayoutParams();

			relativeParams12.leftMargin = 20;
			relativeParams12.rightMargin = 20;

			createAccount.setLayoutParams(relativeParams12);

			LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) login
					.getLayoutParams();

			relativeParams.leftMargin = 20;
			relativeParams.rightMargin = 20;
			login.setLayoutParams(relativeParams);

		} else {

			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.welcom_nightp));

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			createAccount.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));

			LinearLayout.LayoutParams relativeParams12 = (LinearLayout.LayoutParams) createAccount
					.getLayoutParams();

			relativeParams12.leftMargin = 20;
			relativeParams12.rightMargin = 20;

			createAccount.setLayoutParams(relativeParams12);

			LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) login
					.getLayoutParams();

			relativeParams.leftMargin = 20;
			relativeParams.rightMargin = 20;
			login.setLayoutParams(relativeParams);

		}

	}

	private void OrientationLandScape() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			createAccount.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));

			checkBoxMode.setChecked(true);
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.welcom_dayl));

		} else {

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			createAccount.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));

			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.welcom_nightl));

		}

		LinearLayout.LayoutParams relativeParams12 = (LinearLayout.LayoutParams) createAccount
				.getLayoutParams();

		relativeParams12.leftMargin = 200;
		relativeParams12.rightMargin = 200;
		createAccount.setLayoutParams(relativeParams12);

		LinearLayout.LayoutParams relativeParams = (LinearLayout.LayoutParams) createAccount
				.getLayoutParams();

		relativeParams.leftMargin = 200;
		relativeParams.rightMargin = 200;
		login.setLayoutParams(relativeParams);

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Integer viewID = v.getId();
		switch (viewID) {
		case R.id.create_account_btn:
			Intent newAccountIntent = new Intent(Welcome.this, SignUp.class);
			startActivity(newAccountIntent);
			break;

		case R.id.login_btn:
			Intent loginIntent = new Intent(Welcome.this, LoginActivity.class);
			startActivity(loginIntent);
			finish();

			break;

		default:
			break;
		}

	}

	public boolean CheckConnection() {

		boolean check = false;

		detector = new ConnectionDetector(getApplicationContext());

		if (detector.isConnectingToInternet()) {
			check = true;

		} else {

			Toast.makeText(getApplicationContext(),
					"Check your internet connection", Toast.LENGTH_LONG).show();

		}
		return check;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (CheckConnection()) {
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId(context);
				Log.i("Already Stored Device_ID", regid);
				Log.e("Already Stored Device_ID", regid);
				Log.d("Already Stored Device_ID", regid);

				if (regid.isEmpty()) {
					registerInBackground();
				}
			} else {
				Log.i(TAG, "No valid Google Play Services APK found.");
			}

		}

	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void storeRegistrationId(Context context, String regId) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {

		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					Log.i("Received Device_ID", regid);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					// sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i("received Device_ID", regid);
				Log.e("received Device_ID", regid);
				Log.d("received Device_ID", regid);
				//
				// Toast.makeText(getApplicationContext(), msg,
				// Toast.LENGTH_SHORT)
				// .show();

			}
		}.execute(null, null, null);
	}

	// //////////////////////////////////////////

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen for landscape and portrait
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			OrientationLandScape();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			OrientationPortrait();
		}
	}

}
