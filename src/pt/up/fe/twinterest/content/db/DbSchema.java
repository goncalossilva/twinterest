package pt.up.fe.twinterest.content.db;

import pt.up.fe.twinterest.content.TweetContract;
import pt.up.fe.twinterest.content.TweetContract.MediaContract;

public class DbSchema {
	public static final String TYPE_INTEGER = "integer";
	public static final String TYPE_STRING = "varchar(256)";
	
	public static DbTable getTweetsTable() {
		String[][] fields =
			{{TweetContract.Columns._ID, 				TYPE_INTEGER},
			{TweetContract.Columns.TEXT,				TYPE_STRING},
			{TweetContract.Columns.FROM_USER,			TYPE_STRING},
			{TweetContract.Columns.FROM_USER_NAME,		TYPE_STRING},
			{TweetContract.Columns.PROFILE_IMAGE_URL,	TYPE_STRING}};
		
		String[] primaryKeys = {TweetContract.Columns._ID};

		return new DbTable(TweetContract.TABLE_NAME, fields, primaryKeys, null);
	}
	
	public static DbTable getMediaTable() {
		String[][] fields =
			{{MediaContract.Columns._ID,		TYPE_INTEGER},
			{MediaContract.Columns.TWEET_ID, 	TYPE_INTEGER},
			{MediaContract.Columns.MEDIA_URL, 	TYPE_STRING}};
		
		String[] primaryKeys = {MediaContract.Columns._ID};
		
		String[][] foreignKeys =
			{{MediaContract.Columns.TWEET_ID, TweetContract.TABLE_NAME, TweetContract.Columns._ID}};
		
		return new DbTable(MediaContract.TABLE_NAME, fields, primaryKeys, foreignKeys);
	}
}