package com.cygnus.honda;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cygnus.honda.listview.adapters.ContactAdapter;
import com.cygnus.honda.listview.section.Contact;
import com.cygnus.honda.listview.section.ContactItem;
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactFragment extends Fragment implements OnClickListener {

	MyBroadcastReceiver receiver;

	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	public static String userID;
	ConnectionDetector detector;

	public static boolean searchBool = false, check_refresh_contacts = false;

	int statusValCheck = 0;

	String dialogText;
	String responseText;

	public static boolean pressCheck = true;

	public static String FRIEND_ID = "-1";
	public static String GROUP_ID = "-1";
	String groupID = null;

	private ImageView imageViewRound, statusChangeImage;
	TextView contactsTV, recentTV, favoriteTV, groupsTV, separatorTV, userName,
			separator;

	public static TextView userStatus;

	Button searchBtn;

	public static ListView contactListView;
	public static ListView searchContactsListView;

	// json arrays used for tabs: contacts, recents, favorites, groups
	public static JSONArray jsonOuterArray, jsonOuterArrayFriends,
			jsonOuterArrayGroups, jsonOuterArrayFavorites,
			jsonOuterArrayRecents;

	AlertDialog levelDialog;

	public static ArrayList<ContactItem> contactsItems;
	public static ArrayList<ContactItem> contactsItemsFavorites;
	public static ArrayList<ContactItem> contactsItemsRecents;

	public static ArrayList<ContactItem> searchItems;

	public static ContactAdapter contactsAdapter;
	public static ContactAdapter searchAdapter;

	AlertDialog optionDialog;
	Resources resources;

	EditText searchText;

	HorizontalScrollView tabScrollView;

	private static String url;

	// ////////////////////

	public static Integer contactImage = R.drawable.image;

	public static Integer[] statusImage = new Integer[] { R.drawable.offline,
			R.drawable.online, R.drawable.away };

	// ////////////////////////

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.contacts_fragment,
				container, false);

		getActivity().startService(
				new Intent(getActivity(), LocationUpdateService.class));

		LoginActivity.CHECK_CONTACT_GROUP = "3";

		prefs = this.getActivity().getSharedPreferences(
				LoginActivity.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
		editor = this
				.getActivity()
				.getSharedPreferences(LoginActivity.MY_PREFS_NAME,
						getActivity().MODE_PRIVATE).edit();

		userID = prefs.getString(Welcome.USER_ID, null);
		searchText = (EditText) rootView.findViewById(R.id.searchView);

		searchBtn = (Button) rootView.findViewById(R.id.search_text);

		tabScrollView = (HorizontalScrollView) rootView
				.findViewById(R.id.horizonta_scrollview);
		// ///// tabs Textviews objects are initialized here
		separator = (TextView) rootView.findViewById(R.id.separator_TV2);

		separatorTV = (TextView) rootView.findViewById(R.id.separator_TV1);
		userName = (TextView) rootView.findViewById(R.id.user_name);
		userStatus = (TextView) rootView.findViewById(R.id.user_status);

		contactsTV = (TextView) rootView.findViewById(R.id.contacts_tv);
		recentTV = (TextView) rootView.findViewById(R.id.recent_tv);
		favoriteTV = (TextView) rootView.findViewById(R.id.favorite_tv);
		groupsTV = (TextView) rootView.findViewById(R.id.groups_tv);
		// tabs event listener
		contactsTV.setOnClickListener(this);
		recentTV.setOnClickListener(this);
		favoriteTV.setOnClickListener(this);
		groupsTV.setOnClickListener(this);

		if (ModeCheck()) {

			searchBtn.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));

			separator.setBackgroundColor(getResources().getColor(
					R.color.button_bg_day_color));

		} else {

			searchBtn.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));

			separator.setBackgroundColor(getResources().getColor(
					R.color.button_bg_night_color));

		}

		searchBtn.setOnClickListener(this);

		resources = this.getActivity().getResources();

		// contacts listView
		contactListView = (ListView) rootView
				.findViewById(R.id.contacts_listView);

		searchContactsListView = (ListView) rootView
				.findViewById(R.id.search_contacts_listView);

		// contact and status images objects are initialized here.
		imageViewRound = (ImageView) rootView.findViewById(R.id.user_imageView);
		statusChangeImage = (ImageView) rootView
				.findViewById(R.id.status_change_btn);
		Bitmap userImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.image);
		// Bitmap userStatusImage = BitmapFactory.decodeResource(getResources(),
		// R.drawable.online);
		imageViewRound.setImageBitmap(userImage);
		// statusChangeImage.setImageBitmap(userStatusImage);

		// here user status icon is set: online, away, offline
		int status_val_check = prefs.getInt(Welcome.USER_STATUS_ICON, 1);

		if (status_val_check == 0) {

			Bitmap userStatusImage = BitmapFactory.decodeResource(
					getResources(), R.drawable.offline);

			statusChangeImage.setImageBitmap(userStatusImage);

		} else if (status_val_check == 1) {

			Bitmap userStatusImage = BitmapFactory.decodeResource(
					getResources(), R.drawable.online);

			statusChangeImage.setImageBitmap(userStatusImage);

		} else if (status_val_check == 2) {

			Bitmap userStatusImage = BitmapFactory.decodeResource(
					getResources(), R.drawable.away);

			statusChangeImage.setImageBitmap(userStatusImage);

		}

		// user status icon click listener
		statusChangeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ChangeUserStatus();

			}
		});

		// user image
		imageViewRound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ChangeUserStatus();

			}
		});

		// Here arraylist of info are initialized
		contactsItems = new ArrayList<ContactItem>();
		contactsItemsFavorites = new ArrayList<ContactItem>();
		contactsItemsRecents = new ArrayList<ContactItem>();
		searchItems = new ArrayList<ContactItem>();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.cygnus.honda.blinkledcontact");
		receiver = new MyBroadcastReceiver();
		getActivity().registerReceiver(receiver, intentFilter);

		// User using the app info is filled in this function

		FillUserData();

		// ///////// search function is here ////////////

		// Capture Text in EditText
		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				if (searchBool) {
					String text = searchText.getText().toString()
							.toLowerCase(Locale.getDefault());
					contactsAdapter.filter(text, searchBtn,
							searchContactsListView);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});

		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				// HideSoftKeyBoard();

				if (pressCheck) {
					pressCheck = false;
					Bundle bundle = new Bundle();
					JSONObject jObj = null;
					try {

						// ////////////////////// check the tab value
						// /////////////
						if (LoginActivity.CHECK_CONTACT_GROUP.equals("1")) {

							ListViewSingleClick(jObj, bundle, position);

						} else if (LoginActivity.CHECK_CONTACT_GROUP
								.equals("2")) {
							jObj = jsonOuterArrayGroups.getJSONObject(position);

							String group_ID = jObj.getString("Id");

							editor.putString("refreshcontact" + group_ID, "");
							editor.commit();

							contactsAdapter = new ContactAdapter(getActivity(),
									contactsItems, prefs);
							contactListView.setAdapter(contactsAdapter);

							bundle.putString("contacts_list",
									jsonOuterArray.toString());

							bundle.putString("message", jObj.toString());

							LoginActivity.CHECK_CONTACT_GROUP = "2";
							LoginActivity.CHECK_TAB_SELECTED = "2";
							// set Fragmentclass Arguments
							MessagesFragment fragobj = new MessagesFragment();
							fragobj.setArguments(bundle);

							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.fragment_place, fragobj)
									.commit();

						} else {
							ListViewSingleClick(jObj, bundle, position);

						}

						// ///////////////////////////////////////////////////

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

				}

			}

		});

		contactListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						// TODO Auto-generated method stub

						// HideSoftKeyBoard();

						if (LoginActivity.CHECK_CONTACT_GROUP.equals("2")) {

							try {
								JSONObject jsonObj = jsonOuterArrayGroups
										.getJSONObject(position);
								GroupContactOptions(jsonObj);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {

							if (LoginActivity.check_contacts) {
								JSONObject jsonObj;
								try {
									jsonObj = jsonOuterArray
											.getJSONObject(position);
									ContactOptions(jsonObj);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else if (LoginActivity.check_favorite) {
								JSONObject jsonObj;
								try {
									jsonObj = jsonOuterArrayFavorites
											.getJSONObject(position);
									ContactOptions(jsonObj);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else if (LoginActivity.check_recent) {
								JSONObject jsonObj;
								try {
									jsonObj = jsonOuterArrayRecents
											.getJSONObject(position);
									ContactOptions(jsonObj);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}

						return false;
					}
				});

		searchContactsListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub

						// HideSoftKeyBoard();

						contactListView.setVisibility(View.VISIBLE);
						searchContactsListView.setVisibility(View.INVISIBLE);
						separatorTV.setVisibility(View.VISIBLE);
						tabScrollView.setVisibility(View.VISIBLE);

						try {
							JSONObject jsonObj = jsonOuterArrayFriends
									.getJSONObject(position);
							AddFriend(jsonObj.getString("Id"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

		if (LoginActivity.CHECK_CONTACT_GROUP.equals("1")) {
			LoadFriends();

		} else if (LoginActivity.CHECK_CONTACT_GROUP.equals("2")) {
			LoadGroups();
		} else {
			LoadFriends();
		}

		return rootView;
	}

	// ///////////// BroadCastReceiver is defined here /////////////
	private class MyBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			blinkLED2(intent);

		}
	}

	public void blinkLED2(Intent intent) {
		// TODO Auto-generated method stub

		String messageReceived = intent.getStringExtra("message_received");

		if (messageReceived.equals("")) {

			if (contactsItems.size() > 0) {

				if (LoginActivity.check_recent) {

					contactsAdapter = new ContactAdapter(getActivity(),
							contactsItemsRecents, prefs);
					contactListView.setAdapter(contactsAdapter);

				} else if (LoginActivity.check_favorite) {
					contactsAdapter = new ContactAdapter(getActivity(),
							contactsItemsFavorites, prefs);
					contactListView.setAdapter(contactsAdapter);

				} else {
					contactsAdapter = new ContactAdapter(getActivity(),
							contactsItems, prefs);
					contactListView.setAdapter(contactsAdapter);

				}

			}

		} else if (messageReceived.equals("1")) {
			check_refresh_contacts = true;

			if (LoginActivity.CHECK_CONTACT_GROUP.equals("1")) {
				LoadFriends();

			} else if (LoginActivity.CHECK_CONTACT_GROUP.equals("2")) {
				LoadGroups();
			} else {
				LoadFriends();
			}

		} else {
			final String[] separated = messageReceived.split(":");

			if (separated[0].equals("2")) {

				check_refresh_contacts = true;

				LoadFriends();

			}

		}

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	protected void HideSoftKeyBoard() {
		// TODO Auto-generated method stub

		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}

	// /////////// here checked tabs selected on listview signle click
	protected void ListViewSingleClick(JSONObject jObj, Bundle bundle,
			int position) {
		// TODO Auto-generated method stub

		LoginActivity.CHECK_TAB_SELECTED = "1";

		if (LoginActivity.check_contacts) {
			try {
				jObj = jsonOuterArray.getJSONObject(position);

				String userName = jObj.getString("Name");
				String friend_ID_Str = jObj.getString("FriendId");

				editor.putString("refreshcontact" + friend_ID_Str, "");
				editor.commit();

				contactsAdapter = new ContactAdapter(getActivity(),
						contactsItems, prefs);
				contactListView.setAdapter(contactsAdapter);

				Integer imageVal = Integer.valueOf(jObj
						.getString("OnlineStatus"));

				String mood = jObj.getString("MoodStatus");

				boolean IsConfirmed = jObj.getBoolean("IsConfirmed");

				if (mood.equals("null")) {
					mood = "";

				}

				if (IsConfirmed) {

					String friend_ID = prefs.getString("friendid"
							+ friend_ID_Str, "");

					if (friend_ID.matches("")) {
						contactsItemsRecents.add(new ContactItem(userName,
								contactImage, mood, statusImage[imageVal],
								true, true, friend_ID_Str));
						jsonOuterArrayRecents.put(jObj);

					} else {

					}
					// ///////////////////////////////////////////////////////

					bundle.putString("contacts_list", jsonOuterArray.toString());
					bundle.putString("message", jObj.toString());
					LoginActivity.CHECK_CONTACT_GROUP = "1";
					MessagesFragment fragobj = new MessagesFragment();
					fragobj.setArguments(bundle);

					FragmentTransaction importanttransaction = getFragmentManager()
							.beginTransaction();

					importanttransaction.replace(R.id.fragment_place, fragobj);

					// importanttransaction.addToBackStack(null);
					importanttransaction.commit();

					// FragmentManager fragmentManager = getFragmentManager();
					// fragmentManager.beginTransaction()
					// .replace(R.id.fragment_place, fragobj).commit();

				} else {

					bundle.putString("message", jObj.toString());
					LoginActivity.CHECK_TAB_SELECTED = "3";

					ConfrimFragment fragobj = new ConfrimFragment();
					fragobj.setArguments(bundle);
					FragmentTransaction eventtransaction = getFragmentManager()
							.beginTransaction();

					eventtransaction.replace(R.id.fragment_place, fragobj);
					// eventtransaction.addToBackStack(null);

					// Commit the transaction
					eventtransaction.commit();

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (LoginActivity.check_favorite) {
			try {
				jObj = jsonOuterArrayFavorites.getJSONObject(position);
				// ///////////////////////////////////////////////////////

				String userName = jObj.getString("Name");
				String friend_ID_Str = jObj.getString("FriendId");

				editor.putString("refreshcontact" + friend_ID_Str, "");
				editor.commit();

				contactsAdapter = new ContactAdapter(getActivity(),
						contactsItemsFavorites, prefs);
				contactListView.setAdapter(contactsAdapter);

				Integer imageVal = Integer.valueOf(jObj
						.getString("OnlineStatus"));
				// contactsItems.add(new ContactItem(userName, contactImage,
				// "", statusImage[imageVal]));

				String mood = jObj.getString("MoodStatus");

				if (mood.equals("null")) {
					mood = "";

				}

				String friend_ID = prefs.getString("friendid" + friend_ID_Str,
						"");

				if (friend_ID.matches("")) {
					contactsItemsRecents.add(new ContactItem(userName,
							contactImage, mood, statusImage[imageVal], true,
							true, friend_ID_Str));
					jsonOuterArrayRecents.put(jObj);

				} else {

				}
				// ///////////////////////////////////////////////////////

				bundle.putString("contacts_list",
						jsonOuterArrayFavorites.toString());
				bundle.putString("message", jObj.toString());
				LoginActivity.CHECK_CONTACT_GROUP = "1";
				MessagesFragment fragobj = new MessagesFragment();
				fragobj.setArguments(bundle);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.fragment_place, fragobj).commit();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (LoginActivity.check_recent) {
			try {
				jObj = jsonOuterArrayRecents.getJSONObject(position);

				String friend_ID_Str = jObj.getString("FriendId");

				editor.putString("refreshcontact" + friend_ID_Str, "");
				editor.commit();

				contactsAdapter = new ContactAdapter(getActivity(),
						contactsItemsRecents, prefs);
				contactListView.setAdapter(contactsAdapter);

				bundle.putString("contacts_list",
						jsonOuterArrayRecents.toString());
				bundle.putString("message", jObj.toString());
				LoginActivity.CHECK_CONTACT_GROUP = "1";
				MessagesFragment fragobj = new MessagesFragment();
				fragobj.setArguments(bundle);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.fragment_place, fragobj).commit();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// ///////// here long press on listview of contacts then show the options

	protected void ContactOptions(final JSONObject friendObj) {
		// TODO Auto-generated method stub

		// Strings to Show In Dialog
		final CharSequence[] profileSettingsOptions = {
				"View current location", "Remove friend from friendlist" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle("Select Options");
		builder.setSingleChoiceItems(profileSettingsOptions, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						if (item == 0) {

							Intent mapIntent = new Intent(getActivity(),
									CustomTask.class);
							mapIntent.putExtra("participants",
									friendObj.toString());
							startActivity(mapIntent);

						} else if (item == 1) {

							RemoveContacts(friendObj);

						}

						optionDialog.dismiss();
					}
				});
		optionDialog = builder.create();
		optionDialog.show();

	}

	// /////////// Remove contact function and url
	protected void RemoveContacts(JSONObject friendObj) {
		// TODO Auto-generated method stub

		try {

			String id = friendObj.getString("FriendId");

			if (CheckConnection()) {
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/RemoveFriend?UserId="
						+ userID + "&FriendId=" + id;
				dialogText = "Removing...";
				responseText = "Participant removed successfully";

				new PostData().execute();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ///////////// Here are group options when long press on groups listview
	protected void GroupContactOptions(final JSONObject friendObj) {
		// TODO Auto-generated method stub

		// Strings to Show In Dialog
		final CharSequence[] profileSettingsOptions = {
				"View vehicles locations",
				"Remove friend from group conversation" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());
		builder.setTitle("Select Options");
		builder.setSingleChoiceItems(profileSettingsOptions, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						if (item == 0) {

							Intent mapIntent = new Intent(getActivity(),
									CustomTask.class);
							mapIntent.putExtra("participants",
									friendObj.toString());
							startActivity(mapIntent);

						} else if (item == 1) {

							RemoveParticipantsInGroup(friendObj);

						}

						optionDialog.dismiss();
					}
				});
		optionDialog = builder.create();
		optionDialog.show();

	}

	// ///////// Remove participant from Group
	protected void RemoveParticipantsInGroup(JSONObject jObj) {
		// TODO Auto-generated method stub

		JSONArray participantsArray = null;
		try {
			groupID = jObj.getString("Id");
			participantsArray = jObj.getJSONArray("GroupParticipants");

			final CharSequence[] optionsStr = new String[participantsArray
					.length()];

			final String[] friendID = new String[participantsArray.length()];

			for (int i = 0; i < participantsArray.length(); i++) {

				JSONObject jsonObj;

				try {
					jsonObj = participantsArray.getJSONObject(i);
					String userName = jsonObj.getString("Name");
					String id = jsonObj.getString("Id");
					optionsStr[i] = userName;
					friendID[i] = id;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Creating and Building the Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Remove Participant from Group:");
			builder.setSingleChoiceItems(optionsStr, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String ID = friendID[item];
							if (CheckConnection()) {
								url = "http://chatter.cygnussolution.com/ChatterWcf.svc/RemoveUserFromGroup?GroupId="
										+ groupID + "&UserId=" + ID;

								dialogText = "Removing...";
								responseText = "Participant removed successfully";

								new PostData().execute();

							}

							levelDialog.dismiss();
						}
					});
			levelDialog = builder.create();
			levelDialog.show();

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// //////// Here user status is changed //////////
	protected void ChangeUserStatus() {
		// TODO Auto-generated method stub

		final CharSequence[] Options = { "Online", "Away", "Offline" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Set your status:");
		builder.setSingleChoiceItems(Options, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String statusVal = "0";
						boolean checkVal = false;

						if (item == 0) {
							statusVal = "1";
							checkVal = true;
							statusValCheck = 1;

						} else if (item == 1) {
							statusVal = "2";
							checkVal = true;
							statusValCheck = 2;
						} else if (item == 2) {
							statusVal = "0";
							checkVal = true;
							statusValCheck = 0;
						}

						if (checkVal) {

							dialogText = "Updating...";
							responseText = "Status changed successfully";

							url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SetUserOnlineStatus?UserId="
									+ userID + "&OnlineStatus=" + statusVal;
							new UserStatusChange().execute();

						}

						levelDialog.dismiss();
					}
				});
		levelDialog = builder.create();
		levelDialog.show();

	}

	// //////////// User info are filled here ///////////
	private void FillUserData() {
		// TODO Auto-generated method stub

		String user_jsonString = prefs.getString(Welcome.USER_JSON_OBJ, null);

		try {
			JSONObject jObj = new JSONObject(user_jsonString);
			String userID1 = jObj.getString(Welcome.USER_NAME);
			String userStatus1 = jObj.getString(Welcome.USER_STATUS);

			userName.setText(userID1);
			if (userStatus1.equals("null")) {
				userStatus1 = "";
			}
			userStatus.setText(userStatus1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// /////////////// click listener of tabs are implemented here . contacts,
	// recents, favorites and groups
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		// HideSoftKeyBoard();

		int viewID = view.getId();
		switch (viewID) {
		case R.id.contacts_tv:
			contactsTV.setTextColor(resources
					.getColor(R.color.button_bg_night_color));
			recentTV.setTextColor(resources.getColor(R.color.Gray));
			favoriteTV.setTextColor(resources.getColor(R.color.Gray));
			groupsTV.setTextColor(resources.getColor(R.color.Gray));
			LoginActivity.CHECK_CONTACT_GROUP = "1";
			LoginActivity.check_contacts = true;
			LoginActivity.check_favorite = false;
			LoginActivity.check_recent = false;
			LoadFriends();

			break;

		case R.id.recent_tv:
			contactsTV.setTextColor(resources.getColor(R.color.Gray));
			recentTV.setTextColor(resources
					.getColor(R.color.button_bg_night_color));
			favoriteTV.setTextColor(resources.getColor(R.color.Gray));
			groupsTV.setTextColor(resources.getColor(R.color.Gray));
			LoginActivity.CHECK_CONTACT_GROUP = "1";

			LoginActivity.check_contacts = false;
			LoginActivity.check_favorite = false;
			LoginActivity.check_recent = true;

			contactsAdapter = new ContactAdapter(getActivity(),
					contactsItemsRecents, prefs);
			contactListView.setAdapter(contactsAdapter);

			break;

		case R.id.favorite_tv:
			contactsTV.setTextColor(resources.getColor(R.color.Gray));
			recentTV.setTextColor(resources.getColor(R.color.Gray));
			favoriteTV.setTextColor(resources
					.getColor(R.color.button_bg_night_color));
			groupsTV.setTextColor(resources.getColor(R.color.Gray));
			LoginActivity.CHECK_CONTACT_GROUP = "1";
			LoginActivity.check_contacts = false;
			LoginActivity.check_favorite = true;
			LoginActivity.check_recent = false;

			contactsAdapter = new ContactAdapter(getActivity(),
					contactsItemsFavorites, prefs);
			contactListView.setAdapter(contactsAdapter);

			break;

		case R.id.groups_tv:
			contactsTV.setTextColor(resources.getColor(R.color.Gray));
			recentTV.setTextColor(resources.getColor(R.color.Gray));
			favoriteTV.setTextColor(resources.getColor(R.color.Gray));
			groupsTV.setTextColor(resources
					.getColor(R.color.button_bg_night_color));
			
			LoginActivity.check_contacts = false;
			LoginActivity.check_favorite = false;
			LoginActivity.check_recent = false;

			LoginActivity.CHECK_CONTACT_GROUP = "2";
			LoadGroups();

			break;
		case R.id.search_text:
			SearchFriendAdd();

			break;

		default:
			break;
		}

	}

	// ///////////// search friend through email id or phone no //////////
	private void SearchFriendAdd() {
		// TODO Auto-generated method stub

		String searchVal = searchText.getText().toString();

		if (searchVal.isEmpty() || searchVal.matches("")) {
			return;
		}

		searchText.setText("");

		if (searchVal.isEmpty()) {
			return;
		}

		try {

			if (CheckConnection()) {
				searchVal = URLEncoder.encode(searchVal, "UTF-8");
				url = "http://chatter.cygnussolution.com/ChatterWcf.svc/SearchUserToAdd?ApplicationId="
						+ Welcome.APPLICATION_ID
						+ "&CompanyId="
						+ Welcome.COMPANY_ID + "&Key=" + searchVal;
				new SearchFriend(getActivity()).execute();

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void LoadFriends() {
		if (CheckConnection()) {
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/LoadUserFriendList?UserId="
					+ userID;
			new GetData(getActivity()).execute();
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

	public void LoadGroups() {

		if (CheckConnection()) {
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/LoadUserGroups?UserId="
					+ userID;
			new LoadGroups(getActivity()).execute();

		}

	}

	public void AddFriend(String friendID) {
		if (CheckConnection()) {
			url = "http://chatter.cygnussolution.com/ChatterWcf.svc/AddFriend?UserId="
					+ userID + "&FriendId=" + friendID;

			dialogText = "Adding...";
			responseText = "Friend added successfully";

			new PostData().execute();

		}

	}

	// ////////////// Search friend to add /////////////

	private class SearchFriend extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		private Activity activity;

		public SearchFriend(Activity activity) {
			this.activity = activity;
			context = activity;
			dialog = new ProgressDialog(context);
		}

		/** progress dialog to show user that the backup is processing. */

		/** application context. */
		private Context context;

		protected void onPreExecute() {
			dialog.setMessage("Searching...");
			// this.dialog.setTitle ( "Please wait" ) ;
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			HideSoftKeyBoard();

			if (jsonOuterArrayFriends.length() > 0) {

				if (jsonOuterArrayFriends.length() == 1) {

					if (searchItems.isEmpty()) {
						Toast.makeText(getActivity(), "No contact found",
								Toast.LENGTH_SHORT).show();
						contactListView.setVisibility(View.VISIBLE);
						searchContactsListView.setVisibility(View.INVISIBLE);
						separatorTV.setVisibility(View.VISIBLE);
						tabScrollView.setVisibility(View.VISIBLE);

					} else {
						contactListView.setVisibility(View.INVISIBLE);
						searchContactsListView.setVisibility(View.VISIBLE);
						separatorTV.setVisibility(View.INVISIBLE);
						tabScrollView.setVisibility(View.INVISIBLE);
						searchAdapter = new ContactAdapter(getActivity(),
								searchItems, prefs);
						searchContactsListView.setAdapter(searchAdapter);

					}

				} else {

					Toast.makeText(getActivity(), "No contact found",
							Toast.LENGTH_SHORT).show();

				}

			} else {

				contactListView.setVisibility(View.VISIBLE);
				searchContactsListView.setVisibility(View.INVISIBLE);
				separatorTV.setVisibility(View.VISIBLE);
				tabScrollView.setVisibility(View.VISIBLE);

			}

		}

		protected Boolean doInBackground(final String... args) {

			JSONParser jParser = new JSONParser();
			// getting JSON string from URL
			jsonOuterArrayFriends = new JSONArray();
			jsonOuterArrayFriends = jParser.getJSONFromUrl(url);
			searchItems.clear();

			for (int i = 0; i < jsonOuterArrayFriends.length(); i++) {
				JSONObject jsonObj;

				try {
					jsonObj = jsonOuterArrayFriends.getJSONObject(i);
					String userName = jsonObj.getString("Name");

					String searchID = jsonObj.getString("Id");

					Integer imageVal = Integer.valueOf(jsonObj
							.getString("OnlineStatus"));
					if (searchID.equals(userID)) {

					} else {
						searchItems.add(new ContactItem(userName, contactImage,
								"working in cygnus", statusImage[imageVal],
								true, true, searchID));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;

		}
	}

	// ////////////////////////////

	private class GetData extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		private Activity activity;

		public GetData(Activity activity) {
			this.activity = activity;
			context = activity;
			dialog = new ProgressDialog(context);
		}

		/** progress dialog to show user that the backup is processing. */

		/** application context. */
		private Context context;

		protected void onPreExecute() {
			dialog.setMessage("Loading...");
			// this.dialog.setTitle ( "Please wait" ) ;
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			if (check_refresh_contacts) {

			} else {
				dialog.show();

			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (jsonOuterArray.length() > 0) {

				// jsonOuterArray = new JSONArray();
				jsonOuterArrayFavorites = new JSONArray();
				jsonOuterArrayRecents = new JSONArray();
				contactsItems.clear();
				contactsItemsFavorites.clear();
				contactsItemsRecents.clear();

				for (int i = 0; i < jsonOuterArray.length(); i++) {

					JSONObject jsonObj;

					try {
						jsonObj = jsonOuterArray.getJSONObject(i);
						String userName = jsonObj.getString("Name");
						String friend_ID_Str = jsonObj.getString("FriendId");
						Integer imageVal = Integer.valueOf(jsonObj
								.getString("OnlineStatus"));
						// contactsItems.add(new ContactItem(userName,
						// contactImage,
						// "", statusImage[imageVal]));

						String mood = jsonObj.getString("MoodStatus");
						boolean isFavorite = Boolean.valueOf(jsonObj
								.getString("IsFavorite"));

						boolean IsConfirmed = Boolean.valueOf(jsonObj
								.getString("IsConfirmed"));

						boolean IsRequestReciever = Boolean.valueOf(jsonObj
								.getString("IsRequestReciever"));

						if (mood.equals("null")) {
							mood = "";

						}

						if (IsConfirmed) {
							contactsItems.add(new ContactItem(userName,
									contactImage, mood, statusImage[imageVal],
									IsConfirmed, IsRequestReciever,
									friend_ID_Str));

						} else {

							contactsItems.add(new ContactItem(userName,
									contactImage, mood, statusImage[0],
									IsConfirmed, IsRequestReciever,
									friend_ID_Str));

						}

						String friend_ID = prefs.getString("friendid"
								+ friend_ID_Str, "");

						if (friend_ID.matches("")) {

						} else {
							contactsItemsRecents.add(new ContactItem(userName,
									contactImage, mood, statusImage[imageVal],
									true, true, friend_ID_Str));
							jsonOuterArrayRecents.put(jsonObj);
						}

						if (isFavorite) {
							contactsItemsFavorites.add(new ContactItem(
									userName, contactImage, mood,
									statusImage[imageVal], true, true,
									friend_ID_Str));
							jsonOuterArrayFavorites.put(jsonObj);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (check_refresh_contacts) {

					contactsAdapter.notifyAdapter1();
					check_refresh_contacts = false;

				} else {

					searchBool = true;
					pressCheck = true;
					contactsAdapter = new ContactAdapter(getActivity(),
							contactsItems, prefs);
					contactListView.setAdapter(contactsAdapter);
					check_refresh_contacts = false;

				}

			} else {
				searchBool = false;
				check_refresh_contacts = false;
			}

		}

		protected Boolean doInBackground(final String... args) {

			JSONParser jParser = new JSONParser();
			// getting JSON string from URL

			jsonOuterArray = new JSONArray();

			jsonOuterArray = jParser.getJSONFromUrl(url);

			return null;

		}
	}

	private class PostData extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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
			// if (jsonObject != null) {
			// // Wahetever you need
			//
			// }

			contactListView.setVisibility(View.VISIBLE);
			searchContactsListView.setVisibility(View.INVISIBLE);
			separatorTV.setVisibility(View.VISIBLE);
			tabScrollView.setVisibility(View.VISIBLE);

			if (LoginActivity.CHECK_CONTACT_GROUP.equals("2")) {

				LoadGroups();

			} else {

				LoadFriends();

			}

			pDialog.dismiss();
		}
	}

	private class UserStatusChange extends
			AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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

			Toast.makeText(getActivity(), responseText, Toast.LENGTH_SHORT)
					.show();

			if (statusValCheck == 0) {

				Bitmap userStatusImage = BitmapFactory.decodeResource(
						getResources(), R.drawable.offline);

				statusChangeImage.setImageBitmap(userStatusImage);

			} else if (statusValCheck == 1) {

				Bitmap userStatusImage = BitmapFactory.decodeResource(
						getResources(), R.drawable.online);

				statusChangeImage.setImageBitmap(userStatusImage);

			} else if (statusValCheck == 2) {

				Bitmap userStatusImage = BitmapFactory.decodeResource(
						getResources(), R.drawable.away);

				statusChangeImage.setImageBitmap(userStatusImage);

			}

			editor.putInt(Welcome.USER_STATUS_ICON, statusValCheck);
			editor.commit();

			pDialog.dismiss();
		}
	}

	// //////////////////// Load groups /////////////////////

	private class LoadGroups extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		private Activity activity;

		public LoadGroups(Activity activity) {
			this.activity = activity;
			context = activity;
			dialog = new ProgressDialog(context);
		}

		/** progress dialog to show user that the backup is processing. */

		/** application context. */
		private Context context;

		protected void onPreExecute() {
			dialog.setMessage("Loading...");
			// this.dialog.setTitle ( "Please wait" ) ;
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (jsonOuterArrayGroups.length() > 0) {
				pressCheck = true;
				searchBool = true;
				// contactsAdapter.clearAdapter1();
				contactsAdapter = new ContactAdapter(getActivity(),
						contactsItems, prefs);
				contactListView.setAdapter(contactsAdapter);
				// contactsAdapter.notifyAdapter1();
			} else {
				searchBool = false;

			}

		}

		protected Boolean doInBackground(final String... args) {

			JSONParser jParser = new JSONParser();
			// getting JSON string from URL
			jsonOuterArrayGroups = new JSONArray();
			contactsItems.clear();
			// contactsAdapter.clearAdapter1();
			// contactsAdapter.notifyAdapter1();

			jsonOuterArrayGroups = jParser.getJSONFromUrl(url);

			for (int i = 0; i < jsonOuterArrayGroups.length(); i++) {

				JSONObject jsonObj;

				try {
					jsonObj = jsonOuterArrayGroups.getJSONObject(i);
					String userName = jsonObj.getString("GroupName");

					String groupID = jsonObj.getString("Id");

					// Integer imageVal
					// =Integer.valueOf(jsonObj.getString("OnlineStatus"));
					contactsItems.add(new ContactItem(userName, contactImage,
							"", statusImage[1], true, true, groupID));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;

		}
	}

}
