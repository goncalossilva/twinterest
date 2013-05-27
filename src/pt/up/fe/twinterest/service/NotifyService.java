package pt.up.fe.twinterest.service;

import pt.up.fe.twinterest.R;
import pt.up.fe.twinterest.activity.TweetListActivity;
import pt.up.fe.twinterest.content.TweetContract;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;

public class NotifyService extends RefreshService {
	private static final int NOTIFICATION_ID = 0;
	
	private NotifyContentObserver mObserver = new NotifyContentObserver();
	
	private boolean mNotificationShown = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		getContentResolver().registerContentObserver(TweetContract.CONTENT_URI, true, mObserver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		getContentResolver().unregisterContentObserver(mObserver);
	}
	
	private class NotifyContentObserver extends ContentObserver {
		public NotifyContentObserver() {
			super(new Handler());
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			if(!mNotificationShown) {
				Context context = getBaseContext();
				
				Notification.Builder builder = 
					new Notification.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker(getString(R.string.notification_ticker))
						.setContentTitle(getString(R.string.notification_content_title))
						.setContentText(getString(R.string.notification_content_text))
						.setContentIntent(
							PendingIntent.getActivity(
								getBaseContext(),
								0,
								new Intent(context, TweetListActivity.class),
								0
							)
						);
				
				NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(NOTIFICATION_ID, builder.build());
				
				mNotificationShown = true;
			}
		}
	}
}
