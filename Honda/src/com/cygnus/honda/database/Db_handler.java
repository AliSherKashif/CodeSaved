package com.cygnus.honda.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db_handler extends SQLiteOpenHelper {
	public Db_handler(Context context)
	{
		super(context, "friendsMessage.db",null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sql="create table friendsMessage (id integer primary key autoincrement, " + "friendID text  , MessageTo text ,MessageFrom text,MessageText text,MessageTime text,MessageStatus text);";
		//		String sql="create table Eschedule (id integer primary key autoincrement, " + "category text  , title text ,calendar text,time text,location text,phone text,address text);";
        arg0.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	    if (arg1 == 1 && arg2 == 2) {
	        String sql = "drop table Students;";
	        arg0.execSQL(sql);
	       sql= "create table Students (roll_no integer primary key autoincrement, " + "name text not null ,fname text not null, uni text not null);";
	        arg0.execSQL(sql);
	    }
	}
}


