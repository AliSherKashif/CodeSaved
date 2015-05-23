package com.cygnus.honda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cygnus.honda.utilities.JSONParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomTask extends FragmentActivity {

	String url = "http://maps.google.com/maps/api/geocode/json?address=";

	String address;
	SharedPreferences prefs;

	GoogleMap gMmap;
	ArrayList<LatLng> markerPoints;
	TextView tvDistanceDuration;
	ArrayList<String> names;

	Button backBtn;
	RelativeLayout titleBar;

	Integer statusVal = 50;

	JSONObject participantsObj;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.custom_task);
		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
		backBtn = (Button) findViewById(R.id.back_button);

		titleBar = (RelativeLayout) findViewById(R.id.logo);

		if (ModeCheck()) {

			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));
			
			backBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.back_btn));

		} else {

			titleBar.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));
			
			backBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.back_btn_white));

		}

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		names = new ArrayList<String>();

		Intent intent = getIntent();
		if (intent.hasExtra("participants")) {
			String participants = intent.getExtras().getString("participants");
			try {
				participantsObj = new JSONObject(participants);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// initialize the arraylist
		markerPoints = new ArrayList<LatLng>();

		if (LoginActivity.CHECK_TAB_SELECTED.equals("2")) {

			JSONArray participantsArray1 = null;

			try {
				participantsArray1 = participantsObj
						.getJSONArray("GroupParticipants");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (int i = 0; i < participantsArray1.length(); i++) {

				JSONObject jsonObj;

				try {
					jsonObj = participantsArray1.getJSONObject(i);
					String latitude = jsonObj.getString("Latitude");
					String longitude = jsonObj.getString("Longitude");
					names.add(jsonObj.getString("Name"));
					if (latitude.equals("null") && longitude.equals("null")) {

					} else {
						LatLng point1 = new LatLng(Double.valueOf(latitude),
								Double.valueOf(longitude));
						markerPoints.add(point1);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {

			try {
				String latitude = participantsObj.getString("Latitude");
				String longitude = participantsObj.getString("Longitude");
				names.add(participantsObj.getString("Name"));
				statusVal = Integer.valueOf(participantsObj
						.getString("OnlineStatus"));

				if (latitude.equals("null") && longitude.equals("null")) {

				} else {
					LatLng point1 = new LatLng(Double.valueOf(latitude),
							Double.valueOf(longitude));
					markerPoints.add(point1);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		SupportMapFragment mapFragement = (SupportMapFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.map);

		gMmap = mapFragement.getMap();
		gMmap.setMyLocationEnabled(true);

		showPoints();

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	private void showPoints() {
		// TODO Auto-generated method stub

		// Creating MarkerOptions
		MarkerOptions options = new MarkerOptions();

		for (Integer i = 0; i < markerPoints.size(); i++) {
			LatLng point = markerPoints.get(i);

			// Setting the position of the marker
			options.position(point);

			// Setting title for the MarkerOptions
			options.title(names.get(i));
			// Setting snippet for the MarkerOptions
			options.snippet("Latitude:" + point.latitude + ",Longitude: "
					+ point.longitude);

			if (statusVal == 0) {

				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

			} else {
				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

			}

			// Add new marker to the Google Map Android API V2
			gMmap.addMarker(options);

			// Creating CameraUpdate object for position
			CameraUpdate updatePosition = CameraUpdateFactory.newLatLngZoom(
					point, 15);

			// Applying zoom to the marker position
			gMmap.animateCamera(updatePosition);
		}

	}

	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// Integer viewID = v.getId();
	//
	// switch (viewID) {
	// case R.id.show_map_btn:
	//
	// // address = addressET.getText().toString();
	// // new JSONParse().execute();
	// showPoints();
	//
	// break;
	//
	// case R.id.draw_route_btn:
	//
	// LatLng origin = markerPoints.get(0);
	// LatLng dest = markerPoints.get(1);
	//
	// // Getting URL to the Google Directions API
	// String url = getDirectionsUrl(origin, dest);
	//
	// DownloadTask downloadTask = new DownloadTask();
	//
	// // Start downloading json data from Google Directions API
	// downloadTask.execute(url);
	//
	// break;
	//
	// default:
	// break;
	// }
	//
	// }

	private class JSONParse extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CustomTask.this);
			pDialog.setMessage("Searching...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser jParser = new JSONParser();
			JSONObject json = null;
			// Getting JSON from URL
			try {
				address = URLEncoder.encode(address, "UTF-8");
				url = url + address + "&sensor=false";
				json = jParser.getJSONFromUrlPostJson(url);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			if (jsonObject != null) {
				// Wahetever you need
				Double longitude = null, latitude = null;
				try {
					longitude = ((JSONArray) jsonObject.get("results"))
							.getJSONObject(0).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lng");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					latitude = ((JSONArray) jsonObject.get("results"))
							.getJSONObject(0).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lat");

					LatLng latLonObj = new LatLng(11.123456, 12.123456);

					markerPoints.add(latLonObj);

					// Creating MarkerOptions
					MarkerOptions options = new MarkerOptions();

					// Setting the position of the marker
					options.position(latLonObj);

					// Setting title for the MarkerOptions
					options.title("Position");

					// Setting snippet for the MarkerOptions
					options.snippet("Latitude:" + latitude + ",Longitude:"
							+ longitude);

					// options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

					// Add new marker to the Google Map Android API V2
					gMmap.addMarker(options);

					// Creating CameraUpdate object for position
					CameraUpdate updatePosition = CameraUpdateFactory
							.newLatLng(latLonObj);

					// Creating CameraUpdate object for zoom
					CameraUpdate updateZoom = CameraUpdateFactory.zoomBy(4);

					// Updating the camera position to the user input latitude
					// and longitude
					gMmap.moveCamera(updatePosition);

					// Applying zoom to the marker position
					gMmap.animateCamera(updateZoom);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			pDialog.dismiss();
		}
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			if (result.size() < 1) {
				Toast.makeText(getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = point.get("duration");
						continue;
					}
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(10);
				lineOptions.color(Color.RED);

			}

			// tvDistanceDuration.setText("Distance:" + distance + ", Duration:"
			// + duration);

			// Drawing polyline in the Google Map for the i-th route
			gMmap.addPolyline(lineOptions);
		}
	}

}
