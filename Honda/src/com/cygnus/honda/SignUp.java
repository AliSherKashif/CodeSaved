package com.cygnus.honda;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.widget.Toast;

import com.cygnus.honda.R;
import com.cygnus.honda.utilities.ConnectionDetector;
import com.cygnus.honda.utilities.JSONParser;
import com.cygnus.honda.utilities.UtilitiesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SignUp extends Activity {

	Context context;
	// ////////////GCM part ends here
	EditText companyName, name, email, password, phone;
	String nameVal, emailVal, passwordVal, phoneVal;
	Button signUp;

	AlertDialog levelDialog;

	private static String url;
	UtilitiesActivity utilityObj;

	String urlParameter;

	SharedPreferences.Editor editor;
	SharedPreferences prefs;

	String device_Id;
	// TextView nameTV;
	RelativeLayout mainRRBG, sub_mainRRBG;

	TextView mainTVTitlebar;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_activity);

		// nameTV = (TextView) findViewById(R.id.Name_TV);
		mainRRBG = (RelativeLayout) findViewById(R.id.main_rr_bg);
		sub_mainRRBG = (RelativeLayout) findViewById(R.id.sub_main_rr_bg);
		mainTVTitlebar = (TextView) findViewById(R.id.main_tv_titlebar);

		// define the sharedpreferences setter and getter
		editor = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE)
				.edit();
		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
		signUp = (Button) findViewById(R.id.signup_btn);

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
			device_Id = "nothing";

		}

		// //// GCM part
		context = getApplicationContext();

		// to validate email and phone number
		utilityObj = new UtilitiesActivity();

		name = (EditText) findViewById(R.id.Name_ET);
		email = (EditText) findViewById(R.id.Email_ET);
		password = (EditText) findViewById(R.id.password_ET);
		phone = (EditText) findViewById(R.id.phone_ET);
		// vehicleModel = (EditText) findViewById(R.id.vehicleModel_ET);

		// ////// GCM ends here

		

		email.addTextChangedListener(new TextWatcher() {

			Resources resources = SignUp.this.getResources();

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

					if (utilityObj.isValidEmailAddress(s.toString())) {

						signUp.setEnabled(true);

						// Drawable drawable = resources
						// .getDrawable(R.drawable.button_bg);
						// signUp.setBackgroundDrawable(drawable);
						// signUp.setTextColor(resources.getColor(R.color.white));

					} else {
						signUp.setEnabled(false);

						// Drawable drawable = resources
						// .getDrawable(R.drawable.login_button_bg);
						// signUp.setBackgroundDrawable(drawable);
						// signUp.setTextColor(resources.getColor(R.color.black));

					}

				}

			}
		});

		// sign up button click listener to create an account of driver/user
		signUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SignUpFunc();

			}
		});

	}

	private void OrientationPortrait() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			signUp.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_up_bg_dayp));

		} else {

			signUp.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_up_bg_nightp));

		}

		RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) sub_mainRRBG
				.getLayoutParams();
		relativeParams.leftMargin = 0;
		relativeParams.rightMargin = 0;
		sub_mainRRBG.setLayoutParams(relativeParams);

		RelativeLayout.LayoutParams relativeParams1 = (RelativeLayout.LayoutParams) mainTVTitlebar
				.getLayoutParams();
		relativeParams1.height = 180;
		mainTVTitlebar.setLayoutParams(relativeParams1);

	}

	private void OrientationLandScape() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			signUp.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_day));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_up_bg_dayl));

		} else {

			signUp.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button_bg_night));
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.sign_up_bg_nightl));

		}

		RelativeLayout.LayoutParams relativeParams1 = (RelativeLayout.LayoutParams) sub_mainRRBG
				.getLayoutParams();
		// relativeParams1.setMargins(90, 85, 90, 0); // left, top, right,
		// bottom
		relativeParams1.leftMargin = 90;
		relativeParams1.rightMargin = 90;
		sub_mainRRBG.setLayoutParams(relativeParams1);

		RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mainTVTitlebar
				.getLayoutParams();
		relativeParams.height = 85;
		mainTVTitlebar.setLayoutParams(relativeParams);

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	protected void SignUpFunc() {
		// TODO Auto-generated method stub

		ConnectionDetector detector = new ConnectionDetector(
				getApplicationContext());

		nameVal = name.getText().toString();
		emailVal = email.getText().toString();
		passwordVal = password.getText().toString();
		phoneVal = phone.getText().toString();

		if (utilityObj.isValidMobile(phoneVal)) {
		} else {
			Toast.makeText(getApplicationContext(),
					"Please check your phone number", Toast.LENGTH_SHORT)
					.show();
			return;

		}

		if (nameVal.matches("")) {

			Toast.makeText(getApplicationContext(), "Please enter name",
					Toast.LENGTH_SHORT).show();
			return;

		}

		if (emailVal.matches("")) {

			Toast.makeText(getApplicationContext(), "Please enter email",
					Toast.LENGTH_SHORT).show();
			return;

		}

		if (passwordVal.matches("")) {

			Toast.makeText(getApplicationContext(), "Please enter password",
					Toast.LENGTH_SHORT).show();
			return;

		}

		if (phoneVal.matches("")) {

			Toast.makeText(getApplicationContext(),
					"Please enter phone number", Toast.LENGTH_SHORT).show();
			return;

		} else {

			try {
				if (detector.isConnectingToInternet()) {

					nameVal = URLEncoder.encode(nameVal, "UTF-8");
					emailVal = URLEncoder.encode(emailVal, "UTF-8");
					passwordVal = URLEncoder.encode(passwordVal, "UTF-8");
					phoneVal = URLEncoder.encode(phoneVal, "UTF-8");

					url = "http://chatter.cygnussolution.com/ChatterWcf.svc/RegisterUser?ApplicationId="
							+ Welcome.APPLICATION_ID
							+ "&CompanyId="
							+ Welcome.COMPANY_ID
							+ "&UserName="
							+ emailVal
							+ "&Password="
							+ passwordVal
							+ "&Name="
							+ nameVal
							+ "&MobileNumber="
							+ phoneVal
							+ "&UserDeviceId="
							+ device_Id;

					new JSONParse().execute();
				} else {

					Toast.makeText(getApplicationContext(),
							"Check your internet connection", Toast.LENGTH_LONG)
							.show();

				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUp.this);
			pDialog.setMessage("Creating account...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
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
				// Wahetever you need

				String UserId;
				try {
					UserId = jsonObject.getString(Welcome.USER_ID);
					if (UserId != null && UserId.length() > 0) {
						// You will fall here only if there's a value...
						String userID = jsonObject.getString(Welcome.USER_ID);

						String success = jsonObject.getString(Welcome.SUCCESS);

						if (success.equals("true")) {
							editor.putString(Welcome.USER_ID, userID);
							editor.putString(Welcome.USER_JSON_OBJ,
									jsonObject.toString());
							editor.commit();

//							Intent mainIntent = new Intent(SignUp.this,
//									LoginActivity.class);
//							startActivity(mainIntent);
							finish();

						} else {
							Toast.makeText(getApplicationContext(),
									"User creation error", Toast.LENGTH_SHORT)
									.show();

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "exception",
							Toast.LENGTH_SHORT).show();
				}

			}

			pDialog.dismiss();
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

			OrientationPortrait();

		}
	}

}
