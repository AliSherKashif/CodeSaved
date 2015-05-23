package com.cygnus.honda.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import com.cygnus.honda.listview.section.Message;
import com.cygnus.honda.listview.section.MessageItem;

public class DataLayer {
	private Db_handler dbHelper;

	public DataLayer(Context c) {
		dbHelper = new Db_handler(c);
	}

	public long AddMessages(String friendID, String MessageTo,
			String MessageFrom, String MessageText, String MessageTime,
			String MessageStatus) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("friendID", friendID);
			values.put("MessageTo", MessageTo);
			values.put("MessageFrom", MessageFrom);
			values.put("MessageText", MessageText);
			values.put("MessageTime", MessageTime);
			values.put("MessageStatus", MessageStatus);
			return db.insert("friendsMessage", "", values);
		} finally {
			if (db != null)
				db.close();
		}
	}

	public ArrayList<Message> GetMessages(String userID, String friendID,
			ArrayList<Message> messageItems) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor c = db.query("friendsMessage", new String[] { "friendID",
					"MessageTo", "MessageFrom", "MessageText", "MessageTime",
					"MessageStatus" }, null, null, null, null, null);
			if (c.getCount() > 0) {
				// c.moveToFirst();
				while (c.moveToNext()) {

					String friendIDDB = c.getString(c
							.getColumnIndex("friendID"));

					if (friendID.equals(friendIDDB)) {

						String messageTo = c.getString(c
								.getColumnIndex("MessageTo"));
						String messageFrom = c.getString(c
								.getColumnIndex("MessageFrom"));
						String messageText = c.getString(c
								.getColumnIndex("MessageText"));
						String messageTime = c.getString(c
								.getColumnIndex("MessageTime"));

						if (messageFrom.equals(userID)) {
							messageItems.add(new MessageItem(messageText,
									messageTime, false));

						} else {
							messageItems.add(new MessageItem(messageText,
									messageTime, true));

						}
					}

				}
			}
		} finally {
			if (db != null)
				db.close();
		}

		return messageItems;
	}

	public void Update_Record(String id, String category, String title,
			String calendar, String alarm, String starttimevalue,
			String endtimevalue, String phoneval, String addressvalue) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.put("category", category);
			values.put("title", title);
			values.put("calendar", calendar);
			values.put("alarm", alarm);
			values.put("starttime", starttimevalue);
			values.put("endtime", endtimevalue);
			values.put("phone", phoneval);
			values.put("address", addressvalue);
			// values.put("location", location);
			db.update("Eschedule", values, "id " + "=" + id, null);

		} finally {
			if (db != null)
				db.close();
		}
	}

	public int DeleteMessages(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			int recordsDeleted = db.delete("friendsMessage", "friendID=" + id,
					null);
			return recordsDeleted;
		} finally {
			if (db != null)
				db.close();
		}
	}

	public int DeleteAllRecords() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			int dbs = db.delete("friendsMessage", null, null);
			db.close();
			return dbs;
		} finally {
			if (db != null)
				db.close();
		}
	}

}
