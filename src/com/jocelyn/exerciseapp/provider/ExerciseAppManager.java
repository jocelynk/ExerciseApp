package com.jocelyn.exerciseapp.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ExerciseAppManager {
	
	//this class cannot be instantiated
	private ExerciseAppManager() {}
	
	interface WorkoutsColumns {
		String COLUMN_ID = "WorkoutRoutines._id";
		String NAME = "WorkoutRoutines.name";
		String DESCRIPTION = "WorkoutRoutines.description";
	}
	
	interface ExercisesColumns {
		
		String COLUMN_ID = "Exercises._id";
		String NAME = "Exercises.name";
		String DESCRIPTION = "Exercises.description";
		String CATEGORY = "Exercises.category";
	}
	
	interface WRExercisesColumns {
		
		String COLUMN_ID = "WorkoutRoutineExercises._id";
		String COLUMN_WORKOUT_ID = "WorkoutRoutineExercises.workout_id";
		String COLUMN_EXERCISE_ID = "WorkoutRoutineExercises.exercise_id";
	}
	
	private static final String AUTHORITY = "com.jocelyn.exerciseapp.provider";
	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	private static final String WORKOUT_PATH = "workouts";
	private static final String EXERCISES_PATH = "exercises";
	private static final String WR_EXERCISES_PATH = "wr_exercises";
	
	public static final class Workouts implements BaseColumns, WorkoutsColumns {
		
		//this class cannot be instantiated
		private Workouts() {}
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
        + "/workout_routine";
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
        + "/workout_routine";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WORKOUT_PATH).build();
		
		public static Uri buildTeamIdUri(String workoutId) {
            return CONTENT_URI.buildUpon().appendPath(workoutId).build();
        }
	}
	
	public static final class Exercises implements BaseColumns, ExercisesColumns {
		
		//this class cannot be instantiated
		private Exercises() {}
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
        + "/exercises";
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
        + "/exercises";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(EXERCISES_PATH).build();
		
		public static Uri buildTeamIdUri(String exerciseId) {
            return CONTENT_URI.buildUpon().appendPath(exerciseId).build();
        }
	}
	
public static final class WRExercises implements BaseColumns, WRExercisesColumns {
		
		//this class cannot be instantiated
		private WRExercises() {}
		
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
        + "/wrexercises";
		
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
        + "/wrexercises";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WR_EXERCISES_PATH).build();
		
		/*public static Uri buildTeamIdUri(String wrExerciseId) {
            return CONTENT_URI.buildUpon().appendPath(wrExerciseId).build();
        }*/
	}
    

}
