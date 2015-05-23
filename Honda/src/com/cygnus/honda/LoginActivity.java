package com.cygnus.honda;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cygnus.honda.R;
import com.cygnus.honda.utilities.ConnectionDetector;
import com.cygnus.honda.utilities.GPSTracker;
import com.cygnus.honda.utilities.JSONParser;
import com.cygnus.honda.utilities.UtilitiesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends Activity {
	
	
	String SENDER_ID = "406739306702";
	static final String TAG = "GCM Demo";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	ConnectionDetector detector;
	GoogleCloudMessaging gcm;

	public static String CHECK_CONTACT_GROUP = "3";
	public static String CHECK_TAB_SELECTED = "3";
	
	public static String CHECK_REGISTER_BROADCAST_RECEIVER= "1";

	public static boolean checkNotifyCountReceive = true;
	public static boolean check_contacts = true, check_recent = false,
			check_favorite = false, check_call_contacts = true;

	public static final String MY_PREFS_NAME = "MyPrefs";
	public static  SharedPreferences.Editor editor;
	private ProgressDialog pDialog;

	private Context context;
	private static String url, latitude, longitude;

	String urlParameter;

	SharedPreferences prefs;
	EditText email, password;
	TextView forgotPassword;
	Button login;

	CheckBox checkBox;

	
	boolean rememberCredentials;

	String emailVAl, passwordVal;
	// check if GPS enabled
	GPSTracker gpsTracker;

	String device_Id;
	RelativeLayout mainRRBG, subMainRR;
	TextView mainTVTitlebar;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		mainRRBG = (RelativeLayout) findViewById(R.id.main_rr_bg);
		subMainRR = (RelativeLayout) findViewById(R.id.sub_main_rr);
		mainTVTitlebar = (TextView) findViewById(R.id.main_tv_titlebar);
		// EditText objects
		email = (EditText) findViewById(R.id.Email_ET);
		password = (EditText) findViewById(R.id.Password_ET);
		prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

		String success = prefs.getString(Welcome.SUCCESS, "");
		if (success.equals("")) {

		} else {
			
			LoginActivity.CHECK_REGISTER_BROADCAST_RECEIVER = "1";
			Intent mainIntent = new Intent(LoginActivity.this, MainMenu.class);
			startActivity(mainIntent);
			finish();
			

		}

		// buttons objects

		forgotPassword = (TextView) findViewById(R.id.forgotPassword_TV);
		login = (Button) findViewById(R.id.Login_btn);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

			// Do some stuff

			OrientationLandScape();
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Do some stuff
			OrientationPortrait();
		}

		device_Id = prefs.getString(Welcome.PROPERTY_REG_ID, "");
		if (device_Id.isEmpty()) {
			
			
			if (CheckConnection()) {

				if (checkPlayServices()) {
					gcm = GoogleCloudMessaging.getInstance(this);
					device_Id = getRegistrationId(context);
					Log.i("Already Stored Device_ID", device_Id);
					Log.e("Already Stored Device_ID", device_Id);
					Log.d("Already Stored Device_ID", device_Id);

					if (device_Id.isEmpty()) {
						registerInBackground();
					}
				} else {
					Log.i(TAG, "No valid Google Play Services APK found.");
				}
			}
			
			

		}

		gpsTracker = new GPSTracker(this);

		if (gpsTracker.canGetLocation()) {
			latitude = String.valueOf(gpsTracker.latitude);
			longitude = String.valueOf(gpsTracker.longitude);

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gpsTracker.showSettingsAlert();
		}

		//

		email.addTextChangedListener(new TextWatcher() {

			Resources resources = LoginActivity.this.getResources();

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() != 0) {

					UtilitiesActivity utilityObj = new UtilitiesActivity();
					if (utilityObj.isValidEmailAddress(s.toString())) {

						login.setEnabled(true);

						// Drawable drawable = resources
						// .getDrawable(R.drawable.button_bg);
						// login.setBackgroundDrawable(drawable);
						// login.setTextColor(resources
						// .getColor(R.color.blue_dark));

					} else {
						login.setEnabled(false);

						// Drawable drawable = resources
						// .getDrawable(R.drawable.login_button_bg);
						// login.setBackgroundDrawable(drawable);
						// login.setTextColor(resources.getColor(R.color.white));

					}

				}

			}
		});

		// CheckBox object
		checkBox = (CheckBox) findViewById(R.id.remember_checkBox);
		if (RememberCheck()) {
			putCredentials();
		}

		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (checkBox.isChecked()) {
					rememberCredentials = true;
					editor.putBoolean("rememberCredentials",
							rememberCredentials);
					editor.commit();

				} else {

					rememberCredentials = false;
					editor.putBoolean("rememberCredentials",
							rememberCredentials);
					editor.commit();

				}

			}
		});

		forgotPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Forgot password clicked", Toast.LENGTH_SHORT).show();

			}
		});

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				emailVAl = email.getText().toString();
				passwordVal = password.getText().toString();

				loginFunction();

			}
		});
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

	private void OrientationPortrait() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_in_bg_dayp));

		} else {

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_in_bg_nightp));

		}

		RelativeLayout.LayoutParams relativeParams1 = (RelativeLayout.LayoutParams) subMainRR
				.getLayoutParams();

		relativeParams1.leftMargin = 0;
		relativeParams1.rightMargin = 0;
		subMainRR.setLayoutParams(relativeParams1);

		RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) login
				.getLayoutParams();
		buttonParams.height = 80;
		login.setLayoutParams(buttonParams);

		RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mainTVTitlebar
				.getLayoutParams();
		relativeParams.height = 165;
		mainTVTitlebar.setLayoutParams(relativeParams);

	}

	private void OrientationLandScape() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_in_bg_dayl));

		} else {

			login.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_in_bg_nightl));

		}

		RelativeLayout.LayoutParams relativeParams1 = (RelativeLayout.LayoutParams) subMainRR
				.getLayoutParams();
		// relativeParams1.setMargins(90, 40, 90, 0); // left, top, right,
		// bottom

		relativeParams1.leftMargin = 160;
		relativeParams1.rightMargin = 160;
		subMainRR.setLayoutParams(relativeParams1);

		RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) login
				.getLayoutParams();
		// buttonParams.setMargins(0, 8, 0, 0); // left, top, right,bottom

		buttonParams.height = 65;
		login.setLayoutParams(buttonParams);

		RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mainTVTitlebar
				.getLayoutParams();
		relativeParams.height = 50;
		mainTVTitlebar.setLayoutParams(relativeParams);

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	private void saveCredentialsVal(String email, String password) {
		// TODO Auto-generated method stub
		editor.putString("Email", email);
		editor.putString("Password", password);
		editor.commit();
	}

	private void putCredentials() {
		// TODO Auto-generated method stub

		email.setText(prefs.getString("Email", null));
		password.setText(prefs.getString("Password", null));
		checkBox.setChecked(true);

	}

	private boolean RememberCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("rememberCredentials", false);

	}

	protected void loginFunction() {
		// TODO Auto-generated method stub

		ConnectionDetector detector = new ConnectionDetector(
				getApplicationContext());

		if (emailVAl.matches("")) {

			Toast.makeText(getApplicationContext(), "Please enter email",
					Toast.LENGTH_SHORT).show();
			return;

		}

		if (passwordVal.matches("")) {

			Toast.makeText(getApplicationContext(), "Please enter password",
					Toast.LENGTH_SHORT).show();
			return;

		}

		else {

			if (detector.isConnectingToInternet()) {

				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AuthenticateUser?ApplicationId="
						+ Welcome.APPLICATION_ID
						+ "&CompanyId="
						+ Welcome.COMPANY_ID
						+ "&EmailAddress="
						+ emailVAl
						+ "&Password=" + passwordVal + "&DeviceId=" + device_Id;

				new JSONParse().execute();

			} else {

				Toast.makeText(getApplicationContext(),
						"Check your internet connection", Toast.LENGTH_LONG)
						.show();

			}

		}

	}

	private class JSONParse extends AsyncTask<String, String, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Logging...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jParser = new JSONParser();
			// Getting JSON from URL
			JSONObject json = jParser.getJSONFromUrlGetJson(url);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			if (jsonObject != null) {
				// Wahetever you need
				String UserId;
				try {
					UserId = jsonObject.getString(Welcome.USER_ID);
					if (UserId != null && UserId.length() > 0) {
						// You will fall here only if there's a value...

						String userID = jsonObject.getString(Welcome.USER_ID);
						String success = jsonObject.getString(Welcome.SUCCESS);

						if (success.equals("true")) {
							if (RememberCheck()) {
								saveCredentialsVal(emailVAl, passwordVal);
							}
							editor.putString(Welcome.USER_ID, userID);
							editor.putString(Welcome.USER_JSON_OBJ,
									jsonObject.toString());
							editor.putString("registration_id",
									jsonObject.getString("UserDeviceId"));
							editor.putInt(Welcome.USER_STATUS_ICON, 1);

							editor.putString(Welcome.SUCCESS, success);

							editor.commit();

							latitude = String.valueOf(gpsTracker.latitude);
							longitude = String.valueOf(gpsTracker.longitude);

							url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SetUserLocation?UserId="
									+ userID
									+ "&Latitude="
									+ latitude
									+ "&Longitude=" + longitude;
							new JSONParse2().execute();

						} else {
							Toast.makeText(getApplicationContext(),
									"Check your credentials exist",
									Toast.LENGTH_SHORT).show();
							pDialog.dismiss();

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "exception",
							Toast.LENGTH_SHORT).show();
					pDialog.dismiss();
				}

			}

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

				pDialog.dismiss();
				Intent mainIntent = new Intent(LoginActivity.this,
						MainMenu.class);
				startActivity(mainIntent);
				finish();

			}

		}
	}

	// ////////////////////// orientation of screens

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen for landscape and portrait
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			OrientationLandScape();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
			OrientationPortrait();
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
		editor.putString(Welcome.PROPERTY_REG_ID, regId);
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

		String registrationId = prefs.getString(Welcome.PROPERTY_REG_ID, "");
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
					device_Id = gcm.register(SENDER_ID);
					Log.i("Received Device_ID", device_Id);
					msg = "Device registered, registration ID=" + device_Id;

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
					storeRegistrationId(context, device_Id);
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
				Log.i("received Device_ID", device_Id);
				Log.e("received Device_ID", device_Id);
				Log.d("received Device_ID", device_Id);
				//
				// Toast.makeText(getApplicationContext(), msg,
				// Toast.LENGTH_SHORT)
				// .show();

			}
		}.execute(null, null, null);
	}

}
