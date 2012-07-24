package com.jocelyn.exerciseapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;

//check previous list to see if exercises already on list? or can ppl add exercise twice?
public class ExerciseRandomTab extends SherlockFragment {
	public static final String TAG = "ExerciseRandomTab";
	public static final boolean DEBUG = true;

	private Button randomizeButton;
	private Button cancelButton;

	private EditText q1;
	private EditText c1;
	private EditText c2;
	private EditText c3;

	AlertDialog alert = null;

	private Long wRowId;
	private boolean saved = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.randomize_form, container, false);

		randomizeButton = (Button) view.findViewById(R.id.randomize);
		cancelButton = (Button) view.findViewById(R.id.cancel);
		c1 = (EditText) view.findViewById(R.id.v1);
		c2 = (EditText) view.findViewById(R.id.v2);
		c3 = (EditText) view.findViewById(R.id.v3);

		randomizeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saved = saveState();
				if (saved) {
					getActivity().setResult(FragmentActivity.RESULT_OK);
					getActivity().finish();
				}

			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				alert.show();
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.add_exercise_to_workout);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		Bundle bundle;
		if (savedInstanceState != null)
			bundle = savedInstanceState; // 1
		else if (getArguments() != null)
			bundle = getArguments(); // 2
		else
			bundle = getActivity().getIntent().getExtras();

		wRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			wRowId = bundle != null ? bundle
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want cancel?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								getActivity().finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		alert = builder.create();

	}

	private boolean saveState() {

		Context context = getActivity().getApplicationContext();
		CharSequence text;
		int duration = Toast.LENGTH_SHORT;

		Toast toast;

		String input1 = c1.getText().toString();
		String input2 = c2.getText().toString();
		String input3 = c3.getText().toString();

		if (TextUtils.isEmpty(input1)) {
			input1 = "0";
		}
		if (TextUtils.isEmpty(input2)) {
			input2 = "0";
		}
		if (TextUtils.isEmpty(input3)) {
			input3 = "0";
		}
		int value1 = Integer.parseInt(input1);
		int value2 = Integer.parseInt(input2);
		int value3 = Integer.parseInt(input3);

		if (value1 > 3 || value2 > 3 || value3 > 3) {
			text = "You have entered more exercises than we have.";
			toast = Toast.makeText(context, text, duration);
			toast.show();
			return false;
		} else if (value1 + value2 + value3 <= 0) {
			text = "Please enter more than zero exercises.";
			toast = Toast.makeText(context, text, duration);
			toast.show();
			return false;
		} else {
			// Long[] random_ids = new Long[value1];
			Random rand = new Random();
			List<Long> random_ids = new ArrayList<Long>();
			// Cardio List
			List<Long> cardio = new ArrayList<Long>();
			// Strength Training List
			List<Long> st = new ArrayList<Long>();
			// Warmup list
			List<Long> warmup = new ArrayList<Long>();
			for (int i = 1; i <= 9; i++) {
				if (i <= 3) {
					cardio.add(Long.parseLong(i + ""));
				} else if (i <= 6) {
					st.add(Long.parseLong(i + ""));
				} else {
					warmup.add(Long.parseLong(i + ""));
				}
			}

			Collections.shuffle(cardio, rand);
			Collections.shuffle(st, rand);
			Collections.shuffle(warmup, rand);

			for (int i = 0; i < value1; i++) {
				random_ids.add(warmup.get(i));
			}
			for (int i = 0; i < value2; i++) {
				random_ids.add(st.get(i));
			}
			for (int i = 0; i < value3; i++) {
				random_ids.add(cardio.get(i));
			}
			Collections.sort(random_ids);
			Collections.reverse(random_ids);

			ListIterator<Long> list = random_ids.listIterator();
			while (list.hasNext()) {

				ContentValues values = new ContentValues();
				values.put(WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID,
						wRowId);
				values.put(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID,
						list.next());
				getActivity().getContentResolver().insert(
						WRExercises.CONTENT_URI, values);

			}
			text = "Your Exercises have been saved.";
			toast = Toast.makeText(context, text, duration);

			toast.show();
			return true;
		}

		/*
		 * if (wRowId == null) { if (!TextUtils.isEmpty(name)) { ContentValues
		 * values = new ContentValues();
		 * values.put(WorkoutRoutineTable.COLUMN_NAME, name);
		 * values.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
		 * getContentResolver().insert(Workouts.CONTENT_URI, values); //long id
		 * = mDbAdapter.createWorkout(name, description); if (id > 0) { mRowId =
		 * id; } saved = true; toast.show(); } } else { if
		 * (!TextUtils.isEmpty(name)) {
		 * 
		 * ContentValues values = new ContentValues();
		 * values.put(WorkoutRoutineTable.COLUMN_NAME, name);
		 * values.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
		 * //String selection = WorkoutRoutineTable.COLUMN_ID + " = ";
		 * //String[] selectionArgs = new String[] { String.valueOf(mRowId) };
		 * getContentResolver().update(Workouts.buildWorkoutIdUri(""+mRowId),
		 * values, null, null); saved = true; toast.show(); }
		 * 
		 * 
		 * }
		 */

	}
}
