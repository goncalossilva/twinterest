package pt.up.fe.twinterest.fragment;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.content.TweetContract;
import pt.up.fe.twinterest.content.TweetContract.MediaContract;
import pt.up.fe.twinterest.util.Const;
import pt.up.fe.twinterest.util.LruBitmapCache;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class TweetDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int TWEET_LOADER_ID = 0;
	private static final int MEDIA_LOADER_ID = 1;
	
	private static final String[] TWEET_PROJECTION = new String[] { 
		TweetContract.Columns._ID,
		TweetContract.Columns.TEXT,
		TweetContract.Columns.FROM_USER,
		TweetContract.Columns.FROM_USER_NAME,
		TweetContract.Columns.PROFILE_IMAGE_URL};
	
	private static final String[] MEDIA_PROJECTION = new String[] {
		MediaContract.Columns.TWEET_ID,
		MediaContract.Columns.MEDIA_URL};
	
	private long mId;
	
	private ImageView mProfileImageView;
	private TextView mFromUserView;
	private TextView mFromUserNameView;
	private TextView mText;
	private LinearLayout mMediaLayout;
	
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	
	public TweetDetailFragment() { }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mRequestQueue = Volley.newRequestQueue(activity);
		mImageLoader = new ImageLoader(mRequestQueue, LruBitmapCache.getInstance(activity));
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments().containsKey(Const.ARG_ITEM_ID)) {
			mId = getArguments().getLong(Const.ARG_ITEM_ID);
			
			getLoaderManager().initLoader(TWEET_LOADER_ID, null, this);
			getLoaderManager().initLoader(MEDIA_LOADER_ID, null, this);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tweet_detail, container, false);
		
		mProfileImageView = (ImageView)view.findViewById(R.id.profile_image);
		mFromUserView = (TextView)view.findViewById(R.id.from_user);
		mFromUserNameView = (TextView)view.findViewById(R.id.from_user_name);
		mText = (TextView)view.findViewById(R.id.text);
		mMediaLayout = (LinearLayout)view.findViewById(R.id.media);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case TWEET_LOADER_ID:
				return new CursorLoader(
						getActivity(),
						Uri.withAppendedPath(TweetContract.CONTENT_URI, Long.toString(mId)),
						TWEET_PROJECTION,
						null,
						null,
						null);
				
			case MEDIA_LOADER_ID:
				return new CursorLoader(
						getActivity(),
						Uri.withAppendedPath(MediaContract.CONTENT_URI, Long.toString(mId)),
						MEDIA_PROJECTION,
						null,
						null,
						null);
			
			default:
				return null;
		}
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data.moveToFirst()) {
			switch(loader.getId()) {
				case TWEET_LOADER_ID:
					mImageLoader.get(
							data.getString(data.getColumnIndexOrThrow(TweetContract.Columns.PROFILE_IMAGE_URL)),
							ImageLoader.getImageListener(mProfileImageView, 0, 0));
					mFromUserView.setText(
							data.getString(data.getColumnIndexOrThrow(TweetContract.Columns.FROM_USER)));
					mFromUserNameView.setText(
							data.getString(data.getColumnIndexOrThrow(TweetContract.Columns.FROM_USER_NAME)));
					mText.setText(
							data.getString(data.getColumnIndexOrThrow(TweetContract.Columns.TEXT)));
					break;
					
				case MEDIA_LOADER_ID:
					do {
						ImageView mediaImage = new ImageView(getActivity());
						
						mMediaLayout.addView(
								mediaImage,
								new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.WRAP_CONTENT,
										LinearLayout.LayoutParams.WRAP_CONTENT));
						
						mImageLoader.get(
								data.getString(data.getColumnIndexOrThrow(MediaContract.Columns.MEDIA_URL)),
								ImageLoader.getImageListener(mediaImage, 0, 0));
					} while(data.moveToNext());
					break;
					
				default:
					break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()) {
			case TWEET_LOADER_ID:
				mProfileImageView.setImageBitmap(null);
				mFromUserView.setText(null);
				mFromUserNameView.setText(null);
				mText.setText(null);
				break;
				
			case MEDIA_LOADER_ID:
				mMediaLayout.removeAllViews();
				break;
				
			default:
				break;
		}
	}
}
