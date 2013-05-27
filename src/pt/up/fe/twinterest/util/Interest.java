package pt.up.fe.twinterest.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class Interest {
	private static final String API_URL = "http://search.twitter.com/search.json";
	private static final String API_BASE_REFRESH_URL = "?include_entities=true&q=%s";
	
	private static final String PREF_NAME = "interest";
	
	private static final String PREF_KEY_KEYWORD = "keyword";
	private static final String PREF_KEY_REFRESH_URL = "refresh_url";
	
	private static Interest sInstance;
	
	public static synchronized Interest getInstance(Context context) {
		if(sInstance == null)
			sInstance = new Interest(context);
		
		return sInstance;
	}
	
	private String mKeyword;
	private String mRefreshUrl;
	
	private Interest(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		mKeyword = prefs.getString(PREF_KEY_KEYWORD, null);
		mRefreshUrl = prefs.getString(PREF_KEY_REFRESH_URL, null);
	}
	
	public String getKeyword() {
		return mKeyword;
	}
	
	public String getRefreshUrl() {
		if(mRefreshUrl == null)
			return API_URL + String.format(API_BASE_REFRESH_URL, Uri.encode(mKeyword));
		else
			return API_URL + mRefreshUrl;
	}
	
	public void setKeyword(Context context, String interest) {
		mKeyword = interest;
		mRefreshUrl = null;
		save(context);
	}
	
	public void setRefreshUrl(Context context, String nextPage) {
		mRefreshUrl = nextPage;
		save(context);
	}
	
	private void save(Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(PREF_KEY_KEYWORD, mKeyword);
		editor.putString(PREF_KEY_REFRESH_URL, mRefreshUrl);
		editor.commit();
	}
}
