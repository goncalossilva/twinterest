package pt.up.fe.twinterest.content;

import android.net.Uri;

public final class TweetContract {
	public static final String AUTHORITY = "pt.up.fe.twinterest";
	
	public static final String BASE_PATH = "tweets";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	public static final String TABLE_NAME = "tweets";
	public interface Columns {
		public static final String _ID = "_id";
		public static final String TEXT = "text";
		public static final String FROM_USER = "from_user";
		public static final String FROM_USER_NAME = "from_user_name";
		public static final String PROFILE_IMAGE_URL = "profile_image_url";
	}
	
	public static final class MediaContract {
		public static final String BASE_PATH = TweetContract.BASE_PATH + "/media";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
		public static final String TABLE_NAME = "media";
		public interface Columns {
			public static final String _ID = "_id";
			public static final String TWEET_ID = "tweet_id";
			public static final String MEDIA_URL = "media_url";
		}
	}
}
