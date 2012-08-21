package com.jocelyn.exerciseapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class RecordTable {
	//Database table
	public static final String TABLE_RECORD = "Records";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_WRKT_RTNE_E_ID = "workout_exercise_id";
	public static final String COLUMN_E_ATTR_ID = "exercise_attribute_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_TIME = "time";
	
	//Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
		+ TABLE_RECORD
		+ "("
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_DATE + " text not null, "
		+ COLUMN_VALUE + " double not null, "
		+ COLUMN_DESCRIPTION + " text null, "
		+ COLUMN_TIME + " real not null, "
		+ COLUMN_WRKT_RTNE_E_ID + " integer not null REFERENCES " 
		+ WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE 
		+ "("+ WorkoutRoutineExerciseTable.COLUMN_ID + "), "
		+ COLUMN_E_ATTR_ID + " integer not null REFERENCES " 
		+ ExerciseAttributeTable.TABLE_EXERCISE_ATTR
		+ "("+ ExerciseAttributeTable.COLUMN_ID + "));";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(RecordTable.class.getName(), "Upgrading database from version"
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS" + TABLE_RECORD);
		onCreate(database);
	}
		
}
