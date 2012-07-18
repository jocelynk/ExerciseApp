package com.jocelyn.exerciseapp;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
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
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;

public class ExerciseRandomTab extends SherlockFragment {
	public static final String TAG = "ExerciseRandomTab";
	public static final boolean DEBUG = true;
	
	private Button randomizeButton;
	private Button cancelButton;
	
	private EditText q1;
	private EditText c1;
	private EditText c2;
	private EditText c3;
	
	private Long wRowId;
	private boolean saved = false;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.randomize_form, container, false);
		
		randomizeButton = (Button) view.findViewById(R.id.randomize);
		cancelButton = (Button) view.findViewById(R.id.cancel);
		q1 = (EditText) view.findViewById(R.id.v1);
		c1 = (EditText) view.findViewById(R.id.v2);
		c2 = (EditText) view.findViewById(R.id.v3);
		c3 = (EditText) view.findViewById(R.id.v4);
		
		randomizeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();
				
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.add_exercise_to_workout);

		if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
	
		Bundle bundle;
	    if(savedInstanceState != null)  bundle = savedInstanceState; // 1       
	    else if(getArguments() != null) bundle = getArguments();     // 2
	    else                            bundle = getActivity().getIntent().getExtras();

		wRowId = (bundle == null) ? null
				: (Long) bundle
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			wRowId = bundle != null ? bundle
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		// suggested by alex.
		
	}
	
	private void saveState() {
		int value1 = Integer.parseInt(q1.getText().toString());
		int value2 = Integer.parseInt(c1.getText().toString());
		int value3 = Integer.parseInt(c2.getText().toString());
		int value4 = Integer.parseInt(c3.getText().toString());

		//Long[] random_ids = new Long[value1];
		List<Long> random_ids = new ArrayList<Long>();
		Random rand = new Random();
		int random_id = -1;
		
		int sum_category_values = value2+value3+value4;
		if(value1 > 0){
			if(value1 == sum_category_values) {
				for(int i = 0; i<value2; i++) {
					random_id = rand.nextInt(3 - 1 + 1) + 1;
					random_ids.add(Long.parseLong(random_id + ""));
				}
				for(int i = 0; i<value3; i++) {
					random_id = rand.nextInt(6 - 4 + 1) + 4;
					random_ids.add(Long.parseLong(random_id + ""));
				}
				for(int i = 0; i<value4; i++) {
					random_id = rand.nextInt(9 - 7 + 1) + 7;
					random_ids.add(Long.parseLong(random_id + ""));
				}
			}
			
		}
		String test = "";
		ListIterator<Long> list =  random_ids.listIterator();
		if(!random_ids.isEmpty()) {
			while(list.hasNext()) {
				test += list.next().toString();
			}
		}
		Log.d(TAG, "list of ids: " + test);
		
		
		Context context = getActivity().getApplicationContext();
		CharSequence text = "Your Exercises have been saved.";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);

		/*if (wRowId == null) {
			if (!TextUtils.isEmpty(name)) {
				ContentValues values = new ContentValues();
				values.put(WorkoutRoutineTable.COLUMN_NAME, name);
				values.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
		        getContentResolver().insert(Workouts.CONTENT_URI, values);
				//long id = mDbAdapter.createWorkout(name, description);
				if (id > 0) {
					mRowId = id;
				}
		        saved = true;
		        toast.show();
			}
		} else  {
			if (!TextUtils.isEmpty(name)) {
								
				ContentValues values = new ContentValues();
				values.put(WorkoutRoutineTable.COLUMN_NAME, name);
				values.put(WorkoutRoutineTable.COLUMN_DESCRIPTION, description);
				//String selection = WorkoutRoutineTable.COLUMN_ID + " = ";
				//String[] selectionArgs = new String[] { String.valueOf(mRowId) };
				getContentResolver().update(Workouts.buildWorkoutIdUri(""+mRowId), values, null, null);
				saved = true;
				toast.show();
			}
			

		}*/

	}
}
