package com.jocelyn.exerciseapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExerciseDB extends SQLiteOpenHelper {

	private static final String DEBUG_TAG = "ExerciseDB";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "exercise_data";
  
 
    public ExerciseDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
 
    public void onCreate(SQLiteDatabase database) {
		WorkoutRoutineTable.onCreate(database);
		ExerciseTable.onCreate(database);
		WorkoutRoutineExerciseTable.onCreate(database);
		AttributeTable.onCreate(database);
		ExerciseAttributeTable.onCreate(database);
		RecordTable.onCreate(database);
		
	}
	
	//Method is called during an upgrade of the database,
	//e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		WorkoutRoutineTable.onUpgrade(database, oldVersion, newVersion);
		ExerciseTable.onUpgrade(database, oldVersion, newVersion);
		WorkoutRoutineExerciseTable.onUpgrade(database, oldVersion, newVersion);
		AttributeTable.onUpgrade(database, oldVersion, newVersion);
		ExerciseAttributeTable.onUpgrade(database, oldVersion, newVersion);
		RecordTable.onUpgrade(database, oldVersion, newVersion);
	}
	

}
