package com.jocelyn.exerciseapp.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class RecordTable {
	//Database table
	public static final String TABLE_RECORD = "Records";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_WRKT_RTNE_E_ID = "workout_exercise_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_VALUE1 = "value1";
	public static final String COLUMN_E_ATTR_ID1 = "exercise_attribute_id1";
	public static final String COLUMN_VALUE2 = "value2";
	public static final String COLUMN_E_ATTR_ID2 = "exercise_attribute_id2";
	public static final String COLUMN_VALUE3 = "value3";
	public static final String COLUMN_E_ATTR_ID3 = "exercise_attribute_id3";
	public static final String COLUMN_TIME = "time";
	
	//Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
		+ TABLE_RECORD
		+ "("
		+ COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_DATE + " text not null, "
		+ COLUMN_DESCRIPTION + " text null, "
		+ COLUMN_TIME + " real not null, "
		+ COLUMN_WRKT_RTNE_E_ID + " integer not null REFERENCES " 
		+ WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE 
		+ "("+ WorkoutRoutineExerciseTable.COLUMN_ID + "), "
		+ COLUMN_VALUE1 + " double null, "
		+ COLUMN_E_ATTR_ID1 + " integer null REFERENCES " 
		+ ExerciseAttributeTable.TABLE_EXERCISE_ATTR
		+ "("+ ExerciseAttributeTable.COLUMN_ID + "), "
		+ COLUMN_VALUE2 + " double null, "
		+ COLUMN_E_ATTR_ID2 + " integer null REFERENCES " 
		+ ExerciseAttributeTable.TABLE_EXERCISE_ATTR
		+ "("+ ExerciseAttributeTable.COLUMN_ID + "), "
		+ COLUMN_VALUE3 + " double null, "
		+ COLUMN_E_ATTR_ID3 + " integer null REFERENCES " 
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
