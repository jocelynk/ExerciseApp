package com.jocelyn.exerciseapp;


import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.data.RecordTable;
import com.jocelyn.exerciseapp.data.WorkoutRoutineExerciseTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Records;

public class RecordExerciseEditActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "RecordExerciseEdit";
	private static final boolean DEBUG = true;

	private EditText mHourText;
	private EditText mMinuteText;
	private EditText mSecondText;
	private EditText mValueText;
	private EditText mSetText;
	private EditText mRepText;
	private EditText mWeightText;
	private EditText mDescription;
	private Long wreRowId;
	private Long eRowId;
	private String date;
	private Button confirmButton;
	private Button cancelButton;
	private Cursor c;
	private boolean saved = false;

	// for the date picker

	private int mYear;
	private int mMonth;
	private int mDay;

	private TextView mDateDisplay;
	private Button mPickDate;

	static final int DATE_DIALOG_ID = 0;
	static final int CANCEL_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (DEBUG)
			Log.v(TAG, "+++ ON CREATE +++");

		wreRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable("WRE ID");
		if (wreRowId == null) {
			Bundle extras = getIntent().getExtras();
			wreRowId = extras != null ? extras
					.getLong("WRE ID") : null;
		}
		Log.v(TAG, "wreRowID = " + wreRowId);

		eRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable("Exercise ID");
		if (eRowId == null) {
			Bundle extras = getIntent().getExtras();
			eRowId = extras != null ? extras.getLong("Exercise ID")
					: null;
		}
		
		// suggested by alex.
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);


		Log.v(TAG, "Exercise id: " + eRowId);
		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3:
			setContentView(R.layout.cardio_edit);
			mHourText = (EditText) findViewById(R.id.hr);
			mMinuteText = (EditText) findViewById(R.id.min);
			mSecondText = (EditText) findViewById(R.id.sec);
			mValueText = (EditText) findViewById(R.id.value);
			confirmButton = (Button) findViewById(R.id.confirm);
			mDescription = (EditText) findViewById(R.id.description);
			cancelButton = (Button) findViewById(R.id.cancel);
			break;

		}

		// populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();
				if (saved) {
					setResult(RESULT_OK);
					finish();
				} else {
					Toast.makeText(

					getApplicationContext(), "Please fill in fields.",
							Toast.LENGTH_LONG).show();

				}
			}

		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(CANCEL_DIALOG_ID);
			}
		});

		mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
		mPickDate = (Button) findViewById(R.id.pickDate);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		//in previous app, sets date of chosen day on calendar
		//ComponentName activity = getCallingActivity();
		//String act = activity != null ? activity.getClassName() : "other view";
		//if (act.equals("com.team03.fitsup.ui.ExerciseRecordUI")) {
			// get today's date
			final Calendar cal = Calendar.getInstance();
			mYear = cal.get(Calendar.YEAR);
			Log.v(TAG, "" + mYear);
			mMonth = cal.get(Calendar.MONTH);
			Log.v(TAG, "" + mMonth);
			mDay = cal.get(Calendar.DAY_OF_MONTH);
			Log.v(TAG, "" + mDay);
		/*} else {
			String[] temp = date.split("-");
			mYear = Integer.parseInt(temp[2]);
			mMonth = Integer.parseInt(temp[0]) - 1;
			mDay = Integer.parseInt(temp[1]);
		}*/

		// display the current date
		updateDisplay();
	}
	
	private void updateDisplay() {
		this.mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear));
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	// date picker helper method

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (DEBUG)
			Log.v(TAG, "ON SAVED INSTANCE STATE");

		saveState();
		outState.putSerializable(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG)
			Log.v(TAG, "+ ON PAUSE +");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "+ ON RESUME +");
	}

	private void saveState() { // need to write exceptions for formatting errors
		// switch case statement - later need to get exercise id //create SQL
		// query or reuse another
		String date = mDateDisplay.getText().toString();

		Context context = getApplicationContext();
		CharSequence text = "Your Record has been saved.";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		switch (eRowId.intValue()) {
		case 1:
		case 2:
		case 3: // later need to have individual cases for all
			String hour = mHourText.getText().toString();
			String minute = mMinuteText.getText().toString();
			String second = mSecondText.getText().toString();
			String value = mValueText.getText().toString(); // distance
			String des = mDescription.getText().toString().trim();

			double hr = !TextUtils.isEmpty(hour) ? Double.parseDouble(hour) : 0;
			double min = !TextUtils.isEmpty(minute) ? Double
					.parseDouble(minute) : 0;
			double sec = !TextUtils.isEmpty(second) ? Double
					.parseDouble(second) : 0;

			double savedTime = (hr == 0.0 && min == 0.0 && sec == 0.0) ? -1
					: ((hr * 60.0) + min + (sec / 60.0));
			double val = !TextUtils.isEmpty(value) ? Double.parseDouble(value)
					: -1;
			String description = TextUtils.isEmpty(des) ? null : des;
			// time
			/*if (eRowId.intValue() == 1) {
				c = mDbAdapter.fetchRecord(date, 1, wreRowId);
			} else if (eRowId.intValue() == 2) {
				c = mDbAdapter.fetchRecord(date, 3, wreRowId);

			} else if (eRowId.intValue() == 3) {
				c = mDbAdapter.fetchRecord(date, 5, wreRowId);

			}	*/
			//if (c != null && c.getCount() > 0) {
				/*if (savedTime != -1 && val != -1) {
					if (eRowId.intValue() == 1) {
						mDbAdapter.updateRecord(date, savedTime, 1, wreRowId,
								description);
						mDbAdapter.updateRecord(date, val, 2, wreRowId, null);
						saved = true;
						toast.show();

					} else if (eRowId.intValue() == 2) {
						mDbAdapter.updateRecord(date, savedTime, 3, wreRowId,
								description);
						mDbAdapter.updateRecord(date, val, 4, wreRowId, null);
						saved = true;
						toast.show();

					} else if (eRowId.intValue() == 3) {
						mDbAdapter.updateRecord(date, savedTime, 5, wreRowId,
								description);
						mDbAdapter.updateRecord(date, val, 6, wreRowId, null);
						saved = true;
						toast.show();

					}
				}*/
			//} else {
				if (savedTime != -1 && val != -1) {
					ContentValues values1 = new ContentValues();
					ContentValues values2 = new ContentValues();
					if (eRowId.intValue() == 1) {
						// time
						
						values1.put(RecordTable.COLUMN_DATE, date);
						values1.put(RecordTable.COLUMN_VALUE, savedTime);
						values1.put(RecordTable.COLUMN_DESCRIPTION, description);
						values1.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values1.put(RecordTable.COLUMN_E_ATTR_ID, 1);
				        getContentResolver().insert(Records.CONTENT_URI, values1);
						
				        //mDbAdapter.createRecord(date, savedTime, 1, wreRowId,description);
						// distance
				        values2.put(RecordTable.COLUMN_DATE, date);
						values2.put(RecordTable.COLUMN_VALUE, val);
						values2.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values2.put(RecordTable.COLUMN_DESCRIPTION, "");
						values2.put(RecordTable.COLUMN_E_ATTR_ID, 2);
				        getContentResolver().insert(Records.CONTENT_URI, values2);
						//mDbAdapter.createRecord(date, val, 2, wreRowId, null);
						toast.show();
						saved = true;
					} else if (eRowId.intValue() == 2) {
						// time
						values1.put(RecordTable.COLUMN_DATE, date);
						values1.put(RecordTable.COLUMN_VALUE, savedTime);
						values1.put(RecordTable.COLUMN_DESCRIPTION, description);
						values1.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values1.put(RecordTable.COLUMN_E_ATTR_ID, 3);
				        getContentResolver().insert(Records.CONTENT_URI, values1);
						//mDbAdapter.createRecord(date, savedTime, 3, wreRowId, description);
						// distance
				        values2.put(RecordTable.COLUMN_DATE, date);
						values2.put(RecordTable.COLUMN_VALUE, val);
						values2.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values2.put(RecordTable.COLUMN_DESCRIPTION, "");
						values2.put(RecordTable.COLUMN_E_ATTR_ID, 4);
				        getContentResolver().insert(Records.CONTENT_URI, values2);
						//mDbAdapter.createRecord(date, val, 4, wreRowId, null);
						toast.show();
						saved = true;

					} else if (eRowId.intValue() == 3) {
						// time
						values1.put(RecordTable.COLUMN_DATE, date);
						values1.put(RecordTable.COLUMN_VALUE, savedTime);
						values1.put(RecordTable.COLUMN_DESCRIPTION, description);
						values1.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values1.put(RecordTable.COLUMN_E_ATTR_ID, 5);
				        getContentResolver().insert(Records.CONTENT_URI, values1);
						//mDbAdapter.createRecord(date, savedTime, 5, wreRowId, description);
						// distance
				        values2.put(RecordTable.COLUMN_DATE, date);
						values2.put(RecordTable.COLUMN_VALUE, val);
						values2.put(RecordTable.COLUMN_WRKT_RTNE_E_ID, wreRowId);
						values2.put(RecordTable.COLUMN_DESCRIPTION, "");
						values2.put(RecordTable.COLUMN_E_ATTR_ID, 6);
				        getContentResolver().insert(Records.CONTENT_URI, values2);
						//mDbAdapter.createRecord(date, val, 6, wreRowId, null);
						toast.show();
						saved = true;

					}

				//}
			}
			//c.close();
			break;

		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
