package pt.up.fe.twinterest.content;

import pt.up.fe.twinterest.content.TweetContract.MediaContract;
import pt.up.fe.twinterest.content.db.DbHelper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TweetsProvider extends ContentProvider {
	private static final int TWEETS_PATH_CODE = 1;
	private static final int TWEET_PATH_CODE = 2;
	private static final int MEDIA_PATH_CODE = 3;
	private static final int TWEET_MEDIA_PATH_CODE = 4;
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(TweetContract.AUTHORITY, TweetContract.BASE_PATH, TWEETS_PATH_CODE);
		sUriMatcher.addURI(TweetContract.AUTHORITY, TweetContract.BASE_PATH + "/#", TWEET_PATH_CODE);
		sUriMatcher.addURI(TweetContract.AUTHORITY, MediaContract.BASE_PATH, MEDIA_PATH_CODE);
		sUriMatcher.addURI(TweetContract.AUTHORITY, MediaContract.BASE_PATH + "/#", TWEET_MEDIA_PATH_CODE);
	}
	
	private DbHelper mDbHelper;
	
	@Override
	public boolean onCreate() {
		mDbHelper = new DbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch(sUriMatcher.match(uri)) {
			case TWEETS_PATH_CODE:
				builder.setTables(TweetContract.TABLE_NAME);
				break;
				
			case TWEET_PATH_CODE:
				builder.setTables(TweetContract.TABLE_NAME);
				builder.appendWhere(TweetContract.Columns._ID + "=" + uri.getLastPathSegment());
				break;
				
			case MEDIA_PATH_CODE:
				builder.setTables(MediaContract.TABLE_NAME);
				break;
				
			case TWEET_MEDIA_PATH_CODE:
				builder.setTables(MediaContract.TABLE_NAME);
				builder.appendWhere(MediaContract.Columns.TWEET_ID + "=" + uri.getLastPathSegment());
				break;

			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
			case TWEETS_PATH_CODE:
			case MEDIA_PATH_CODE:
			case TWEET_MEDIA_PATH_CODE:
				return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + uri.toString();
				
			case TWEET_PATH_CODE:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + uri.toString();
				
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		long id;
		switch(sUriMatcher.match(uri)) {
			case TWEETS_PATH_CODE:
				id = db.insert(TweetContract.TABLE_NAME, null, values);
				if(id != -1) {
					getContext().getContentResolver().notifyChange(uri, null);
					
					return Uri.parse(TweetContract.BASE_PATH + "/" + id);
				}
				else {
					return null;
				}
				
			case MEDIA_PATH_CODE:
				id = db.insert(MediaContract.TABLE_NAME, null, values);
				if(id != -1) {
					getContext().getContentResolver().notifyChange(uri, null);
					
					return Uri.parse(TweetContract.BASE_PATH + "/" + id);
				}
				else {
					return null;
				}

			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count;
		
		switch(sUriMatcher.match(uri)) {
			case TWEETS_PATH_CODE:
				count = db.delete(TweetContract.TABLE_NAME, "1", null);
				break;
				
			case MEDIA_PATH_CODE:
				count = db.delete(MediaContract.TABLE_NAME, "1", null);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if(count > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// Nothing ever gets updated.
		return 0;
	}
}
