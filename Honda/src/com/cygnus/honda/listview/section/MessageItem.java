package com.cygnus.honda.listview.section;

public class MessageItem implements Message{
	
	public final String message;
	public final String date;
	public final boolean checkVal;
	
	//public final JSONObject jObj;
	

	public MessageItem(String message, String date, boolean check) {
		this.message = message;
		this.date = date;
		this.checkVal = check;
		
		//this.jObj = jObj;
		
	}

	@Override
	public boolean isSection() {
		return false;
	}

}
