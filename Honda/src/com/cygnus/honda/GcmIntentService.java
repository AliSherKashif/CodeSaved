/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cygnus.honda;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

	public static SharedPreferences.Editor editor;
	SharedPreferences prefs;
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
		editor = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE)
				.edit();

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		Log.i("Notification received", "Thanks");

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle

			// Post notification of received message.
			// sendNotification("Received: " + extras.toString());
			String[] separated = extras.getString("message").split(":");
			if (separated[0].equals("0")) {

				if (separated.length == 3) {

					editor.putString("refreshcontact" + separated[1], separated[1]);

					editor.commit();

					sendNotification(separated[2]);

				} else {

					editor.putString("refreshcontact" + separated[1], separated[1]);
					editor.commit();

					sendNotification(separated[2] + " : " + separated[3]);
				}

				Intent intent1 = new Intent();
				intent1.putExtra("message_received",
						extras.getString("message"));
				intent1.setAction("com.cygnus.honda.blinkled");
				sendBroadcast(intent1);

			} else if (separated[0].equals("2")) {
				// sendNotification(extras.getString("message"));

				LoginActivity.checkNotifyCountReceive = true;
				Intent intent1 = new Intent();
				intent1.putExtra("message_received",
						extras.getString("message"));
				intent1.setAction("com.cygnus.honda.blinkledcontact");
				sendBroadcast(intent1);

				if (separated.length == 3) {
					sendNotification(separated[2]);

				}

			}

			else {
				sendNotification(extras.getString("message"));

			}

			LoginActivity.checkNotifyCountReceive = true;

			Log.i(TAG, "Received: " + extras.toString());
		}

		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// LoginActivity.CHECK_CONTACT_GROUP = "3";
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, LoginActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg).setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
