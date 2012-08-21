package com.jocelyn.exerciseapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import com.actionbarsherlock.R;
import com.actionbarsherlock.app.SherlockActivity;


public class RecordCalendarActivity extends SherlockActivity{
	
	protected static final int ACTIVITY_VIEW = 0;
	private int mYear;
    private int mMonth;
    private int mDay;
	private CalendarView cv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_calendar);
		cv = (CalendarView)findViewById(R.id.calview); 
		cv.setOnDateChangeListener(mDateSetListener);
		
	}
	
	 // the callback received when the user "sets" the date in the dialog
    private CalendarView.OnDateChangeListener mDateSetListener =
        new CalendarView.OnDateChangeListener() {
 
        public void onSelectedDayChange(CalendarView view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String selectedDate = new StringBuilder().append(mMonth + 1).append("/").append(mDay).append("/")
            .append(mYear).append(" ").toString();
 
            Intent i = new Intent(getBaseContext(), WRListActivity.class);
			
			startActivityForResult(i, ACTIVITY_VIEW);
        }
    };

}
