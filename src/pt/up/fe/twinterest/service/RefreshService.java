package pt.up.fe.twinterest.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.content.TweetContract;
import pt.up.fe.twinterest.content.TweetContract.MediaContract;
import pt.up.fe.twinterest.util.Interest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class RefreshService extends Service {
	private static final String LOG_TAG = RefreshService.class.getSimpleName();
	
	private RequestQueue mRequestQueue;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mRequestQueue = Volley.newRequestQueue(getBaseContext());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, final int startId) {
		final Interest interest = Interest.getInstance(getBaseContext());
		
		mRequestQueue.add(
			new JsonObjectRequest(
				interest.getRefreshUrl(),
				null,
				new TweetResponseHandler(startId, interest),
				new TweetResponseErrorHandler(startId)
			)
		);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mRequestQueue.stop();
	}
	
	/**
	 * Handles twitter API response by storing all tweets and their media.
	 */
	private class TweetResponseHandler implements Response.Listener<JSONObject> {
		private int mStartId;
		private Interest mInterest;
		
		public TweetResponseHandler(int startId, Interest interest) {
			mStartId = startId;
			mInterest = interest;
		}
		
		@Override
		public void onResponse(JSONObject response) {
			try {
				String refreshUrl = response.getString("refresh_url");
				mInterest.setRefreshUrl(getBaseContext(), refreshUrl);
				
				JSONArray results = response.getJSONArray("results");
				for(int i = 0; i < results.length(); i++) {
					JSONObject tweet = results.getJSONObject(i);
					
					ContentValues values = new ContentValues();
					values.put(TweetContract.Columns._ID, tweet.getLong("id"));
					values.put(TweetContract.Columns.TEXT, tweet.getString("text"));
					values.put(TweetContract.Columns.FROM_USER, tweet.getString("from_user"));
					values.put(TweetContract.Columns.FROM_USER_NAME, tweet.getString("from_user_name"));
					values.put(TweetContract.Columns.PROFILE_IMAGE_URL, tweet.getString("profile_image_url"));
					
					getContentResolver().insert(TweetContract.CONTENT_URI, values);
					
					if(tweet.has("entities")) {
						JSONObject entities = tweet.getJSONObject("entities");
						if(entities.has("media")) {
							JSONArray allMedia = entities.getJSONArray("media");
							for(int j = 0; j < allMedia.length(); j++) {
								JSONObject media = allMedia.getJSONObject(j);
								
								values.clear();
								values.put(MediaContract.Columns.TWEET_ID, tweet.getLong("id"));
								values.put(MediaContract.Columns.MEDIA_URL, media.getString("media_url_https"));
								
								getContentResolver().insert(MediaContract.CONTENT_URI, values);
							}
						}
					}
				}
			} catch(JSONException e) {
				Log.w(LOG_TAG, e);
				Toast.makeText(getBaseContext(), R.string.toast_invalid_response, Toast.LENGTH_LONG).show();
			}
			stopSelf(mStartId);
		}
	}
	
	/**
	 * Handles twitter API errors by notifying the user. 
	 */
	private class TweetResponseErrorHandler implements Response.ErrorListener {
		private int mStartId;
		
		public TweetResponseErrorHandler(int startId) {
			mStartId = startId;
		}
		
		@Override
		public void onErrorResponse(VolleyError error) {
			Toast.makeText(getBaseContext(), R.string.toast_refresh_failed, Toast.LENGTH_LONG).show();
			stopSelf(mStartId);
		}
	}
}
