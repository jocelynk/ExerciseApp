package com.jocelyn.exerciseapp.provider;

import java.util.Arrays;

import android.content.ContentProvider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jocelyn.exerciseapp.data.ExerciseDB;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Exercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.ExercisesColumns;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercisesColumns;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WorkoutsColumns;

public class ExerciseAppProvider extends ContentProvider {
	
	public static final String TAG = "ExerciseAppProvider";
	
	private ExerciseDB mDB;
	
	private static final UriMatcher sURIMatcher;

	//Workouts Table
	public static final int WORKOUTS = 100;
	public static final int WORKOUT_ID = 101;
	
	//Exercises Table
	public static final int EXERCISES = 200;
	public static final int EXERCISE_ID = 201;
	
	//WR Exercises Table
	public static final int WR_EXERCISES = 300;
	public static final int WR_EXERCISE_ID = 301;
	
	//Workout with Exercises
	public static final int WR_ID_EXERCISES = 400;
	

	private static final boolean DEBUG = true;

	private static final int DISTINCT_EXERCISE = 500;
	
	static {
		sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getWorkoutPath() , WORKOUTS);
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getWorkoutPath() + "/#", WORKOUT_ID);
	    
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getExercisesPath() , EXERCISES);
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getExercisesPath() + "/#", EXERCISE_ID);
	    
	    //select distinct exercises
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getExercisesPath() + "/distinct",  DISTINCT_EXERCISE);
	    
	
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getWrExercisesPath() , WR_EXERCISES);
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getWrExercisesPath() + "/#", WR_EXERCISE_ID);
	    
	    //gets all Exercises of a specific Workout
	    sURIMatcher.addURI(ExerciseAppManager.getAuthority(), ExerciseAppManager.getWorkoutPath() + "/#/" +  ExerciseAppManager.getExercisesPath(),  WR_ID_EXERCISES);

	}
	//need to think about uri for joining tables
	
	@Override
    public boolean onCreate() {
		mDB = new ExerciseDB(getContext());
        return true;
    }
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
	    case WORKOUTS:
	    	return Workouts.CONTENT_TYPE;
	    case WORKOUT_ID:
	        return Workouts.CONTENT_ITEM_TYPE;
	    case WR_ID_EXERCISES:
	    	return WRExercises.CONTENT_TYPE;
	    	
	    case EXERCISES:
	    	return Exercises.CONTENT_TYPE;
	    case EXERCISE_ID:
	        return Exercises.CONTENT_ITEM_TYPE;
	    case WR_EXERCISES:
	    	return WRExercises.CONTENT_TYPE; //need to think about uri for joining tables*/
	    case WR_EXERCISE_ID:
	        return WRExercises.CONTENT_ITEM_TYPE;
	    default:
	        throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (DEBUG) Log.v(TAG, "delete(uri=" + uri + ")");
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = mDB.getWritableDatabase();
	    int rowsAffected = 0;
	    String id;
	    
	    switch (uriType) {
	    case WORKOUTS:
	        rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	                selection, selectionArgs);
	        break;
	    case WORKOUT_ID:
	        id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	            		WorkoutRoutineTable.COLUMN_ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	                    selection + " and " + WorkoutRoutineTable.COLUMN_ID + "=" + id,
	                    selectionArgs);
	        }
	        break;
	   /* case WR_EXERCISES:
	        rowsAffected = sqlDB.delete(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE,
	                selection, selectionArgs);
	        break;*/
	    case WR_EXERCISE_ID:
	        id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE,
	            		WorkoutRoutineExerciseTable.COLUMN_ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE,
	                    selection + " and " + WorkoutRoutineExerciseTable.COLUMN_ID + "=" + id,
	                    selectionArgs);
	        }
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsAffected;
	}
	

	@Override
	//equivalent to createWorkout method below
	public Uri insert(Uri uri, ContentValues values) {
		if (DEBUG) Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
		long rowId; 
		Uri newUri;
		
		
		switch (sURIMatcher.match(uri)) {
		case WORKOUTS:
			rowId = mDB.getWritableDatabase().insert(WorkoutRoutineTable.TABLE_WORKOUTROUTINE , "", values);
        if (rowId > 0) {
        	newUri = Workouts.buildWorkoutIdUri(""+rowId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
		case WR_EXERCISES:
			rowId = mDB.getWritableDatabase().insert(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE , "", values);
        if (rowId > 0) {
        	newUri = WRExercises.buildWRExerciseIdUri(""+rowId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
		default: 
			throw new SQLException("Failed to insert row into " + uri);
        }
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (DEBUG) {
	         Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) +
	         ", selection=" + selection + ", selectionArgs=" + Arrays.toString(selectionArgs) +
	         ", sortOrder=" + sortOrder + ")");
	        }
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	    qb.setTables(WorkoutRoutineTable.TABLE_WORKOUTROUTINE);
	    String groupBy = null;
	    boolean useDistinct = false;

	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case WORKOUT_ID:
		    qb.setTables(WorkoutRoutineTable.TABLE_WORKOUTROUTINE);
	        qb.appendWhere(WorkoutRoutineTable.COLUMN_ID + "=" + uri.getLastPathSegment());
	        break;
	    case WORKOUTS:
		    qb.setTables(WorkoutRoutineTable.TABLE_WORKOUTROUTINE);
	        break;
	    case WR_EXERCISE_ID:
	    	qb.setTables(ExerciseTable.TABLE_EXERCISE + ", " + WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE);
	    	qb.appendWhere(WRExercisesColumns.COLUMN_EXERCISE_ID 
	    			+ " = " + ExercisesColumns.COLUMN_ID 
	    			+ " AND " + WRExercisesColumns.COLUMN_ID 
	    			+ " = " + uri.getLastPathSegment());	
	    	break;
	    case WR_ID_EXERCISES:
	    	qb.setTables(WorkoutRoutineTable.TABLE_WORKOUTROUTINE + ", " +  ExerciseTable.TABLE_EXERCISE + ", " +  WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE);
	    	qb.appendWhere(WorkoutsColumns.COLUMN_ID
	    			+ " = " + WRExercisesColumns.COLUMN_WORKOUT_ID 
	    			+ " AND " +  WRExercisesColumns.COLUMN_EXERCISE_ID 
	    			+ " = " + ExercisesColumns.COLUMN_ID 
	    			+ " AND " + WRExercisesColumns.COLUMN_WORKOUT_ID 
	    			+ " = " + uri.getPathSegments().get(1));
	    	break;
	    	//does it make a difference if search from workout table or wrexercise table?
	    	
	    case EXERCISE_ID:
		    qb.setTables(ExerciseTable.TABLE_EXERCISE);
	        qb.appendWhere(ExerciseTable.COLUMN_ID + "=" + uri.getLastPathSegment());
	        break;
	    case EXERCISES:
		    qb.setTables(ExerciseTable.TABLE_EXERCISE);
	        break;
	    case DISTINCT_EXERCISE:
	    	useDistinct = true;
	    	groupBy = ExerciseTable.COLUMN_CATEGORY;
	    	qb.setTables(ExerciseTable.TABLE_EXERCISE);
	    	break;
	   /* case WR_EXERCISE_ID:
		    qb.setTables(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE);
	        qb.appendWhere(WorkoutRoutineExerciseTable.COLUMN_ID + "=" + uri.getLastPathSegment());
	        
	        break;
	    case WR_EXERCISES:
		    qb.setTables(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE);
	        break;*/
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
	    Log.v(TAG, "asdf" + useDistinct);
	    qb.setDistinct(useDistinct);
	    Cursor cursor = qb.query(mDB.getReadableDatabase(),
	            projection, selection, selectionArgs, groupBy, null, sortOrder);
	    if (cursor != null)
	    	cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}
	

	

	@Override
	   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	   {
		if (DEBUG) Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		
	      int count = 0;
	      switch (sURIMatcher.match(uri)){
	      case WORKOUTS:
	    	  count = mDB.getWritableDatabase().update(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, values, selection, selectionArgs);
	          break;
	      case WORKOUT_ID:  	
	         count = mDB.getWritableDatabase().update(
	        		  WorkoutRoutineTable.TABLE_WORKOUTROUTINE, 
	        		  values,
	        		  WorkoutRoutineTable.COLUMN_ID + " = " + uri.getLastPathSegment()
	        		  + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), 
	        		  selectionArgs);
	          break;
	      case WR_EXERCISES:
	    	  count = mDB.getWritableDatabase().update(WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE, values, selection, selectionArgs);
	          break;
	      case WR_EXERCISE_ID:                
	          count = mDB.getWritableDatabase().update(
	        		  WorkoutRoutineExerciseTable.TABLE_WORKOUTROUTINE_EXERCISE, 
	        		  values,
	        		  WorkoutRoutineExerciseTable.COLUMN_ID + " = " + uri.getLastPathSegment()
	        		  + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), 
	        		  selectionArgs);
	          break;
	            
	         default: throw new IllegalArgumentException(
	            "Unknown URI " + uri);    
	      }       
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	   }

}
