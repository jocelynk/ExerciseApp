package com.jocelyn.exerciseapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Exercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercises;


//change to user loaders
public class WRExerciseEditActivity extends SherlockActivity {
	private static final String TAG = "WorkoutExerciseEdit";
	private static final boolean DEBUG = true;
	
	private Long wRowId;
	private Long eRowId;
	static final int CANCEL_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_exercise_to_workout);

		if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");

		Button confirmButton = (Button) findViewById(R.id.confirm);
		Button cancelButton = (Button) findViewById(R.id.cancel);

		fillSpinnerCategory();

		wRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			Bundle extras = getIntent().getExtras();
			wRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		// suggested by alex.
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				save();
				setResult(RESULT_OK);
				// pops activity off the "back stack" and destroys it. resumes the next activity on the back stack.
				finish();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(CANCEL_DIALOG_ID);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CANCEL_DIALOG_ID:
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

	private void fillSpinnerCategory() {

		String[] projection = {ExerciseTable.COLUMN_ID, ExerciseTable.COLUMN_CATEGORY};
		CursorLoader c = new CursorLoader(this, Exercises.CONTENT_URI, projection, null, null, ExerciseTable.COLUMN_CATEGORY );
		//need groupby
		Cursor cur = c.loadInBackground();
		if(cur!= null)
			cur.moveToFirst();

		// create an array to specify which fields we want to display
		String[] from = new String[] { ExerciseTable.COLUMN_CATEGORY };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };
		// create simple cursor adapter
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cur, from, to, 0);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// get reference to our spinner
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new MyOnItemSelectedListener());
		//cur.close();
	}
	
	private void fillSpinnerExercise(String category) {

		String[] projection = {ExerciseTable.COLUMN_ID, ExerciseTable.COLUMN_NAME};
		String selection = ExerciseTable.COLUMN_CATEGORY + " IS ? ";
		CursorLoader c = new CursorLoader(this, Exercises.CONTENT_URI, projection, selection, new String[] {category}, null );
		Cursor cur = c.loadInBackground();
		if(cur!= null)
			cur.moveToFirst();
		
		String[] from = new String[] { ExerciseTable.COLUMN_NAME };
		int[] to = new int[] { android.R.id.text1 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cur, from, to, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner s = (Spinner) findViewById(R.id.spinner2);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new MyOnItemSelectedListener());
		//cur.close();
	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if(parent.getId() == R.id.spinner1) {			
				Cursor c = (Cursor) parent.getAdapter().getItem(pos); 
				fillSpinnerExercise(c.getString(c.getColumnIndex(ExerciseTable.COLUMN_CATEGORY)));
			} else if(parent.getId() == R.id.spinner2) {
				eRowId = parent.getSelectedItemId();
			}
		}

	
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG) Log.v(TAG, "ON SAVED INSTANCE STATE");
		outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, wRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG) Log.v(TAG, "+ ON PAUSE +");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(DEBUG) Log.v(TAG, "+ ON RESUME +");
		fillSpinnerCategory();
	}

	private void save() {
		ContentValues values = new ContentValues();
		values.put(WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID, wRowId);
		values.put(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID, eRowId);
		getContentResolver().insert(WRExercises.CONTENT_URI, values);
		
		Context context = getApplicationContext();
		CharSequence text = "Your Exercise has been added.";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
	}
}
