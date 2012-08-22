package com.jocelyn.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.data.RecordTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.ExercisesColumns;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Records;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.RecordsColumns;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercisesColumns;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;

public class WRViewActivity extends SherlockFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "WRViewActivity";
	private static final boolean DEBUG = true;
	private static final int WR_VIEW = 0;
	private static final int EXERCISE_VIEW = 1;

	private static final int ACTIVITY_VIEW = 2;
	private static final int ACTIVITY_CREATE = 3;

	private TextView mNameText;
	private TextView mDescriptionText;
	private Long mRowId;

	 //private SimpleCursorAdapter adapter;
	private ListAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_options);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		mNameText = (TextView) findViewById(R.id.name);
		mDescriptionText = (TextView) findViewById(R.id.description);

		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(WorkoutRoutineTable.COLUMN_ID) : null;
		}

		
		 /*String[] uiBindFrom = { ExerciseTable.COLUMN_NAME }; 
		 int[] uiBindTo = { R.id.text1 };
		 
		 adapter = new SimpleCursorAdapter(getApplicationContext(),
		 R.layout.exercises_row, null, uiBindFrom, uiBindTo,
		0);*/
		 
		adapter = new ListAdapter(getApplicationContext(), null, 0);
		final Intent i = new Intent(this, RecordExerciseActivity.class);
		final ListView lv = (ListView) findViewById(android.R.id.list);

		lv.setAdapter(adapter);
		lv.setEmptyView(findViewById(android.R.id.empty));

		lv.setClickable(true);
		/*
		 * lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		 * position, long id) { String[] projection = {ExercisesColumns.NAME,
		 * WRExercisesColumns.COLUMN_ID, WRExercisesColumns.COLUMN_EXERCISE_ID};
		 * Cursor exercise_id =
		 * getContentResolver().query(WRExercises.buildWRExerciseIdUri(""+id),
		 * projection, null, null, null);
		 * 
		 * String name = ""; Long e_id = (long) -1; if (exercise_id != null &&
		 * exercise_id.moveToFirst()) { name = exercise_id.getString(exercise_id
		 * .getColumnIndexOrThrow(ExerciseTable.COLUMN_NAME)); E e_id =
		 * exercise_id .getLong(exercise_id
		 * .getColumnIndexOrThrow(WorkoutRoutineExerciseTable
		 * .COLUMN_EXERCISE_ID));
		 * 
		 * } exercise_id.close();
		 * 
		 * i.putExtra(WorkoutRoutineExerciseTable.COLUMN_ID, id);
		 * i.putExtra(ExerciseTable.COLUMN_NAME, name);
		 * i.putExtra(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID, e_id);
		 * startActivityForResult(i, ACTIVITY_VIEW); } });
		 */
		getSupportLoaderManager().initLoader(WR_VIEW, null, this);
		getSupportLoaderManager().initLoader(EXERCISE_VIEW, null, this);

		// suggested by alex.
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		registerForContextMenu(lv);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG)
			Log.v(TAG, "+ ON PAUSE +");
		// getSupportLoaderManager().restartLoader(EXERCISE_VIEW, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
		getSupportLoaderManager().restartLoader(EXERCISE_VIEW, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// suggested by alex.
		// changed options menu to XML
		getSupportMenuInflater()
				.inflate(R.menu.workout_view_options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// suggested by alex.
		// changed options menu to XML
		switch (item.getItemId()) {
		case R.id.add_exercise:
			createWorkoutExercise();
			return true;
		case R.id.home:
			startActivity(new Intent(this, WRListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.wr_exercise_context_menu, menu);
	}

	@Override
	// Change to View Exercise, Add Record, Delete Exercise
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.add_record:
			final Intent i = new Intent(this, RecordExerciseEditActivity.class);
			String[] projection = { ExercisesColumns.NAME,
					WRExercisesColumns.COLUMN_ID,
					WRExercisesColumns.COLUMN_EXERCISE_ID };
			Cursor exercise_id = getContentResolver().query(
					WRExercises.buildWRExerciseIdUri("" + info.id), projection,
					null, null, null);
			String name = "";
			Long e_id = (long) -1;
			if (exercise_id != null && exercise_id.moveToFirst()) {
				name = exercise_id.getString(exercise_id
						.getColumnIndexOrThrow(ExerciseTable.COLUMN_NAME));

				e_id = exercise_id
						.getLong(exercise_id
								.getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID));

			}
			exercise_id.close();
			i.putExtra("WRE ID", info.id);
			i.putExtra("Exercise ID", e_id);
			startActivityForResult(i, ACTIVITY_CREATE);
			return true;
		case R.id.view_records:
			final Intent in = new Intent(this, RecordCalendarActivity.class);
			startActivityForResult(in, ACTIVITY_VIEW);
			return true;
		case R.id.menu_delete:
			// mDbAdapter.deleteExerciseFromWorkout(info.id);
			getContentResolver().delete(
					WRExercises.buildWRExerciseIdUri("" + info.id), null, null);
			getSupportLoaderManager().restartLoader(EXERCISE_VIEW, null, this);
			// mDbAdapter.deleteRecordsByWRE(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void createWorkoutExercise() {
		Intent i = new Intent(this, WRExerciseEditActivity.class);
		i.putExtra(WorkoutRoutineTable.COLUMN_ID, mRowId);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/*
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { super.onListItemClick(l, v, position, id); Cursor
	 * exercise_id = mDbAdapter.fetchExercisebyWRE(id); for (String col :
	 * exercise_id.getColumnNames()) Log.v(TAG, col);
	 * 
	 * String name = exercise_id.getString(exercise_id
	 * .getColumnIndexOrThrow(ExerciseTable.COLUMN_NAME));
	 * 
	 * long e_id = exercise_id .getLong(exercise_id
	 * .getColumnIndexOrThrow(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID));
	 * Intent i = new Intent(this, ExerciseRecordUI.class);
	 * i.putExtra(WorkoutRoutineExerciseTable.COLUMN_ID, id);
	 * i.putExtra(ExerciseTable.COLUMN_NAME, name);
	 * i.putExtra(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID, e_id);
	 * exercise_id.close(); startActivityForResult(i, ACTIVITY_VIEW); }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cur;
		Log.v(TAG, "id on onCreateLoader: " + id);
		switch (id) {
		case WR_VIEW:
			String[] projection = { WorkoutRoutineTable.COLUMN_ID,
					WorkoutRoutineTable.COLUMN_NAME,
					WorkoutRoutineTable.COLUMN_DESCRIPTION };
			cur = new CursorLoader(this,
					Workouts.buildWorkoutIdUri("" + mRowId), projection, null,
					null, null);
			Log.d(TAG, "WRVIEW");
			break;
		default:
			Log.d(TAG, "DEFAULT");
			//String[] columns = { ExercisesColumns.NAME,
					//WRExercisesColumns.COLUMN_ID };
			String[] columns = { ExercisesColumns.NAME, WRExercisesColumns.COLUMN_ID,  
					ExercisesColumns.CATEGORY, RecordsColumns.COLUMN_VALUE1, RecordsColumns.COLUMN_VALUE2, RecordsColumns.COLUMN_VALUE3 };
			cur = new CursorLoader(this, Workouts.buildWorkoutIdExerciseRecordsUri(""
					+ mRowId), columns, null, null, null);

			break;
			//Select Exercises.Name, WRExercises.ID, RecordTable.value1 RecordTable.value2
			//from 
		}
		return cur;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int id = loader.getId();
		Log.v(TAG, "loader id = " + id);
		switch (id) {
		case WR_VIEW:
			if (cursor != null && cursor.moveToFirst()) {
				mNameText
						.setText(cursor.getString(cursor
								.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
				mDescriptionText
						.setText(cursor.getString(cursor
								.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
				Log.d(TAG, mNameText.getText() + "");
				Log.d(TAG, mDescriptionText.getText() + "");
				// suggested by alex
				getSupportActionBar().setTitle(mNameText.getText());
				if (!TextUtils.isEmpty(mDescriptionText.getText())) {
					getSupportActionBar().setSubtitle(
							mDescriptionText.getText());
				}

			}
			// cursor.close();
			break;
		// Log.v(TAG, "loader id in WR_VIEW = " +id);
		default:
			// Log.d(TAG, "swapCursor");
			// Log.v(TAG, "loader id in Exercise_VIEW = " +id);
			adapter.swapCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();

		switch (id) {
		case WR_VIEW:
			break;
		default:
			adapter.swapCursor(null);
			break;
		}
	}

	private class ListAdapter extends CursorAdapter {

		private LayoutInflater mInflater;
		private Context mContext;

		/*
		 * private int getItemViewType(Cursor cursor) { String type =
		 * cursor.getString
		 * (cursor.getColumnIndex(ExerciseTable.COLUMN_CATEGORY)); if
		 * (type.equals("Cardio")) { return 0; } else if
		 * (type.equals("Strength Training")) { return 1; } else { return 2; } }
		 * 
		 * @Override public int getItemViewType(int position) { Cursor cursor =
		 * (Cursor) getItem(position); return getItemViewType(cursor); }
		 */

		public ListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		/*
		 * @Override public View getView(int arg0, View arg1, ViewGroup arg2) {
		 * // TODO Auto-generated method stub return null; }
		 */

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			if (cursor.getString(
					cursor.getColumnIndex(ExerciseTable.COLUMN_CATEGORY))
					.equals("Cardio")) {
				holder.textView1.setText(cursor.getString(cursor
						.getColumnIndex(ExerciseTable.COLUMN_NAME)));
				// need to make a query that gets exercise attributes and compares number to know which to set vs hardcoding it in
				String s1 = cursor.getString(cursor
						.getColumnIndex(RecordTable.COLUMN_VALUE1));
				String s2 = cursor.getString(cursor
						.getColumnIndex(RecordTable.COLUMN_VALUE2));
				if (s1 != null && s2 != null) {
					holder.textView2.setText(cursor.getString(cursor
							.getColumnIndex(RecordTable.COLUMN_VALUE1)));
					holder.textView3.setText(cursor.getString(cursor
							.getColumnIndex(RecordTable.COLUMN_VALUE2)));
				}
			} else {
				holder.textView1.setText(cursor.getString(cursor
						.getColumnIndex(ExerciseTable.COLUMN_NAME)));
			}

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			View v = null;
			if (cursor.getString(
					cursor.getColumnIndex(ExerciseTable.COLUMN_CATEGORY))
					.equals("Cardio")) {
				v = mInflater.inflate(R.layout.exercises_row1, parent, false);
				holder.textView1 = (TextView) v.findViewById(R.id.text1);
				holder.textView2 = (TextView) v.findViewById(R.id.text2);
				holder.textView3 = (TextView) v.findViewById(R.id.text3);
			} else {
				v = mInflater.inflate(R.layout.exercises_row, parent, false);
				holder.textView1 = (TextView) v.findViewById(R.id.text1);
			}

			v.setTag(holder);
			return v;
		}

		@Override
		public Cursor swapCursor(Cursor cursor) {
			// Create our indexer
			return super.swapCursor(cursor);
		}

	}

	public static class ViewHolder {
		public TextView textView1;
		public TextView textView2;
		public TextView textView3;
		public TextView textView4;

	}

}