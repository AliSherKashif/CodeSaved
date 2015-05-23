package com.cygnus.honda;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import com.cygnus.honda.utilities.JSONParser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroupFragment extends Fragment implements OnClickListener {

	Button createGroupBtn, backBtn;
	EditText groupNameET;
	private static String url;
	String userID;
	SharedPreferences prefs;

	boolean checkUser = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		final View rootView = inflater.inflate(R.layout.create_group_fragment,
				container, false);

		prefs = this.getActivity().getSharedPreferences(
				LoginActivity.MY_PREFS_NAME, getActivity().MODE_PRIVATE);

		userID = prefs.getString(Welcome.USER_ID, null);

		createGroupBtn = (Button) rootView.findViewById(R.id.create_group_btn);
		backBtn = (Button) rootView.findViewById(R.id.back_btn);
		groupNameET = (EditText) rootView.findViewById(R.id.group_name_TV);

		createGroupBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		Integer viewID = view.getId();
		switch (viewID) {

		case R.id.create_group_btn:

			String groupName = groupNameET.getText().toString();
			if (groupName.isEmpty()) {
				Toast.makeText(getActivity(), "Please enter group name",
						Toast.LENGTH_SHORT).show();

				return;
			}

			try {
				groupName = URLEncoder.encode(groupName, "UTF-8");
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/CreateNewGroup?UserId="
						+ userID + "&GroupName=" + groupName;
				new PostData().execute();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case R.id.back_btn:
			MessagesFragment fragMessageobj = new MessagesFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.fragment_place, fragMessageobj).commit();
			break;

		default:
			break;
		}

	}

	private class PostData extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Creating group...");
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

				String success, groupID;
				try {
					success = jsonObject.getString(Welcome.SUCCESS);
					// groupID = jsonObject.getString(Welcome.SUCCESS);
					if (success != null && success.length() > 0) {
						// You will fall here only if there's a value...

						if (success.equals("true")) {

							// if(checkUser){

							// set Fragmentclass Arguments
							// ContactFragment fragobj = new ContactFragment();
							// LoginActivity.CHECK_CONTACT_GROUP = "3";
							//
							// FragmentManager fragmentManager =
							// getFragmentManager();
							// fragmentManager.beginTransaction()
							// .replace(R.id.fragment_place2, fragobj)
							// .commit();
							//
							// // set Fragmentclass Arguments
							// MessagesFragment fragMessageobj = new
							// MessagesFragment();
							// fragmentManager
							// .beginTransaction()
							// .replace(R.id.fragment_place,
							// fragMessageobj).commit();

							// getActivity().finish();
							//
							// Intent mainIntent = new Intent(getActivity(),
							// LoginActivity.class);
							// startActivity(mainIntent);

							closeFragment();

						} else {
							Toast.makeText(getActivity(),
									"Group creation error", Toast.LENGTH_SHORT)
									.show();

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "exception",
							Toast.LENGTH_SHORT).show();
				}

			}

			pDialog.dismiss();
		}
	}

	public void closeFragment() {
		// TODO Auto-generated method stub

		LoginActivity.CHECK_CONTACT_GROUP = LoginActivity.CHECK_CONTACT_GROUP;
		ContactFragment.pressCheck = true;
		// getActivity().getFragmentManager().popBackStack();
		

		Intent intent1 = new Intent();
		intent1.putExtra("message_received", "1");
		intent1.setAction("com.cygnus.honda.blinkledcontact");
		getActivity().sendBroadcast(intent1);
		
		getActivity().getFragmentManager().beginTransaction().remove(this)
		.commit();
		

	}

}
