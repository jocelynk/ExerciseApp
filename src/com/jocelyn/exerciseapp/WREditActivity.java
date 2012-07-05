package com.jocelyn.exerciseapp;

import com.actionbarsherlock.app.SherlockActivity;
import com.jocelyn.exerciseapp.data.ExerciseDB;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class WREditActivity extends SherlockActivity {
	
	private static final String TAG = "WorkoutRoutineEdit";
	private static final boolean DEBUG = true;

	private ExerciseDB mDB;
	private EditText mNameText;
	private EditText mDescriptionText;
	private Long mRowId;
	private Boolean saved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_edit);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		mNameText = (EditText) findViewById(R.id.name);
		mDescriptionText = (EditText) findViewById(R.id.description);

		Button confirmButton = (Button) findViewById(R.id.confirm);
		Button cancelButton = (Button) findViewById(R.id.cancel);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);

		if (mRowId == null) {
			// suggested by alex
			mRowId = getIntent()
					.getLongExtra(WorkoutRoutineTable.COLUMN_ID, -1);
			if (mRowId == -1)
				mRowId = null;
		}

		// suggeseted by alex.
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		// suggested by alex
		String title = getIntent().getStringExtra(WRListActivity.LAUNCH_WORKOUT);
		if (title.equals(WRListActivity.CREATE_REQUEST)) {
			// then the activity was launched to "add a workout"
			getSupportActionBar().setTitle(R.string.workout_routine_add_title);
		} else {
			// activity was launched to "edit a workout"
			getSupportActionBar().setTitle(R.string.workout_routine_edit_title);
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();

				if(saved) {
					setResult(RESULT_OK);
					finish();
				} else
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Please fill in fields.", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				
				
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(0);
			}
		});
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to cancel?")
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		}
		return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG)
			Log.v(TAG, "ON SAVED INSTANCE STATE");
		saveState();
		outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG)
			Log.v(TAG, "+ ON PAUSE +");
		// saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
		populateFields();
	}

	private void populateFields() {
		if (mRowId != null) {
			//query from provider here
			Cursor workout = mDbAdapter.fetchWorkout(mRowId);
			startManagingCursor(workout);
			mNameText.setText(workout.getString(workout
					.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
			mDescriptionText
					.setText(workout.getString(workout
							.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
		}
	}

	private void saveState() {
		String name = mNameText.getText().toString();
		String description = mDescriptionText.getText().toString();

		Context context = getApplicationContext();
		CharSequence text = "Your Workout Routine has been saved.";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);

		if (mRowId == null) {
			if (!TextUtils.isEmpty(name)) {
				ContentValues values = new ContentValues();
				values.put(WorkoutRoutineTable.COLUMN_NAME, name);
				values.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
		        Uri uri = getContentResolver().insert(ExerciseAppProvider.CONTENT_URI, values);
				//long id = mDbAdapter.createWorkout(name, description);
				/*if (id > 0) {
					mRowId = id;
				}
				toast.show();
				saved = true;*/
		        toast.show();
			}
		} else  {
			if (!TextUtils.isEmpty(name)) {
				//need to create update in ExerciseAppProvider
			mDbAdapter.updateWorkout(mRowId, name, description);
			toast.show();
			saved = true;
			}

		}

	}

}
