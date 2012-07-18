package com.jocelyn.exerciseapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;

public class ExerciseRandomTab extends SherlockFragment {
	public static final String TAG = "ExerciseRandomTab";
	public static final boolean DEBUG = true;
	
	private Button randomizeButton;
	private Button cancelButton;
	
	private Long wRowId;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.randomize_form, container, false);
		
		randomizeButton = (Button) view.findViewById(R.id.randomize);
		cancelButton = (Button) view.findViewById(R.id.cancel);
		
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
}
