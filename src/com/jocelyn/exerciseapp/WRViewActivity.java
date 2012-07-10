package com.jocelyn.exerciseapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Workouts;
import com.jocelyn.exerciseapp.provider.ExerciseAppProvider;

public class WRViewActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final String TAG = "WRViewActivity";
	private static final boolean DEBUG = true;
	private static final int WR_VIEW = 0;
	private TextView mNameText;
	private TextView mDescriptionText;
	private Long mRowId;

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
		
		getSupportLoaderManager().initLoader(WR_VIEW, null, this);

		
		// suggested by alex.
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//CursorLoader workout = new CursorLoader(this, Uri.withAppendedPath(ExerciseAppProvider.CONTENT_URI, String.valueOf(mRowId)), null, null, null, null);
		CursorLoader workout = new CursorLoader(this, Workouts.buildWorkoutIdUri(""+mRowId), null, null, null, null);

	    return workout;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()) {
			mNameText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_NAME)));
			mDescriptionText.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutRoutineTable.COLUMN_DESCRIPTION)));
			
			// suggested by alex
			getSupportActionBar().setTitle(mNameText.getText());
			if (!TextUtils.isEmpty(mDescriptionText.getText())) {
				getSupportActionBar().setSubtitle(mDescriptionText.getText());
			}
			cursor.close();
			
			
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
