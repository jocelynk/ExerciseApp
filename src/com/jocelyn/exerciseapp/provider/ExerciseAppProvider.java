package com.jocelyn.exerciseapp.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.jocelyn.exerciseapp.data.ExerciseDB;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;

public class ExerciseAppProvider extends ContentProvider {
	
	private static final String AUTHORITY = "com.jocelyn.exerciseapp.provider";
	public static final int WORKOUTS = 100;
	public static final int WORKOUT_ID = 110;
	
	private static final String WORKOUT_BASE_PATH = "workouts";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	        + "/" + WORKOUT_BASE_PATH);
	//for other tables, need to create new content_URI, or see how more advanced content providers do it, do they create interface?
	// or put in separate class constructor
	
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	        + "/workout_routine";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	        + "/workout_routine";
	
	private ExerciseDB mDB;

	private static final UriMatcher sURIMatcher = new UriMatcher(
	        UriMatcher.NO_MATCH);
	static {
	    sURIMatcher.addURI(AUTHORITY, WORKOUT_BASE_PATH , WORKOUTS);
	    sURIMatcher.addURI(AUTHORITY, WORKOUT_BASE_PATH + "/#", WORKOUT_ID);
	}

	
	@Override
    public boolean onCreate() {
		mDB = new ExerciseDB(getContext());
        return true;
    }
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	        String[] selectionArgs, String sortOrder) {
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(WorkoutRoutineTable.TABLE_WORKOUTROUTINE);
	 
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case WORKOUT_ID:
	        queryBuilder.appendWhere(WorkoutRoutineTable.COLUMN_ID + "="
	                + uri.getLastPathSegment());
	        break;
	    case WORKOUTS:
	        // no filter
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
	 
	    Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
	            projection, selection, selectionArgs, null, null, sortOrder);
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = mDB.getWritableDatabase();
	    int rowsAffected = 0;
	    switch (uriType) {
	    case WORKOUTS:
	        rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	                selection, selectionArgs);
	        break;
	    case WORKOUT_ID:
	        String id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	            		WorkoutRoutineTable.COLUMN_ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(WorkoutRoutineTable.TABLE_WORKOUTROUTINE,
	                    selection + " and " + WorkoutRoutineTable.COLUMN_ID + "=" + id,
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
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
	    case WORKOUTS:
	    	return CONTENT_TYPE;
	    case WORKOUT_ID:
	        return CONTENT_ITEM_TYPE;
	    default:
	        throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	}

	@Override
	//equivalent to createWorkout method below
	public Uri insert(Uri uri, ContentValues values) {
		switch (sURIMatcher.match(uri)) {
		case WORKOUTS:
			long rowID = mDB.getWritableDatabase().insert(WorkoutRoutineTable.TABLE_WORKOUTROUTINE , "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
		default: 
            throw new UnsupportedOperationException("URI: " + uri + " not supported.");
        }
	}
	/*
	public long createWorkout(String name, String description) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(WorkoutRoutineTable.COLUMN_NAME, name);
		initialValues.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);

		return mDb.insert(WorkoutRoutineTable.TABLE_WORKOUTROUTINE, null,
				initialValues);
	}*/


	

	@Override
	   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	   {
	      int count = 0;
	      switch (sURIMatcher.match(uri)){
	         case WORKOUTS:
	            count = mDB.getWritableDatabase().update(
	            		WorkoutRoutineTable.TABLE_WORKOUTROUTINE, 
	            		values, 
	            		selection, 
	            		selectionArgs);
	            break;
	         case WORKOUT_ID:                
	            count = mDB.getWritableDatabase().update(
	            		WorkoutRoutineTable.TABLE_WORKOUTROUTINE, 
	            		values,
	            		WorkoutRoutineTable.COLUMN_ID + " = " + uri.getLastPathSegment()
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
