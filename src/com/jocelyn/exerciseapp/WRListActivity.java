package com.jocelyn.exerciseapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.jocelyn.exerciseapp.R;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppProvider;

public class WRListActivity extends SherlockListActivity {

	private static final String TAG = "WRListActivity";
	private static final boolean DEBUG = true;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;
	private static final int ACTIVITY_EDIT = 2;

	private static final int INSERT_ID = Menu.FIRST;
	
	// suggested by alex. pass these to "add/edit" workout activity
	//   so that it will know whether to display "add workout" or
	//   "edit workout" when it is launched.
	public static final String LAUNCH_WORKOUT = "launch_workout";
	public static final String CREATE_REQUEST = "create_request";
	public static final String EDIT_REQUEST = "edit_request";

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.workouts_index);
	    Log.v(TAG, "+++ ON CREATE +++");
	    
	    getSupportActionBar().setDisplayShowTitleEnabled(false); 
	    
	    String[] projection = { WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME, WorkoutRoutineTable.COLUMN_DESCRIPTION };
	    String[] uiBindFrom = { WorkoutRoutineTable.COLUMN_NAME };
	    int[] uiBindTo = { R.id.text1 };
	 
	    Cursor workouts = managedQuery(
	            ExerciseAppProvider.CONTENT_URI, projection, null, null, null);
	 
	    CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.workouts_row, workouts,
	            uiBindFrom, uiBindTo);
	 
	    setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// suggested by alex.
		//   changed options menu to XML
		getSupportMenuInflater().inflate(R.menu.workout_index_options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// suggested by alex.
		//   changed options menu to XML
		switch (item.getItemId()) {
		case R.id.add_workout:
			createWorkout();
			return true;
		}
		return super.onOptionsItemSelected(item);
		/*int id = item.getItemId();
		if (id == R.id.add_workout) {
		    createWorkout();
		    return true;
		}*/
		
	}
	
	public void createWorkout() {
		Intent i = new Intent(this, WREditActivity.class);
		
		// suggested by alex
		i.putExtra(LAUNCH_WORKOUT, CREATE_REQUEST);
		
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, WRViewActivity.class);		
		i.putExtra(WorkoutRoutineTable.COLUMN_ID, id);
		startActivityForResult(i, ACTIVITY_VIEW);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	
	public void onDestroy() {
		Log.d(TAG, "Destroying View ...");
		//mDbAdapter.close();
		super.onDestroy();
	}
	
}
