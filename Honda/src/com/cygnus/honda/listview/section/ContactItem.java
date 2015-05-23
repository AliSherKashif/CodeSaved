package com.cygnus.honda.listview.section;

import android.annotation.SuppressLint;
import java.util.Comparator;

import org.json.JSONObject;

@SuppressLint("DefaultLocale")
public class ContactItem {

	private String contacName;
	private String id;
	private Integer contactImage;
	private String contactStatus;
	private Integer statusImage;
	private boolean IsConfirmed;
	private boolean IsRequestReciever;

	// public final JSONObject jObj;

	public ContactItem(String contacName, Integer contactImage,
			String contactStatus, Integer statusImage, boolean IsConfirmed,
			boolean IsRequestReciever, String id) {
		this.contacName = contacName;
		this.contactImage = contactImage;
		this.contactStatus = contactStatus;
		this.statusImage = statusImage;
		this.IsConfirmed = IsConfirmed;
		this.IsRequestReciever = IsRequestReciever;
		this.id = id;
		// this.jObj = jObj;
	}

	public String getContacName() {
		return this.contacName;
	}

	public String getContactStatus() {
		return this.contactStatus;
	}

	public Integer getContactImage() {
		return this.contactImage;
	}

	public Integer getStatusImage() {
		return this.statusImage;
	}

	public boolean getIsConfirmed() {
		return this.IsConfirmed;
	}

	public boolean getIsRequestReciever() {
		return this.IsRequestReciever;
	}

	public String getID() {
		return this.id;
	}

}
