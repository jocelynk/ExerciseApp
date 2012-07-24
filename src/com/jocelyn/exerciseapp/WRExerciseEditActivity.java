package com.jocelyn.exerciseapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;


//change to user loaders
public class WRExerciseEditActivity extends SherlockFragmentActivity {
	private static final String TAG = "WRExerciseEditActivity";
	private static final boolean DEBUG = true;
	
	private Long wRowId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUG) Log.d(TAG, "++ON CREATE++");
        // Notice how there is not very much code in the Activity. All the business logic for
        // rendering tab content and even the logic for switching between tabs has been pushed
        // into the Fragments. This is one example of how to organize your Activities with Fragments.
        // This benefit of this approach is that the Activity can be reorganized using layouts
        // for different devices and screen resolutions.
        setContentView(R.layout.exercise_fragment_tab);
        
        wRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(WorkoutRoutineTable.COLUMN_ID);
		if (wRowId == null) {
			Bundle extras = getIntent().getExtras();
			wRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}
		
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
        
        
        // Grab the instance of TabFragment that was included with the layout and have it
        // launch the initial tab.
        FragmentManager fm = getSupportFragmentManager();
        ExerciseTabFragment tabFragment = (ExerciseTabFragment) fm.findFragmentById(R.id.fragment_tab);
        tabFragment.gotoListView();
    }
	
	
}
