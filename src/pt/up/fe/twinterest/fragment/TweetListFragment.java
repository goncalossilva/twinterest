package pt.up.fe.twinterest.fragment;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.content.TweetContract;
import pt.up.fe.twinterest.util.Interest;
import pt.up.fe.twinterest.util.LruBitmapCache;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class TweetListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	// The saved instance state bundle key representing the activated item position. Only used on tablets.
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	
	private static final String[] PROJECTION = new String[]{
		TweetContract.Columns._ID,
		TweetContract.Columns.TEXT,
		TweetContract.Columns.FROM_USER,
		TweetContract.Columns.PROFILE_IMAGE_URL};
	
	// The fragment's current callback object, which is notified of list item clicks.
	private Callback mCallback = DUMMY_CALLBACK;
	
	 // The current activated item position. Only used on tablets.
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
			
	private SimpleCursorAdapter mAdapter;
	
	/**
	 * A callback interface that all activities containing this fragment must implement.
	 */
	public interface Callback {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(long id);
		
		public void refresh();
		
		public void updateInterestKeyword(String newKeyword);
	}
	
	/**
	 * A dummy implementation of the {@link Callback} interface that does nothing. Used only when this fragment is not
	 * attached to an activity.
	 */
	private static final Callback DUMMY_CALLBACK = new Callback() {
		@Override public void onItemSelected(long id) { }
		@Override public void refresh() { }
		@Override public void updateInterestKeyword(String newKeyword) { }
	};
	
	public TweetListFragment() { }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Activities containing this fragment must implement its callbacks.
		if(!(activity instanceof Callback))
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		
		mCallback = (Callback)activity;
		
		mRequestQueue = Volley.newRequestQueue(activity);
		mImageLoader = new ImageLoader(mRequestQueue, LruBitmapCache.getInstance(activity));
		
		mAdapter = new SimpleCursorAdapter(
				activity,
				R.layout.fragment_tweet_list_item,
				null,
				new String[] { 
					TweetContract.Columns.PROFILE_IMAGE_URL,
					TweetContract.Columns.FROM_USER,
					TweetContract.Columns.TEXT},
				new int[] {
					R.id.profile_image,
					R.id.from_user,
					R.id.text},
				0);
		
		mAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(columnIndex == cursor.getColumnIndex(TweetContract.Columns.PROFILE_IMAGE_URL)) {
					ImageView imageView = (ImageView)view;
					imageView.setImageBitmap(null);
					mImageLoader.get(cursor.getString(columnIndex), ImageLoader.getImageListener(imageView, 0, 0));
					return true;
				}
				
				return false;
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		setEmptyText(getString(R.string.empty_no_tweets));
		setListAdapter(mAdapter);
		
		// Restore the previously serialized activated item position.
		if(savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.menu_tweet_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_refresh:
				mCallback.refresh();
				
				return true;
			
			case R.id.menu_add_interest:
				final Activity activity = getActivity();
				final Interest interest = Interest.getInstance(activity);
				
				String keyword = interest.getKeyword();
				
				final EditText editText = new EditText(activity);
				editText.setText(keyword);
				if(keyword != null)
					editText.setSelection(keyword.length());
				
				new AlertDialog.Builder(activity)
					.setTitle(R.string.dialog_title)
					.setView(editText)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newKeyword = editText.getText().toString();
							
							mCallback.updateInterestKeyword(newKeyword);
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		// Reset the active callbacks interface to the dummy implementation.
		mCallback = DUMMY_CALLBACK;
		
		mRequestQueue.stop();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		// Animate selected item.
		TranslateAnimation anim =
				new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);
		anim.setDuration(150);
		anim.setRepeatCount(1);
		anim.setRepeatMode(Animation.REVERSE);
		view.startAnimation(anim);
		
		// Notify the active callbacks interface (the activity, if attached to one) that an item has been selected.
		mCallback.onItemSelected(id);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when
	 * touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}
	
	private void setActivatedPosition(int position) {
		if(position == ListView.INVALID_POSITION)
			getListView().setItemChecked(mActivatedPosition, false);
		else
			getListView().setItemChecked(position, true);
		
		mActivatedPosition = position;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
				getActivity(),
				TweetContract.CONTENT_URI,
				PROJECTION,
				null,
				null,
				TweetContract.Columns._ID + " DESC");
				
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}
