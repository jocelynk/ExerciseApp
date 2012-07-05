package com.jocelyn.exerciseapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ExerciseAttributeTable {
	//Database table
	public static final String TABLE_EXERCISE_ATTR = "ExerciseAttributes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_EXERCISE_ID = "exercise_id";
	public static final String COLUMN_ATTR_ID = "attribute_id";
	
	//Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
		+ TABLE_EXERCISE_ATTR
		+ "("
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_ATTR_ID + " integer not null REFERENCES " 
		+ AttributeTable.TABLE_ATTR 
		+ "("+ AttributeTable.COLUMN_ID + "), "
		+ COLUMN_EXERCISE_ID + " integer not null REFERENCES " 
		+ ExerciseTable.TABLE_EXERCISE
		+ "("+ ExerciseTable.COLUMN_ID + "));";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ExerciseAttributeTable.class.getName(), "Upgrading database from version"
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS" + TABLE_EXERCISE_ATTR);
		onCreate(database);
	}
		
}
