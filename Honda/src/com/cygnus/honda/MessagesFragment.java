package com.cygnus.honda;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cygnus.honda.R;

import com.cygnus.honda.database.DataLayer;
import com.cygnus.honda.listview.adapters.ContactAdapter;
import com.cygnus.honda.listview.adapters.MessagesAdapter;
import com.cygnus.honda.listview.section.ContactItem;
import com.cygnus.honda.listview.section.Message;
import com.cygnus.honda.listview.section.MessageItem;
import com.cygnus.honda.listview.section.MessageSectionItem;
import com.cygnus.honda.utilities.ConnectionDetector;
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
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessagesFragment extends Fragment implements OnClickListener {

	boolean showDialog = false, checkfavorite = false,
			check_favorite_btn = false, check_participant_btn = false,
			check_calledOnce = true;

	// //// FRIENDS DATA
	public static final String FRIEND_ID = "FriendId";
	public static final String FRIEND_MOBILE_NUMBER = "FriendMobileNumber";
	public static final String FRIEND_USER_NAME = "FriendUserName";
	public static final String FRIEND_NAME = "Name";
	public static final String FRIEND_STATUS = "OnlineStatus";
	public static final String FRIEND_DEVICE_ID = "FriendDeviceId";

	// ////// MESSAGE DATA

	public static final String MESSAGE_FROM = "MessageFrom";
	public static final String MESSAGE_TEXT = "MessageText";
	public static final String MESSAGE_TIME = "MessageTimeInline";
	public static final String MESSAGE_TO = "MessageTo";
	public static final String MESSAGE_DELETED = "isDeleted";
	public static final String MESSAGE_STATUS = "MessageStatus";

	Activity activity;

	// ///////////////////////

	ConnectionDetector detector;

	AlertDialog levelDialog;
	private ImageView friend_imageViewRound, friend_statusImage;

	Button callBtn, mapBtn, attachBtn, sendBtn, addParticipantsBtn;
	EditText messageET;
	ImageView favoriteBtn;

	TextView friendName, friendStatusTV;
	String userID, friendID, friendMobileNumber, friendDeviceId, group_ID;
	String names = "";
	CharSequence friendNameVal;
	SharedPreferences prefs;

	private static String url;
	JSONArray jsonOuterArray, contactList;

	public static MessagesAdapter messagesAdapter;
	public static ArrayList<Message> messageItems;

	ListView messagesListView, messagesListView1;

	JSONObject message_Participants;
	JSONArray messagesArray;

	DataLayer dataLayerObj;

	String contactgroup;

	SharedPreferences.Editor editor;
	MyBroadcastReceiver receiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		final View rootView = inflater.inflate(R.layout.messages_frament,
				container, false);

		activity = getActivity();

		// ///////////////// rounded imageviews //////////////
		friend_imageViewRound = (ImageView) rootView
				.findViewById(R.id.friend_imageView);
		friend_statusImage = (ImageView) rootView
				.findViewById(R.id.friend_status_img);

		// ///////////////////////////////

		dataLayerObj = new DataLayer(getActivity());

		prefs = this.getActivity().getSharedPreferences(
				LoginActivity.MY_PREFS_NAME, getActivity().MODE_PRIVATE);

		editor = this
				.getActivity()
				.getSharedPreferences(LoginActivity.MY_PREFS_NAME,
						getActivity().MODE_PRIVATE).edit();

		userID = prefs.getString(Welcome.USER_ID, null);
		messageItems = new ArrayList<Message>();
		messageItems.clear();

		// list view
		messagesListView = (ListView) rootView
				.findViewById(R.id.messages_listView);

		messagesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HideSoftKeyBoard();

			}
		});

		messagesListView1 = (ListView) rootView
				.findViewById(R.id.messages_listView1);

		messagesListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HideSoftKeyBoard();

			}
		});

		// /////// Edit text objects message write ////////
		messageET = (EditText) rootView.findViewById(R.id.message_ET);
	
		friendStatusTV = (TextView) rootView
				.findViewById(R.id.friend_status_TV);

		// //////////// TextView
		friendName = (TextView) rootView.findViewById(R.id.friend_name_TV);

		// button objects are initialized here
		favoriteBtn = (ImageView) rootView.findViewById(R.id.favorite_image);
		callBtn = (Button) rootView.findViewById(R.id.phone_btn);
		mapBtn = (Button) rootView.findViewById(R.id.location_btn);
		// attachBtn = (Button) rootView.findViewById(R.id.attach_btn);
		sendBtn = (Button) rootView.findViewById(R.id.send_btn);
		// statusBtn = (Button) rootView.findViewById(R.id.status_btn);
		
		if (LoginActivity.CHECK_TAB_SELECTED.equals("3")) {
			messageET.setEnabled(false);
			sendBtn.setEnabled(false);
		} else {
			messageET.setEnabled(true);
			sendBtn.setEnabled(true);

		}
		

		if (ModeCheck()) {

			messageET.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.edit_text_bg));

			sendBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.send_btn_bg_day));

		} else {

			messageET.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.edit_text_bg_night));

			sendBtn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.send_btn_bg_night));

		}

		addParticipantsBtn = (Button) rootView
				.findViewById(R.id.add_participant_btn);

		// button objects click listener
		addParticipantsBtn.setOnClickListener(this);
		favoriteBtn.setOnClickListener(this);

		callBtn.setOnClickListener(this);
		mapBtn.setOnClickListener(this);
		// attachBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);

		Bundle args = getArguments();
		if (args != null) {

			String strtext = args.getString("message");
			contactgroup = LoginActivity.CHECK_CONTACT_GROUP;

			if (contactgroup.equals("1")) {
				callBtn.setVisibility(View.VISIBLE);
				mapBtn.setVisibility(View.VISIBLE);
				addParticipantsBtn.setVisibility(View.INVISIBLE);
				friend_imageViewRound.setOnClickListener(this);
			} else if (contactgroup.equals("2")) {
				callBtn.setVisibility(View.INVISIBLE);
				mapBtn.setVisibility(View.VISIBLE);
				addParticipantsBtn.setVisibility(View.VISIBLE);
			}

			if (strtext.isEmpty()) {

			} else {

				try {

					String stglist = args.getString("contacts_list");
					contactList = new JSONArray(stglist);
					message_Participants = new JSONObject(strtext);

					if (contactgroup.equals("2")) {

						FillGroupData();

					} else {
						FillFriendsData();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} else {
			// here broadcast receiver is register to get notifications

			if (LoginActivity.CHECK_REGISTER_BROADCAST_RECEIVER.equals("1")) {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("com.cygnus.honda.blinkled");
				receiver = new MyBroadcastReceiver();
				getActivity().registerReceiver(receiver, intentFilter);
				LoginActivity.CHECK_REGISTER_BROADCAST_RECEIVER = "2";

			}

		}

		return rootView;
	}



	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	private void FillGroupData() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String friend;
		try {
			friend = message_Participants.getString("GroupName");
			friendName.setText(friend);
			group_ID = message_Participants.getString("Id");
			ContactFragment.GROUP_ID = group_ID;

			friend_statusImage.setVisibility(View.INVISIBLE);

			JSONArray jArray = message_Participants
					.getJSONArray("GroupParticipants");

			for (int j = 0; j < jArray.length(); j++) {
				JSONObject jOb = jArray.getJSONObject(j);
				names = names + jOb.getString("Name") + " ";

			}
			friendStatusTV.setText(names);

			FillGroupMessagesData();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean CheckConnection() {

		boolean check = false;

		detector = new ConnectionDetector(getActivity());

		if (detector.isConnectingToInternet()) {
			check = true;

		} else {

			Toast.makeText(getActivity(), "Check your internet connection",
					Toast.LENGTH_LONG).show();

		}
		return check;
	}

	private void FillGroupMessagesData() {
		// TODO Auto-generated method stub

		messageItems = new ArrayList<Message>();
		messageItems.clear();
		boolean checkOrientation = ModeCheck();
		messageItems = dataLayerObj.GetMessages(userID, group_ID, messageItems);

		if (messageItems.size() > 0) {
			messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
					checkOrientation);
			messagesListView1.setAdapter(messagesAdapter);

			messagesListView1.setVisibility(View.VISIBLE);
			messagesListView.setVisibility(View.INVISIBLE);

		} else {
			showDialog = true;

		}

		if (CheckConnection()) {
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/LoadGroupConversation?GroupId="
					+ group_ID;
			new GetGroupMessage(getActivity()).execute();
		}

	}

	// ///////////// BroadCastReceiver is defined here /////////////
	private class MyBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			blinkLED(intent);

		}
	}

	public void blinkLED(Intent intent) {
		// TODO Auto-generated method stub

		String messageReceived = intent.getStringExtra("message_received");
		final String[] separated = messageReceived.split(":");
		contactgroup = LoginActivity.CHECK_CONTACT_GROUP;
		group_ID = ContactFragment.GROUP_ID;
		friendID = ContactFragment.FRIEND_ID;
		if (separated[0].equals("0")) {
			if (contactgroup.equals("2")) {

				ContactFragment.FRIEND_ID = "-1";

				if (separated[1].equals(group_ID)) {

					editor.putString("refreshcontact" + group_ID, "");
					editor.commit();
					SimpleDateFormat sdfDate = new SimpleDateFormat(
							"dd/MM/yyyy");
					SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

					Date now = new Date();

					String strDate = sdfDate.format(now);
					String strTime = sdfTime.format(now);

					String time = strDate + " at " + strTime;

					long res = dataLayerObj.AddMessages(group_ID, userID,
							group_ID, separated[2] + ": " + separated[3], time,
							"1");

					if (res > 0) {

						messageItems.add(new MessageItem(separated[2] + ": "
								+ separated[3], time, true));

						ShowGroupStoreMessages();

					}

				} else {
					Intent intent1 = new Intent();
					intent1.putExtra("message_received", "");
					intent1.setAction("com.cygnus.honda.blinkledcontact");
					getActivity().sendBroadcast(intent1);
				}
			} else if (contactgroup.equals("1")) {

				ContactFragment.GROUP_ID = "-1";

				if (separated[1].equals(friendID)) {

					editor.putString("refreshcontact" + friendID, "");
					editor.commit();

					SimpleDateFormat sdfDate = new SimpleDateFormat(
							"dd/MM/yyyy");
					SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

					Date now = new Date();

					String strDate = sdfDate.format(now);
					String strTime = sdfTime.format(now);

					String time = strDate + " at " + strTime;

					long res = dataLayerObj.AddMessages(friendID, userID,
							friendID, separated[2], time, "1");
					if (res > 0) {

						messageItems.add(new MessageItem(separated[2], time,
								true));

						ShowStoreMessages();

					}

				} else {
					Intent intent1 = new Intent();
					intent1.putExtra("message_received", "");
					intent1.setAction("com.cygnus.honda.blinkledcontact");
					getActivity().sendBroadcast(intent1);
				}
			} 
			else {
				Intent intent1 = new Intent();
				intent1.putExtra("message_received", "");
				intent1.setAction("com.cygnus.honda.blinkledcontact");
				getActivity().sendBroadcast(intent1);
			}
		} else if (separated[0].equals("2")) {

		}

		else {

			Toast.makeText(getActivity(), messageReceived, Toast.LENGTH_SHORT)
					.show();

		}

	}

	private void FillFriendsData() {
		// TODO Auto-generated method stub
		String friend;
		try {
			friend = message_Participants.getString(FRIEND_NAME);
			friendDeviceId = message_Participants.getString(FRIEND_DEVICE_ID);
			messagesArray = message_Participants
					.getJSONArray("RecentConversation");
			friendName.setText(friend);
			String friendMoodStatus = message_Participants
					.getString(Welcome.USER_STATUS);
			if (friendMoodStatus.equals("null")) {
				friendMoodStatus = "";
			}
			friendStatusTV.setText(friendMoodStatus);

			friendID = message_Participants.getString(FRIEND_ID);

			ContactFragment.FRIEND_ID = friendID;

			editor.putString("friendid" + friendID, friendID);
			editor.commit();
			editor.apply();

			Integer imageVal = Integer.valueOf(message_Participants
					.getString("OnlineStatus"));

			boolean isFavorite = Boolean.valueOf(message_Participants
					.getString("IsFavorite"));
			if (isFavorite) {
				favoriteBtn.setImageResource(R.drawable.favorite);
				checkfavorite = false;
			} else {
				favoriteBtn.setImageResource(R.drawable.unfavorite);
				checkfavorite = true;

			}

			Bitmap userStatusImage = BitmapFactory.decodeResource(
					getResources(), ContactFragment.statusImage[imageVal]);
			friend_statusImage.setImageBitmap(userStatusImage);

			friendMobileNumber = message_Participants
					.getString(FRIEND_MOBILE_NUMBER);
			// FillMessagesData();

			FillSingleMessagesData();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ShowGroupStoreMessages() {

		boolean checkOrientation = ModeCheck();

		// Toast.makeText(getActivity(), "Message received", Toast.LENGTH_SHORT)
		// .show();
		//
		// Toast.makeText(getActivity(), "Message received 123",
		// Toast.LENGTH_SHORT)
		// .show();
		//
		// messageItems = new ArrayList<Message>();
		// messageItems.clear();
		// messagesAdapter.clearAdapter();
		// messagesAdapter = null;
		// messageItems = dataLayerObj.GetMessages(userID, group_ID,
		// messageItems);

		// messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
		// checkOrientation);
		messagesAdapter.notifyAdapter();
		// messagesListView.setAdapter(messagesAdapter);

		// scrollMyListViewToBottom();

	}

	private void FillSingleMessagesData() {
		// TODO Auto-generated method stub

		messageItems = new ArrayList<Message>();

		boolean checkOrientation = ModeCheck();
		messageItems.clear();

		messageItems = dataLayerObj.GetMessages(userID, friendID, messageItems);

		if (messageItems.size() > 0) {
			messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
					checkOrientation);

			messagesListView1.setAdapter(messagesAdapter);

			messagesListView1.setVisibility(View.VISIBLE);
			messagesListView.setVisibility(View.INVISIBLE);

		} else {
			showDialog = true;
		}

		if (CheckConnection()) {

			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/LoadSingleConversation?From="
					+ userID + "&To=" + friendID;
			new GetSingleMessage(getActivity()).execute();
		}

	}

	private void scrollMyListViewToBottom() {

		messagesListView.clearFocus();
		messagesListView
				.setSelection(messagesListView.getAdapter().getCount() - 1);
		messagesListView.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				messagesListView.setSelection(messagesListView.getCount() - 1);
			}
		});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		Integer viewID = view.getId();
		switch (viewID) {
		case R.id.favorite_image:
			// ToastDisplay("Favorite is clicked!");

			check_participant_btn = false;
			check_favorite_btn = true;

			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AddOrRemoveFavorite?UserId="
					+ userID
					+ "&FriendId="
					+ friendID
					+ "&MakeFavorite="
					+ checkfavorite;
			new AddFriendInGroup().execute();

			break;
		case R.id.phone_btn:

			if (friendMobileNumber != null) {
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						getActivity());
				// Setting Dialog Title
				builder1.setTitle("Confirmation");
				builder1.setMessage("Do you want to call?");
				builder1.setCancelable(true);
				builder1.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(Intent.ACTION_CALL);
								intent.setData(Uri.parse("tel:"
										+ friendMobileNumber));
								startActivity(intent);
								dialog.cancel();
							}
						});
				builder1.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();

			}

			break;

		case R.id.location_btn:

			if (LoginActivity.CHECK_TAB_SELECTED.equals("1")
					|| LoginActivity.CHECK_TAB_SELECTED.equals("2")) {
				Intent mapIntent = new Intent(getActivity(), CustomTask.class);
				mapIntent.putExtra("participants",
						message_Participants.toString());
				startActivity(mapIntent);

			}

			break;

		case R.id.friend_imageView:
			check_participant_btn = false;
			check_favorite_btn = true;

			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AddOrRemoveFavorite?UserId="
					+ userID
					+ "&FriendId="
					+ friendID
					+ "&MakeFavorite="
					+ checkfavorite;
			new AddFriendInGroup().execute();

			break;

		case R.id.send_btn:

			if (LoginActivity.CHECK_TAB_SELECTED.equals("2")) {
				SendButtonFunction();

			} else if (LoginActivity.CHECK_TAB_SELECTED.equals("1")) {
				SendButtonFunction();
			} else {
				Toast.makeText(getActivity(),
						"Select any contact or group to send message",
						Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.add_participant_btn:

			check_participant_btn = true;
			check_favorite_btn = false;

			AddParticipantsInGroup();

			break;

		default:
			break;
		}

	}

	private void AddParticipantsInGroup() {
		// TODO Auto-generated method stub

		final CharSequence[] optionsStr = new String[contactList.length()];

		final String[] friendID = new String[contactList.length()];

		for (int i = 0; i < contactList.length(); i++) {

			JSONObject jsonObj;

			try {
				jsonObj = contactList.getJSONObject(i);
				String userName = jsonObj.getString("Name");
				String id = jsonObj.getString("FriendId");
				optionsStr[i] = userName;
				friendID[i] = id;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Friend in Group:");
		builder.setSingleChoiceItems(optionsStr, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String ID = friendID[item];
						if (CheckConnection()) {
							url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AddGroupParticipant?GroupId="
									+ group_ID + "&ParticipantId=" + ID;
							friendNameVal = optionsStr[item];
							new AddFriendInGroup().execute();

						}

						levelDialog.dismiss();
					}
				});
		levelDialog = builder.create();
		levelDialog.show();

	}

	private void SendButtonFunction() {

		String messageTxt = messageET.getText().toString();
		if (messageTxt.isEmpty() || messageTxt.matches("")) {
			return;
		}

		// Calendar c = Calendar.getInstance();
		// int mint = c.get(Calendar.MINUTE);
		// int hour = c.get(c.HOUR);
		// String time = String.valueOf(hour) + ":" + String.valueOf(mint);
		//

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

		Date now = new Date();

		String strDate = sdfDate.format(now);
		String strTime = sdfTime.format(now);

		String time = strDate + " at " + strTime;

		if (CheckConnection()) {
			messageET.setText("");
			if (contactgroup.equals("2")) {

				long res = dataLayerObj.AddMessages(group_ID, group_ID, userID,
						messageTxt, time, "1");

				if (res > 0) {
					// Toast.makeText(getActivity(), "record inserted: ",
					// Toast.LENGTH_SHORT).show();
				}
				ShowDBGroupMessages(messageTxt);

			} else {

				long res = dataLayerObj.AddMessages(friendID, friendID, userID,
						messageTxt, time, "1");

				if (res > 0) {
					// Toast.makeText(getActivity(), "record inserted: ",
					// Toast.LENGTH_SHORT).show();
				}
				ShowDBMessages(messageTxt);

			}

		}

	}

	private void ShowDBGroupMessages(String messageTxt) {
		// TODO Auto-generated method stub

		boolean checkOrientation = ModeCheck();

		messageItems = new ArrayList<Message>();
		messageItems.clear();

		messagesAdapter.clearAdapter();

		messageItems = dataLayerObj.GetMessages(userID, group_ID, messageItems);
		messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
				checkOrientation);
		messagesListView.setAdapter(messagesAdapter);
		// scrollMyListViewToBottom();

		try {
			messageTxt = URLEncoder.encode(messageTxt, "UTF-8");

			if (CheckConnection()) {
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SendGroupMessage?ApplicationId="
						+ Welcome.APPLICATION_ID
						+ "&CompanyId="
						+ Welcome.COMPANY_ID
						+ "&From="
						+ userID
						+ "&GroupId="
						+ group_ID + "&Message=" + messageTxt;
				new PostData().execute();

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void ToastDisplay(String display) {
		// TODO Auto-generated method stub

		Toast.makeText(getActivity(), display, Toast.LENGTH_SHORT).show();

	}

	protected void HideSoftKeyBoard() {
		// TODO Auto-generated method stub

		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}

	private void ShowStoreMessages() {
		// messageItems = new ArrayList<Message>();

		boolean checkOrientation = ModeCheck();
		// messageItems.clear();
		//
		// messagesAdapter.clearAdapter();
		//
		// messageItems = dataLayerObj.GetMessages(userID, friendID,
		// messageItems);
		// messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
		// checkOrientation);
		messagesAdapter.notifyAdapter();
		// messagesListView.setAdapter(messagesAdapter);

	}

	private void ShowDBMessages(String messageTxt) {
		// TODO Auto-generated method stub

		boolean checkOrientation = ModeCheck();
		messageItems = new ArrayList<Message>();
		messageItems.clear();

		messagesAdapter.clearAdapter();
		messageItems = dataLayerObj.GetMessages(userID, friendID, messageItems);
		messagesAdapter = new MessagesAdapter(getActivity(), messageItems,
				checkOrientation);
		messagesListView.setAdapter(messagesAdapter);
		// scrollMyListViewToBottom();

		try {
			messageTxt = URLEncoder.encode(messageTxt, "UTF-8");

			if (CheckConnection()) {
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SendSingleMesage?ApplicationId="
						+ Welcome.APPLICATION_ID
						+ "&CompanyId="
						+ Welcome.COMPANY_ID
						+ "&From="
						+ userID
						+ "&To="
						+ friendID
						+ "&Message="
						+ messageTxt
						+ "&DeviceId="
						+ friendDeviceId;

				new PostData().execute();

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class GetSingleMessage extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		private Activity activity;

		public GetSingleMessage(Activity activity) {
			this.activity = activity;
			context = activity;
			dialog = new ProgressDialog(context);
		}

		/** progress dialog to show user that the backup is processing. */

		/** application context. */
		private Context context;

		protected void onPreExecute() {
			dialog.setMessage("Loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);

			if (showDialog) {
				dialog.show();

			} else {

			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			ContactFragment.pressCheck = true;

			if (jsonOuterArray.length() > 0) {

				boolean checkOrientation = ModeCheck();

				// messagesListView.setAdapter(null);

				// messagesListView.setAdapter(null);

				// messagesAdapter = new MessagesAdapter(getActivity(),
				// messageItems, checkOrientation);
				// messagesListView.setAdapter(messagesAdapter);
				// scrollMyListViewToBottom();

				// if (showDialog) {

				messagesAdapter = new MessagesAdapter(getActivity(),
						messageItems, checkOrientation);
				messagesListView.setAdapter(messagesAdapter);
				// } else {
				// messagesAdapter.notifyAdapter();
				//
				// }

			} else {

			}

			messagesListView1.setVisibility(View.INVISIBLE);
			messagesListView.setVisibility(View.VISIBLE);

		}

		protected Boolean doInBackground(final String... args) {

			JSONParser jParser = new JSONParser();
			// getting JSON string from URL
			jsonOuterArray = new JSONArray();
			jsonOuterArray = jParser.getJSONFromUrl(url);
			messageItems = new ArrayList<Message>();
			messageItems.clear();
			dataLayerObj.DeleteMessages(friendID);

			for (int i = jsonOuterArray.length() - 1; i >= 0; i--) {
				JSONObject jsonObj;
				// try {
				// jsonObj = jsonOuterArrayFriends.getJSONObject(i);
				try {
					jsonObj = jsonOuterArray.getJSONObject(i);
					String message = jsonObj.getString(MESSAGE_TEXT);
					String date = jsonObj.getString(MESSAGE_TIME);
					String messageFrom = jsonObj.getString(MESSAGE_FROM);
					String messageTo = jsonObj.getString(MESSAGE_TO);

					long res = dataLayerObj.AddMessages(friendID, messageTo,
							messageFrom, message, date, "1");

					if (res > 0) {
						// Toast.makeText(getActivity(), "record inserted: "+
						// message,
						// Toast.LENGTH_SHORT).show();
					}

					if (messageFrom.equals(userID)) {
						messageItems.add(new MessageItem(message, date, false));

					} else {
						messageItems.add(new MessageItem(message, date, true));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;

		}
	}

	private class PostData extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Sending message...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			// pDialog.show();
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
					success = jsonObject.getString("Success");
					if (success.equals("true")) {

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "exception",
							Toast.LENGTH_SHORT).show();
				}

			}

			// pDialog.dismiss();
		}
	}

	// ///////////////// add participant in group ////////////

	private class AddFriendInGroup extends
			AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			if (check_participant_btn) {
				pDialog.setMessage("Adding participant...");
			} else {
				pDialog.setMessage("Updating...");
			}

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
					success = jsonObject.getString("Success");
					if (success.equals("true")) {
						if (check_participant_btn) {
							names = names + " " + friendNameVal;
							friendStatusTV.setText(names);
							Toast.makeText(getActivity(),
									"Participant is added", Toast.LENGTH_SHORT)
									.show();

						} else {
							if (checkfavorite) {
								favoriteBtn
										.setImageResource(R.drawable.favorite);
								checkfavorite = false;

							} else {

								favoriteBtn
										.setImageResource(R.drawable.unfavorite);
								checkfavorite = true;

							}

							Intent intent1 = new Intent();
							intent1.putExtra("message_received", "1");
							intent1.setAction("com.cygnus.honda.blinkledcontact");
							getActivity().sendBroadcast(intent1);

						}

					} else {

						if (check_participant_btn) {
							Toast.makeText(getActivity(),
									"User is already in this group",
									Toast.LENGTH_SHORT).show();
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

	// /////////////////////// Load Group conversation ////////////////////

	private class GetGroupMessage extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		private Activity activity;

		public GetGroupMessage(Activity activity) {
			this.activity = activity;
			context = activity;
			dialog = new ProgressDialog(context);
		}

		/** progress dialog to show user that the backup is processing. */

		/** application context. */
		private Context context;

		protected void onPreExecute() {
			dialog.setMessage("Loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			if (showDialog) {
				dialog.show();

			} else {

			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (showDialog) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

			} else {

			}

			ContactFragment.pressCheck = true;

			if (jsonOuterArray.length() > 0) {

				boolean checkOrientation = ModeCheck();

				// if (showDialog) {
				//

				messagesAdapter = new MessagesAdapter(getActivity(),
						messageItems, checkOrientation);
				messagesListView.setAdapter(messagesAdapter);
				// }else{
				// messagesAdapter.notifyAdapter();
				// }

			} else {

			}

			messagesListView1.setVisibility(View.INVISIBLE);
			messagesListView.setVisibility(View.VISIBLE);

		}

		protected Boolean doInBackground(final String... args) {

			JSONParser jParser = new JSONParser();
			// getting JSON string from URL
			jsonOuterArray = new JSONArray();
			jsonOuterArray = jParser.getJSONFromUrl(url);
			messageItems = new ArrayList<Message>();
			messageItems.clear();

			dataLayerObj.DeleteMessages(group_ID);

			for (int i = jsonOuterArray.length() - 1; i >= 0; i--) {
				JSONObject jsonObj;
				// try {
				// jsonObj = jsonOuterArrayFriends.getJSONObject(i);
				try {
					jsonObj = jsonOuterArray.getJSONObject(i);
					String message = jsonObj.getString(MESSAGE_TEXT);
					String date = jsonObj.getString(MESSAGE_TIME);
					String messageFrom = jsonObj.getString(MESSAGE_FROM);
					String messageTo = jsonObj.getString(MESSAGE_TO);

					long res = dataLayerObj.AddMessages(group_ID, messageTo,
							messageFrom, message, date, "1");

					if (res > 0) {
						// Toast.makeText(getActivity(), "record inserted: "+
						// message,
						// Toast.LENGTH_SHORT).show();
					}

					if (messageFrom.equals(userID)) {
						messageItems.add(new MessageItem(message, date, false));

					} else {
						messageItems.add(new MessageItem(message, date, true));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;

		}
	}



}
