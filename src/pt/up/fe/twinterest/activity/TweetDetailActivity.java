package pt.up.fe.twinterest.activity;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.fragment.TweetDetailFragment;
import pt.up.fe.twinterest.util.Const;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * An activity representing a single Tweet detail screen. This activity is only used on handset devices. On tablet-size
 * devices, item details are presented side-by-side with a list of items in a {@link TweetListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than a {@link TweetDetailFragment}.
 */
public class TweetDetailActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_detail);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState == null) {
			// Create the detail fragment and add it to the activity using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putLong(Const.ARG_ITEM_ID, getIntent().getLongExtra(Const.ARG_ITEM_ID, -1));
			TweetDetailFragment fragment = new TweetDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.tweet_detail_container, fragment).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpTo(this, new Intent(this, TweetListActivity.class));
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
