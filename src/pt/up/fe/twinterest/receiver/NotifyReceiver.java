package pt.up.fe.twinterest.receiver;

import pt.up.fe.twinterest.service.NotifyService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class NotifyReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		manager.setInexactRepeating(
			AlarmManager.ELAPSED_REALTIME_WAKEUP,
			SystemClock.elapsedRealtime(),
			AlarmManager.INTERVAL_FIFTEEN_MINUTES,
			PendingIntent.getService(
					context,
					0,
					new Intent(context, NotifyService.class),
					0
			)
		);
	}
	
}
