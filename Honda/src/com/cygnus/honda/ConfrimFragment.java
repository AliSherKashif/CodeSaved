package com.cygnus.honda;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cygnus.honda.utilities.JSONParser;

public class ConfrimFragment extends Fragment implements OnClickListener {

	Button backBtn, confrimBtn, rejectBtn;
	private static String url;
	String userID, friendID;
	SharedPreferences prefs;

	private ImageView friend_statusImage;
	TextView friendName, friendStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		final View rootView = inflater.inflate(
				R.layout.confrim_friend_fragment, container, false);

		friend_statusImage = (ImageView) rootView
				.findViewById(R.id.friend_status_img);

		prefs = this.getActivity().getSharedPreferences(
				LoginActivity.MY_PREFS_NAME, getActivity().MODE_PRIVATE);

		userID = prefs.getString(Welcome.USER_ID, null);

		confrimBtn = (Button) rootView.findViewById(R.id.confirm_btn);
		backBtn = (Button) rootView.findViewById(R.id.back_btn);
		rejectBtn = (Button) rootView.findViewById(R.id.reject_btn);

		friendName = (TextView) rootView.findViewById(R.id.friend_name_TV);
		friendStatus = (TextView) rootView.findViewById(R.id.friend_status_TV);

		// //////////// pass arguments ////////////////////////

		Bundle args = getArguments();
		if (args != null) {

			String strtext = args.getString("message");

			try {
				JSONObject friendJsonObj = new JSONObject(strtext);

				String friend = friendJsonObj
						.getString(MessagesFragment.FRIEND_NAME);
				friendName.setText(friend);

				friendID = friendJsonObj.getString(MessagesFragment.FRIEND_ID);

				String friendMoodStatus = friendJsonObj
						.getString(Welcome.USER_STATUS);
				if (friendMoodStatus.equals("null")) {
					friendMoodStatus = "";
				}

				boolean IsConfirmed = friendJsonObj.getBoolean("IsConfirmed");
				boolean IsRequestReciever = friendJsonObj
						.getBoolean("IsRequestReciever");

				if (IsRequestReciever) {
					friendStatus.setText(friendMoodStatus);

				} else {

					friendStatus
							.setText("This contact has not shared his details");
					confrimBtn.setVisibility(View.INVISIBLE);
					rejectBtn.setVisibility(View.INVISIBLE);

				}

				Integer imageVal = Integer.valueOf(friendJsonObj
						.getString("OnlineStatus"));

				Bitmap userStatusImage = BitmapFactory.decodeResource(
						getResources(), ContactFragment.statusImage[0]);
				friend_statusImage.setImageBitmap(userStatusImage);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		confrimBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		rejectBtn.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		Integer viewID = view.getId();
		switch (viewID) {

		case R.id.confirm_btn:

			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AcceptOrRejectRequest?RecieverId="
					+ userID + "&FriendId=" + friendID + "&IsConfirmed=true";
			new PostData().execute();

			break;

		case R.id.reject_btn:

			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AcceptOrRejectRequest?RecieverId="
					+ userID + "&FriendId=" + friendID + "&IsConfirmed=false";
			new PostData().execute();

			break;

		case R.id.back_btn:

			// set Fragmentclass Arguments
			LoginActivity.CHECK_TAB_SELECTED = "1";
			ContactFragment.pressCheck = true;
//			Intent intent1 = new Intent();
//			intent1.putExtra("message_received", "1");
//			intent1.setAction("com.cygnus.honda.blinkledcontact");
//			getActivity().sendBroadcast(intent1);
			getActivity().getFragmentManager().beginTransaction().remove(this)
					.commit();

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
			pDialog.setMessage("Updating...");
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

							CloseFragment();

						} else {
							Toast.makeText(getActivity(),
									"Updating status error", Toast.LENGTH_SHORT)
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

	public void CloseFragment() {
		// TODO Auto-generated method stub

		LoginActivity.CHECK_TAB_SELECTED = "1";
		ContactFragment.pressCheck = true;
		// getActivity().getFragmentManager().popBackStack();
		getActivity().getFragmentManager().beginTransaction().remove(this)
				.commit();

		Intent intent1 = new Intent();
		intent1.putExtra("message_received", "1");
		intent1.setAction("com.cygnus.honda.blinkledcontact");
		getActivity().sendBroadcast(intent1);

	}

}
