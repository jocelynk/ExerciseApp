package com.jocelyn.exerciseapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jocelyn.exerciseapp.data.ExerciseTable;
import com.jocelyn.exerciseapp.provider.ExerciseAppManager.Exercises;


public class ExerciseSearchTab extends SherlockListFragment implements
		OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
	// This is the Adapter being used to display the list's data.
	SimpleCursorAdapter mAdapter;

	// If non-null, this is the current filter the user has provided.
	String mCurFilter;

	//public View onCreateView(LayoutInflater inflater, ViewGroup container,
		//	Bundle savedInstanceState) {
		//if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
		//	return null;
	//	}

		//View view = inflater.inflate(R.layout.search_form, null);

		// Get the SearchView and set the searchable configuration
		/*SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) view
				.findViewById(R.id.search_view);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getActivity().getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		return inflater.inflate(R.layout.search_form, null);*/

	//}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText("No exercises");

		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);

		String[] uiBindFrom = { ExerciseTable.COLUMN_NAME };
		int[] uiBindTo = { R.id.text1 };

		mAdapter = new SimpleCursorAdapter(getActivity()
				.getApplicationContext(), R.layout.exercises_row_search, null,
				uiBindFrom, uiBindTo,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);

	}
	
	 @Override 
	 public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         // Place an action bar item for searching.
         MenuItem item = menu.add("Search");
         item.setIcon(android.R.drawable.ic_menu_search);
         item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
         SearchView sv = new SearchView(getActivity());
         sv.setOnQueryTextListener(this);
         item.setActionView(sv);
     }


	public boolean onQueryTextChange(String newText) {
		// Called when the action bar search text has changed. Update
		// the search filter, and restart the loader to do a new query
		// with this filter.
		mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
		getLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// Don't care about this.
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Insert desired behavior here.
		Log.i("FragmentComplexList", "Item clicked: " + id);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		 Uri baseUri;

		 String select = ExerciseTable.COLUMN_NAME + " LIKE '%" + mCurFilter + "%' OR " + ExerciseTable.COLUMN_CATEGORY + " LIKE '%" + mCurFilter + "%'";
		  if (mCurFilter != null) {
     		return new CursorLoader(getActivity(), Exercises.CONTENT_URI,
     				new String[] {ExerciseTable.COLUMN_NAME, ExerciseTable.COLUMN_ID}, select, null,
     				ExerciseTable.COLUMN_NAME + " COLLATE LOCALIZED ASC");
          } else
          {
        	  return new CursorLoader(getActivity(), Exercises.CONTENT_URI,
       				new String[] {ExerciseTable.COLUMN_NAME, ExerciseTable.COLUMN_ID}, null, null,
       				null);
          }

		
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader, so we don't care about the ID.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);

		 // The list should now be shown.
        if (isResumed()) {
        	Log.v("EXERCISE_SEARCH_TAB", "Is Resumed");
            setListShown(true);
        } else {
        	Log.v("EXERCISES_SEARCH_TAB", "IS not resumed");
            setListShownNoAnimation(true);
        }

	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}
}
