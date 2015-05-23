package com.cygnus.honda.listview.section;

public class MessageSectionItem implements Message{
	
	 private final String title;
	  
	 public MessageSectionItem(String title) {
	  this.title = title;
	 }
	  
	 public String getTitle(){
	  return title;
	 }
 
	 @Override
	 public boolean isSection() {
	  return true;
	 }

}
