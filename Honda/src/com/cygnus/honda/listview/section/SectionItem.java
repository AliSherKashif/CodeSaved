package com.cygnus.honda.listview.section;

public class SectionItem implements Contact{
	 
	 private final String title;
	  
	 public SectionItem(String title) {
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
