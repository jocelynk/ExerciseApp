package com.jocelyn.exerciseapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;


public class WRListActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "WRListActivity";
	private static final boolean DEBUG = true;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_VIEW = 1;
	private static final int ACTIVITY_EDIT = 2;
	private static final int WR_LIST_LOADER = 0x01;
	//private static final int INSERT_ID = Menu.FIRST;
	
	private SimpleCursorAdapter adapter;
	
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
	    
	    //String[] projection = { WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME, WorkoutRoutineTable.COLUMN_DESCRIPTION };
	    String[] uiBindFrom = { WorkoutRoutineTable.COLUMN_NAME };
	    int[] uiBindTo = { R.id.text1 };
	    
	    adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.workouts_row, null, uiBindFrom, uiBindTo,0);
	    
	    //Cursor workouts = managedQuery(
	      //      ExerciseAppProvider.CONTENT_URI, projection, null, null, null);
	 
	   // CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.workouts_row, workouts,
	     //       uiBindFrom, uiBindTo);
	 
	    final Intent i = new Intent(this, WRViewActivity.class);		
	    final ListView lv = (ListView) findViewById(android.R.id.list);
	    
		lv.setAdapter(adapter);
		lv.setEmptyView(findViewById(android.R.id.empty));

		lv.setClickable(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		      // Object o = lv.getItemAtPosition(position);
				i.putExtra(WorkoutRoutineTable.COLUMN_ID, id);
				startActivityForResult(i, ACTIVITY_VIEW);
		    }
		 });

		   
		   
	    //setListAdapter(adapter);
	    getSupportLoaderManager().initLoader(WR_LIST_LOADER, null, this);
	    registerForContextMenu(lv);
	    //registerForContextMenu(getListView());
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
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.workoutroutine_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		//Cursor c = mDbAdapter.fetchWRE(info.id);

		switch (item.getItemId()) {
		case R.id.menu_delete:
			getContentResolver().delete(Workouts.buildWorkoutIdUri(""+info.id), null, null);
			
			//mDbAdapter.deleteRecordByWR(info.id);
			//mDbAdapter.deleteWorkoutExercise(info.id);
			//mDbAdapter.deleteWorkout(info.id);

			/*
			 * while (c.isAfterLast() == false) { Log.v(TAG, "Infinite loop");
			 * long rowId = c.getLong(c
			 * .getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_ID));
			 * mDbAdapter.deleteRecordsByWRE(rowId); }
			 */
			//fillData();
			return true;
		case R.id.menu_edit:
			Intent j = new Intent(this, WREditActivity.class);
			j.putExtra(WorkoutRoutineTable.COLUMN_ID, info.id);
			
			// suggested by alex
			j.putExtra(LAUNCH_WORKOUT, EDIT_REQUEST);
			
			startActivityForResult(j, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	public void createWorkout() {
		Intent i = new Intent(this, WREditActivity.class);
		
		// suggested by alex
		i.putExtra(LAUNCH_WORKOUT, CREATE_REQUEST);
		
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	/*
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, WRViewActivity.class);		
		i.putExtra(WorkoutRoutineTable.COLUMN_ID, id);
		startActivityForResult(i, ACTIVITY_VIEW);
	}*/


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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { WorkoutRoutineTable.COLUMN_ID, WorkoutRoutineTable.COLUMN_NAME, WorkoutRoutineTable.COLUMN_DESCRIPTION };	 
	    CursorLoader cursorLoader = new CursorLoader(this,
	            Workouts.CONTENT_URI, projection, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	    adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	    adapter.swapCursor(null);
	}
	
}
