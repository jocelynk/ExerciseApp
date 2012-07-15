package com.jocelyn.exerciseapp;

import com.actionbarsherlock.app.SherlockFragment;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Exercises;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.WRExercises;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExerciseListTab extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TAG = "WRExerciseEditActivity";
	private static final boolean DEBUG = true;
	static final int CANCEL_DIALOG_ID = 1;
	private static final int CATEGORY = 2;
	private static final int EXERCISE = 3;
	
	private Long wRowId;
	private Long eRowId;
	
	private SimpleCursorAdapter adapter;
	private Button confirmButton;
	private Button cancelButton;
	private Spinner s1;
	private Spinner s2;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_exercise_to_workout, container, false);
		
		confirmButton = (Button) view.findViewById(R.id.confirm);
		cancelButton = (Button) view.findViewById(R.id.cancel);
		s1 = (Spinner) view.findViewById(R.id.spinner1);
		s2 = (Spinner) view.findViewById(R.id.spinner2);
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				save();
				getActivity();
				getActivity().setResult(FragmentActivity.RESULT_OK);
				// pops activity off the "back stack" and destroys it. resumes the next activity on the back stack.
				getActivity().finish();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getActivity().showDialog(CANCEL_DIALOG_ID);
			}
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.add_exercise_to_workout);

		if (DEBUG) Log.v(TAG, "+++ ON CREATE +++");
		fillSpinnerCategory();
		
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
	
	/*@Override
	protected DialogFragment onCreateDialog(int id) {
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

	}*/

	private void fillSpinnerCategory() {
		if(getLoaderManager().getLoader(CATEGORY) != null)
			getLoaderManager().restartLoader(CATEGORY, null, this);
		else
			getLoaderManager().initLoader(CATEGORY, null, this);
		// create an array to specify which fields we want to display
		String[] from = new String[] { ExerciseTable.COLUMN_CATEGORY };
		// create an array of the display item we want to bind our data to
		int[] to = new int[] { android.R.id.text1 };
		// create simple cursor adapter
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null, from, to, 0);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// get reference to our spinner
		
		s1.setAdapter(adapter);
		s1.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
		
	}
	
	private void fillSpinnerExercise(String category) {
		Bundle arg = new Bundle();
		arg.putString("category", category);
		if(getLoaderManager().getLoader(EXERCISE) != null)
			getLoaderManager().restartLoader(EXERCISE, arg, this);
		else
			getLoaderManager().initLoader(EXERCISE, arg, this);
		Log.v(TAG, "aosijnfgapoiwejnhgfa[poiwejf " + this);
		String[] from = new String[] { ExerciseTable.COLUMN_NAME };
		int[] to = new int[] { android.R.id.text1 };
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null, from, to, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter);
		s2.setOnItemSelectedListener(new MyOnItemSelectedListener());

	}

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if(parent.getId() == R.id.spinner1) {			
				Cursor c = (Cursor) parent.getAdapter().getItem(pos); 
				fillSpinnerExercise(c.getString(c.getColumnIndex(ExerciseTable.COLUMN_CATEGORY)));
				Bundle arg = new Bundle();
				arg.putString("category", c.getString(c.getColumnIndex(ExerciseTable.COLUMN_CATEGORY)));
			} else if(parent.getId() == R.id.spinner2) {
				eRowId = parent.getSelectedItemId();
			}
		}

	
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG) Log.v(TAG, "ON SAVED INSTANCE STATE");
		outState.putSerializable(WorkoutRoutineTable.COLUMN_ID, wRowId);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (DEBUG) Log.v(TAG, "+ ON PAUSE +");
		fillSpinnerCategory();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(DEBUG) Log.v(TAG, "+ ON RESUME +");
		fillSpinnerCategory();
	}

	private void save() {
		ContentValues values = new ContentValues();
		values.put(WorkoutRoutineExerciseTable.COLUMN_WORKOUT_ID, wRowId);
		values.put(WorkoutRoutineExerciseTable.COLUMN_EXERCISE_ID, eRowId);
		getActivity().getContentResolver().insert(WRExercises.CONTENT_URI, values);
		
		Context context = getActivity().getApplicationContext();
		CharSequence text = "Your Exercise has been added.";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
		CursorLoader c;
		
		switch(id) {
		case CATEGORY:
			String[] projection = {ExerciseTable.COLUMN_ID, ExerciseTable.COLUMN_CATEGORY};
			c = new CursorLoader(getActivity(), Uri.withAppendedPath(Exercises.CONTENT_URI, "distinct"), projection, null, null, ExerciseTable.COLUMN_CATEGORY );
			break;
		default:
			String[] projection2 = {ExerciseTable.COLUMN_ID, ExerciseTable.COLUMN_NAME};
			String selection = ExerciseTable.COLUMN_CATEGORY + " IS ? ";
			c = new CursorLoader(getActivity(), Exercises.CONTENT_URI, projection2, selection, new String[] {arg.getString("category")}, null );
			break;
		}	
		return c;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int id = loader.getId();
		switch(id){
		case CATEGORY:
			adapter.swapCursor(cursor);
			break;
		default:
			adapter.swapCursor(cursor);
			break;
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		switch(id){
		case CATEGORY:
			adapter.swapCursor(null);
			break;
		default:
			adapter.swapCursor(null);
			break;
		}
		
	}
	
	/*public static class MyAlertDialogFragment extends DialogFragment {

	    public static MyAlertDialogFragment newInstance(int title) {
	        MyAlertDialogFragment frag = new MyAlertDialogFragment();
	       
	        return frag;
	    }

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        int title = getArguments().getInt("title");

	        return new AlertDialog.Builder(getActivity()).setMessage("Are you sure you want to cancel?")
	                .setPositiveButton("YES",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((FragmentAlertDialog)getActivity()).doPositiveClick();
	                        }
	                    }
	                )
	                .setNegativeButton("NO",
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            ((FragmentAlertDialog)getActivity()).doNegativeClick();
	                        }
	                    }
	                )
	                .create();
	    }
	}
	
	
	

	void showDialog() {
	    DialogFragment newFragment = MyAlertDialogFragment.newInstance(
	            R.string.alert_dialog_two_buttons_title);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
	    // Do stuff here.
	    Log.i("FragmentAlertDialog", "Positive click!");
	}

	public void doNegativeClick() {
	    // Do stuff here.
	    Log.i("FragmentAlertDialog", "Negative click!");
	}*/
}
