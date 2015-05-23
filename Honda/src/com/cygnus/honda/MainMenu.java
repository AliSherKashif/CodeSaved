package com.cygnus.honda;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.cygnus.honda.R;
import com.cygnus.honda.database.DataLayer;
import com.cygnus.honda.utilities.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity implements OnClickListener {

	RelativeLayout titleBar, mainThemeBG;

	DataLayer dataLayerObj;
	Button viewEditbtn, createGroupButton, logoutBtn, backButton,
			hideShowButton;

	TextView backTV, editPicTV, editProfileTV, updateprofileTV, changeMoodTV;

	LinearLayout fragmentsLL, bottomBar, contacts_fragment_LL,
			message_fragment_LL;
	ScrollView profileRL;

	String dialogText;
	final Context context = this;

	boolean check_logout = true, check_update_mood = false,
			check_hide_show = true;

	AlertDialog optionDialog;
	private static String url;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	EditText userName, userEmail, userMood, userPhone, city, country;

	String user_Mood_String;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_activity);

		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
		editor = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE)
				.edit();

		dataLayerObj = new DataLayer(getApplicationContext());

		titleBar = (RelativeLayout) findViewById(R.id.logo);
		mainThemeBG = (RelativeLayout) findViewById(R.id.main_theme_bg);
		bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

		fragmentsLL = (LinearLayout) findViewById(R.id.fragments_LL);

		contacts_fragment_LL = (LinearLayout) findViewById(R.id.contact_fragment_LL);
		message_fragment_LL = (LinearLayout) findViewById(R.id.message_fragment_LL);

		profileRL = (ScrollView) findViewById(R.id.profile_SL);
		// //////////////// Text View Objects ////////////////////

		userName = (EditText) findViewById(R.id.user_name_ET);
		userEmail = (EditText) findViewById(R.id.email_ET);
		userMood = (EditText) findViewById(R.id.mood_ET);
		userPhone = (EditText) findViewById(R.id.phone_ET);
		city = (EditText) findViewById(R.id.city_ET);
		country = (EditText) findViewById(R.id.country_ET);

		fragmentsLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

		String user_jsonString = prefs.getString(Welcome.USER_JSON_OBJ, null);

		try {
			JSONObject jObj = new JSONObject(user_jsonString);
			String userID1 = jObj.getString(Welcome.USER_NAME);
			String userStatus1 = jObj.getString(Welcome.USER_STATUS);

			userName.setText(userID1);
			if (userStatus1.equals("null")) {
				userStatus1 = "";
			}
			userMood.setText(userStatus1);
			userEmail.setText(jObj.getString(Welcome.EMAIL));
			userPhone.setText(jObj.getString(Welcome.PHONE));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// here is button objects intialized
		viewEditbtn = (Button) findViewById(R.id.view_edit_button);
		createGroupButton = (Button) findViewById(R.id.create_group_button);
		logoutBtn = (Button) findViewById(R.id.log_out_button);
		backButton = (Button) findViewById(R.id.back_button);

		hideShowButton = (Button) findViewById(R.id.hide_show_button);
		// buttons click listener
		viewEditbtn.setOnClickListener(this);
		createGroupButton.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);
		backButton.setOnClickListener(this);
		hideShowButton.setOnClickListener(this);

		// ////////////// Profile screen textview objects
		editProfileTV = (TextView) findViewById(R.id.edit_TV);
		updateprofileTV = (TextView) findViewById(R.id.update_TV);
		editPicTV = (TextView) findViewById(R.id.change_dp);
		backTV = (TextView) findViewById(R.id.back_TV);
		changeMoodTV = (TextView) findViewById(R.id.change_mood_TV);

		editProfileTV.setOnClickListener(this);
		updateprofileTV.setOnClickListener(this);
		editPicTV.setOnClickListener(this);
		backTV.setOnClickListener(this);
		changeMoodTV.setOnClickListener(this);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Do some stuff
			OrientationLandScape();

		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Do some stuff
			OrientationPortrait();
		}

	}

	private void OrientationPortrait() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {

			// titleBar, mainThemeBG, bottm bar

			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));

			bottomBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));
			mainThemeBG.setBackgroundColor(getResources().getColor(
					R.color.white));

		} else {

			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));

			bottomBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));
			mainThemeBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.night_themep));

		}

	}

	private void OrientationLandScape() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));

			bottomBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));
			mainThemeBG.setBackgroundColor(getResources().getColor(
					R.color.white));

		} else {

			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));

			bottomBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));
			mainThemeBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.night_themel));

		}

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
		case R.id.view_edit_button:

			userName.setEnabled(false);
			userEmail.setEnabled(false);
			userMood.setEnabled(false);
			userPhone.setEnabled(false);
			city.setEnabled(false);
			country.setEnabled(false);
			backButton.setVisibility(View.INVISIBLE);

			fragmentsLL.setVisibility(View.INVISIBLE);
			profileRL.setVisibility(View.VISIBLE);
			String userId = prefs.getString(Welcome.USER_ID, "nothing");
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/GetUserProfile?UserId="
					+ userId;
			new LoadDPInfo().execute();

			break;

		case R.id.create_group_button:

			CreateGroupFragment fragobj = new CreateGroupFragment();

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.fragment_place, fragobj).commit();

			break;
		case R.id.log_out_button:

			LogOutFunction();

			break;

		case R.id.hide_show_button:
			HideShowFunction();

			break;

		case R.id.back_button:
			finish();

			break;

		case R.id.back_TV:
			fragmentsLL.setVisibility(View.VISIBLE);
			profileRL.setVisibility(View.INVISIBLE);
			backButton.setVisibility(View.VISIBLE);

			break;

		case R.id.update_TV:
			check_logout = false;
			check_update_mood = true;
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			UpdateProfileFunction();

			break;

		case R.id.change_mood_TV:

			check_update_mood = true;
			SeePrompt();

			break;

		case R.id.edit_TV:

			userName.setEnabled(true);
			userEmail.setEnabled(false);
			userMood.setEnabled(true);
			userPhone.setEnabled(true);
			city.setEnabled(true);
			country.setEnabled(true);

			userName.requestFocus();
			// Show soft keyboard for the user to enter the value.
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT);

			break;

		default:
			break;
		}

	}

	private void HideShowFunction() {
		// TODO Auto-generated method stub

		if (check_hide_show) {
			check_hide_show = false;
			LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT);
			childParam1.weight = 0.0f;
			childParam1.bottomMargin = 20;
			contacts_fragment_LL.setLayoutParams(childParam1);

			LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT);
			childParam2.weight = 1.0f;
			childParam2.bottomMargin = 20;
			message_fragment_LL.setLayoutParams(childParam2);

			fragmentsLL.removeAllViews();
			fragmentsLL.setWeightSum(1f);
			fragmentsLL.addView(contacts_fragment_LL);
			fragmentsLL.addView(message_fragment_LL);

		} else {
			check_hide_show = true;
			LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT);
			childParam1.weight = 0.4f;
			childParam1.bottomMargin = 20;
			contacts_fragment_LL.setLayoutParams(childParam1);

			LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT);
			childParam2.weight = 0.6f;
			childParam2.bottomMargin = 20;
			message_fragment_LL.setLayoutParams(childParam2);

			fragmentsLL.removeAllViews();
			fragmentsLL.setWeightSum(1f);
			fragmentsLL.addView(contacts_fragment_LL);
			fragmentsLL.addView(message_fragment_LL);

		}

	}

	private void UpdateProfileFunction() {
		// TODO Auto-generated method stub

		String user_name = userName.getText().toString();
		String user_mood = userMood.getText().toString();
		String user_phone = userPhone.getText().toString();
		String user_city = city.getText().toString();
		String user_country = country.getText().toString();

		try {
			user_Mood_String = user_mood;

			user_name = URLEncoder.encode(user_name, "UTF-8");
			user_mood = URLEncoder.encode(user_mood, "UTF-8");
			user_phone = URLEncoder.encode(user_phone, "UTF-8");
			user_city = URLEncoder.encode(user_city, "UTF-8");
			user_country = URLEncoder.encode(user_country, "UTF-8");
			String userId = prefs.getString(Welcome.USER_ID, "nothing");
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/UpdateUserProfile?UserId="
					+ userId
					+ "&Name="
					+ user_name
					+ "&Mobile="
					+ user_phone
					+ "&City="
					+ user_city
					+ "&Country="
					+ user_country
					+ "&Mood=" + user_mood;
			dialogText = "Updating profile...";

			new LogOutUser().execute();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void SeePrompt() {
		// TODO Auto-generated method stub

		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result

						user_Mood_String = userInput.getText().toString();

						updateMoodFunction(userInput.getText().toString());

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void LogOutFunction() {
		// TODO Auto-generated method stub

		String userId = prefs.getString(Welcome.USER_ID, "nothing");
		url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SignOut?UserId="
				+ userId;
		dialogText = "Logging out...";

		new LogOutUser().execute();

	}

	private void updateMoodFunction(String user_Mood) {
		// TODO Auto-generated method stub

		try {

			if (user_Mood.isEmpty() || user_Mood.matches("")) {
				Toast.makeText(getApplicationContext(),
						"Please enter your mood", Toast.LENGTH_SHORT).show();
			} else {
				user_Mood = URLEncoder.encode(user_Mood, "UTF-8");
				String userId = prefs.getString(Welcome.USER_ID, "nothing");
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/UpdateUserMoodStatus?UserId="
						+ userId + "&MoodStatus=" + user_Mood;
				dialogText = "Updating your mood...";
				new LogOutUser().execute();

			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class LogOutUser extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainMenu.this);
			pDialog.setMessage(dialogText);
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

				String success;
				try {
					success = jsonObject.getString(Welcome.SUCCESS);
					if (success != null && success.length() > 0) {
						// You will fall here only if there's a value...

						success = jsonObject.getString(Welcome.SUCCESS);

						if (success.equals("true")) {

							if (check_update_mood) {
								ContactFragment.userStatus
										.setText(user_Mood_String);
								fragmentsLL.setVisibility(View.VISIBLE);
								profileRL.setVisibility(View.INVISIBLE);
								check_update_mood = false;

							} else {
								editor.putString(Welcome.USER_ID, "");
								editor.putString("Email", "");
								editor.putString("Password", "");
								editor.putBoolean("rememberCredentials", false);
								editor.putString(Welcome.SUCCESS, "");
								editor.commit();
								dataLayerObj.DeleteAllRecords();
								Intent mainIntent = new Intent(MainMenu.this,
										Welcome.class);
								startActivity(mainIntent);
								finish();

							}

						} else {
							Toast.makeText(getApplicationContext(),
									"Log out error", Toast.LENGTH_SHORT).show();

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

	// ////////////////////////// loading profile info ////////////////////

	private class LoadDPInfo extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainMenu.this);
			pDialog.setMessage("Loading info...");
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
				String userName1;
				try {
					userName1 = jsonObject.getString(Welcome.USER_NAME);
					if (userName1 != null && userName1.length() > 0) {
						// You will fall here only if there's a value...

						String name = jsonObject.getString(Welcome.NAME);
						String phone = jsonObject.getString(Welcome.PHONE);
						String mood = jsonObject.getString(Welcome.USER_STATUS);
						if (mood.equals("null") || mood.isEmpty()
								|| mood.equals("")) {
							mood = "";
						}
						String city1 = jsonObject.getString(Welcome.CITY);
						if (city1.equals("null") || city1.isEmpty()
								|| city1.equals("")) {
							city1 = "";
						}
						String country1 = jsonObject.getString(Welcome.COUNTRY);

						if (country1.equals("null") || country1.isEmpty()
								|| country1.equals("")) {
							country1 = "";
						}

						userName.setText(name);
						userEmail.setText(userName1);
						userMood.setText(mood);
						userPhone.setText(phone);
						city.setText(city1);
						country.setText(country1);
						pDialog.dismiss();

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
