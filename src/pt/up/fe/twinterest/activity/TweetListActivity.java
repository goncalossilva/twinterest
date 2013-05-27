package pt.up.fe.twinterest.activity;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.content.TweetContract;
import pt.up.fe.twinterest.content.TweetContract.MediaContract;
import pt.up.fe.twinterest.fragment.TweetDetailFragment;
import pt.up.fe.twinterest.fragment.TweetListFragment;
import pt.up.fe.twinterest.service.RefreshService;
import pt.up.fe.twinterest.util.Const;
import pt.up.fe.twinterest.util.Interest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

public class TweetListActivity extends FragmentActivity implements TweetListFragment.Callback {
	 // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	private boolean mTwoPane;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tweet_list);
		
		if(findViewById(R.id.tweet_detail_container) != null) {
			// The detail container view will be present only in the large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the activity should be in two-pane mode.
			mTwoPane = true;
			
			// In two-pane mode, list items should be given the 'activated' state when touched.
			((TweetListFragment)getSupportFragmentManager().findFragmentById(R.id.tweet_list))
					.setActivateOnItemClick(true);
		}
		
		// Parse content sent from other apps.
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if(Intent.ACTION_SEND.equals(action) && "text/plain".equals(type))
			updateInterestKeyword(intent.getStringExtra(Intent.EXTRA_TEXT));
	}
	
	@Override
	public void onItemSelected(long id) {
		if(mTwoPane) {
			// In two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using
			// a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putLong(Const.ARG_ITEM_ID, id);
			TweetDetailFragment fragment = new TweetDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.tweet_detail_container, fragment).commit();
			
		}
		else {
			// In single-pane mode, simply start the detail activity for the selected item ID.
			Intent detailIntent = new Intent(this, TweetDetailActivity.class);
			detailIntent.putExtra(Const.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	@Override
	public void refresh() {
		Interest interest = Interest.getInstance(this);
		
		if(!TextUtils.isEmpty(interest.getKeyword()))
			startService(new Intent(this, RefreshService.class));
		else
			Toast.makeText(this, R.string.toast_no_interest, Toast.LENGTH_LONG).show();
	}

	@Override
	public void updateInterestKeyword(String newKeyword) {
		Interest interest = Interest.getInstance(this);
		
		if(!newKeyword.equals(interest.getKeyword())) {
			interest.setKeyword(this, newKeyword);
			
			getContentResolver().delete(TweetContract.CONTENT_URI, null, null);
			getContentResolver().delete(MediaContract.CONTENT_URI, null, null);
		
			Toast.makeText(this, R.string.toast_interest_updated, Toast.LENGTH_SHORT).show();
			
			refresh();
		}
	}
}
